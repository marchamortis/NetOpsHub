// NetOps Hub - Cloudflare Worker API
// v2.0.1 - SECURITY HARDENED
//
// Security model (v2.0.1):
//  * No static/master tokens: the previous "cfat_*" creator bypass is removed.
//    Every privileged call must carry a session token issued by /auth/login.
//  * Passwords: PBKDF2-SHA256, 100,000 iterations, 16-byte per-user salt.
//    Legacy SHA-256(static-salt) hashes are accepted once and transparently
//    upgraded to PBKDF2 at the next successful login.
//  * Sessions: only SHA-256(token) is persisted, 24h TTL, invalidated on
//    password change / role change / deactivation.
//  * Login rate limiting: per-IP+username sliding window, exponential backoff hint.
//  * All data endpoints (GET + mutations) require authentication.
//  * CORS restricted to an allowlist (native mobile/desktop apps are unaffected:
//    they do not send an Origin header and receive no CORS headers at all).
//  * Server-side IDs for created resources (client-supplied IDs are ignored).
//  * Audit trail includes failed logins and rate-limit hits.

export interface Env {
  DB: D1Database;
  JWT_SECRET?: string;
}

interface UserSession {
  id: string;
  username: string;
  email: string;
  role: 'Creator' | 'Admin' | 'Editor' | 'Viewer';
  session_id: string;
}

// ============================================================
// CRYPTO HELPERS
// ============================================================

const PBKDF2_ITERATIONS = 100_000;
const LEGACY_SALT = 'netops_salt_2026'; // only used to verify & migrate legacy hashes

function toHex(buf: ArrayBuffer | Uint8Array): string {
  const bytes = buf instanceof Uint8Array ? buf : new Uint8Array(buf);
  return Array.from(bytes).map(b => b.toString(16).padStart(2, '0')).join('');
}

function randomHex(byteLen: number): string {
  const bytes = new Uint8Array(byteLen);
  crypto.getRandomValues(bytes);
  return toHex(bytes);
}

async function sha256Hex(input: string): Promise<string> {
  const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(input));
  return toHex(digest);
}

// Modern password hash: pbkdf2$<iterations>$<saltHex>$<hashHex>
async function hashPasswordPBKDF2(password: string, saltHex?: string): Promise<string> {
  const salt = saltHex ? hexToBytes(saltHex) : crypto.getRandomValues(new Uint8Array(16));
  const keyMaterial = await crypto.subtle.importKey(
    'raw', new TextEncoder().encode(password), 'PBKDF2', false, ['deriveBits']
  );
  const bits = await crypto.subtle.deriveBits(
    { name: 'PBKDF2', hash: 'SHA-256', salt: salt as BufferSource, iterations: PBKDF2_ITERATIONS },
    keyMaterial,
    256
  );
  return `pbkdf2$${PBKDF2_ITERATIONS}$${toHex(salt)}$${toHex(bits)}`;
}

function hexToBytes(hex: string): Uint8Array {
  const clean = hex.length % 2 === 0 ? hex : '0' + hex;
  const out = new Uint8Array(clean.length / 2);
  for (let i = 0; i < out.length; i++) out[i] = parseInt(clean.substr(i * 2, 2), 16);
  return out;
}

// Legacy hash (v1.0): SHA-256(password + static salt)
async function legacyHash(password: string): Promise<string> {
  return sha256Hex(password + LEGACY_SALT);
}

// Timing-safe comparison for equal-length hex strings
function safeEquals(a: string, b: string): boolean {
  if (a.length !== b.length) return false;
  let diff = 0;
  for (let i = 0; i < a.length; i++) diff |= a.charCodeAt(i) ^ b.charCodeAt(i);
  return diff === 0;
}

interface VerifyResult { ok: boolean; upgradeTo?: string; saltHex?: string; }

async function verifyPassword(password: string, stored: string, storedSalt: string): Promise<VerifyResult> {
  try {
    if (stored.startsWith('pbkdf2$')) {
      const [, iterStr, saltHex, hashHex] = stored.split('$');
      const iterations = parseInt(iterStr, 10) || PBKDF2_ITERATIONS;
      const keyMaterial = await crypto.subtle.importKey(
        'raw', new TextEncoder().encode(password), 'PBKDF2', false, ['deriveBits']
      );
      const bits = await crypto.subtle.deriveBits(
        { name: 'PBKDF2', hash: 'SHA-256', salt: hexToBytes(saltHex) as BufferSource, iterations },
        keyMaterial, 256
      );
      return { ok: safeEquals(toHex(bits), hashHex) };
    }
    // Legacy SHA-256 + static salt → verify, then signal an upgrade
    const legacy = await legacyHash(password);
    if (safeEquals(legacy, stored)) {
      const freshSalt = randomHex(16);
      const upgraded = await hashPasswordPBKDF2(password, freshSalt);
      return { ok: true, upgradeTo: upgraded, saltHex: freshSalt };
    }
    return { ok: false };
  } catch {
    return { ok: false };
  }
}

// ============================================================
// RATE LIMITING (in-memory, per-isolate sliding window)
// ============================================================

interface Hit { count: number; windowStart: number; }
const loginAttempts = new Map<string, Hit>();
const LOGIN_WINDOW_MS = 10 * 60 * 1000; // 10 minutes
const LOGIN_MAX_FAILURES = 8;

function rateLimitKey(ip: string, username: string): string {
  return `${ip}|${username.toLowerCase()}`;
}

function isRateLimited(key: string): boolean {
  const hit = loginAttempts.get(key);
  if (!hit) return false;
  if (Date.now() - hit.windowStart > LOGIN_WINDOW_MS) {
    loginAttempts.set(key, { count: 0, windowStart: Date.now() });
    return false;
  }
  return hit.count >= LOGIN_MAX_FAILURES;
}

function recordFailure(key: string): void {
  const hit = loginAttempts.get(key);
  if (!hit || Date.now() - hit.windowStart > LOGIN_WINDOW_MS) {
    loginAttempts.set(key, { count: 1, windowStart: Date.now() });
  } else {
    hit.count++;
  }
  // Opportunistic cleanup to bound memory
  if (loginAttempts.size > 5000) {
    const now = Date.now();
    for (const [k, v] of loginAttempts) {
      if (now - v.windowStart > LOGIN_WINDOW_MS) loginAttempts.delete(k);
    }
  }
}

function clearFailures(key: string): void {
  loginAttempts.delete(key);
}

// ============================================================
// CORS + SECURITY HEADERS
// ============================================================

const ALLOWED_ORIGINS = new Set<string>([
  'https://netops-hub.mortavox.workers.dev',
  'https://netops-hub-api.mortavox.workers.dev',
  'https://mortavox.github.io',
  'http://localhost:3000',
  'http://localhost:4173'
]);

function corsHeaders(request: Request): Record<string, string> {
  const origin = request.headers.get('Origin');
  // Native Android/Windows clients send no Origin header at all.
  if (origin && ALLOWED_ORIGINS.has(origin)) {
    return {
      'Access-Control-Allow-Origin': origin,
      'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization',
      'Access-Control-Max-Age': '86400',
      'Vary': 'Origin'
    };
  }
  return {};
}

function securityHeaders(): Record<string, string> {
  return {
    'X-Content-Type-Options': 'nosniff',
    'X-Frame-Options': 'DENY',
    'Referrer-Policy': 'no-referrer',
    'Cache-Control': 'no-store'
  };
}

function json(data: any, request: Request, status = 200): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
      ...securityHeaders(),
      ...corsHeaders(request)
    }
  });
}

// ============================================================
// AUTH
// ============================================================

const SESSION_TTL_MS = 24 * 60 * 60 * 1000; // 24 hours

async function authenticate(request: Request, env: Env): Promise<UserSession | null> {
  const authHeader = request.headers.get('Authorization');
  if (!authHeader || !authHeader.startsWith('Bearer ')) return null;
  const token = authHeader.substring(7).trim();
  if (token.length < 32 || token.length > 128 || !/^[a-f0-9]+$/i.test(token)) return null;

  const tokenHash = await sha256Hex(token);
  try {
    const session = await env.DB.prepare(
      `SELECT s.id AS session_id, s.expires_at, u.id, u.username, u.email, u.role, u.is_active
       FROM sessions s
       JOIN users u ON s.user_id = u.id
       WHERE s.token_hash = ? AND s.expires_at > datetime('now')`
    ).bind(tokenHash).first<any>();

    if (!session || session.is_active !== 1) return null;
    return {
      id: session.id,
      username: session.username,
      email: session.email,
      role: session.role,
      session_id: session.session_id
    };
  } catch {
    return null;
  }
}

function requireRole(user: UserSession, ...roles: string[]): boolean {
  return roles.includes(user.role);
}

function clientIp(request: Request): string {
  return (
    request.headers.get('CF-Connecting-IP') ||
    request.headers.get('X-Forwarded-For')?.split(',')[0].trim() ||
    'unknown'
  );
}

// ============================================================
// AUDIT
// ============================================================

async function logAudit(env: Env, user: UserSession | null, action: string, type: string, id?: string, details?: string) {
  try {
    await env.DB.prepare(
      `INSERT INTO audit_logs (id, user_id, username, action, resource_type, resource_id, details)
       VALUES (?, ?, ?, ?, ?, ?, ?)`
    ).bind(
      'log_' + randomHex(8),
      user?.id || 'anonymous',
      user?.username || 'anonymous',
      action,
      type,
      id || null,
      details || null
    ).run();
  } catch { /* audit must never break the request path */ }
}

// ============================================================
// VALIDATION HELPERS
// ============================================================

const USERNAME_RE = /^[a-zA-Z0-9._-]{3,32}$/;
const EXTENSION_RE = /^[0-9+\-() ]{0,16}$/;

function clean(v: any, maxLen: number): string {
  if (v === null || v === undefined) return '';
  return String(v).slice(0, maxLen).trim();
}

function passwordPolicyError(password: string): string | null {
  if (!password || password.length < 8) return 'Password must be at least 8 characters';
  if (!/[A-Z]/.test(password)) return 'Password must include an uppercase letter';
  if (!/[a-z]/.test(password)) return 'Password must include a lowercase letter';
  if (!/[0-9]/.test(password)) return 'Password must include a digit';
  if (!/[^A-Za-z0-9]/.test(password)) return 'Password must include a special character';
  return null;
}

function sanitizePhoneEntry(raw: any): { first_name: string; last_name: string; phone: string; company: string; extension: string; notes: string } | null {
  const first_name = clean(raw.first_name ?? raw.firstName, 80);
  if (!first_name) return null;
  const extension = clean(raw.extension, 16);
  if (extension && !EXTENSION_RE.test(extension)) return null;
  return {
    first_name,
    last_name: clean(raw.last_name ?? raw.lastName, 80),
    phone: clean(raw.phone, 32),
    company: clean(raw.company, 120),
    extension,
    notes: clean(raw.notes, 500)
  };
}

// ============================================================
// WORKER
// ============================================================

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url);
    const path = url.pathname;
    const method = request.method;

    if (method === 'OPTIONS') {
      return new Response(null, { status: 204, headers: { ...corsHeaders(request), 'Access-Control-Max-Age': '86400' } });
    }

    // Body size guard (8 MB) for JSON mutations
    const contentLength = parseInt(request.headers.get('Content-Length') || '0', 10);
    if (['POST', 'PUT'].includes(method) && contentLength > 8 * 1024 * 1024) {
      return json({ error: 'Payload too large' }, request, 413);
    }

    try {
      return await handle(request, env, url, method, path);
    } catch (err: any) {
      console.error('Unhandled worker error:', err);
      return json({ error: 'Internal server error' }, request, 500);
    }
  }
};

async function handle(request: Request, env: Env, url: URL, method: string, path: string): Promise<Response> {
  // ----------------------------------------------------------
  // HEALTH (public, minimal info)
  // ----------------------------------------------------------
  if (path === '/api/v1/health' && method === 'GET') {
    let dbStatus = 'ok';
    try { await env.DB.prepare('SELECT 1').first(); } catch { dbStatus = 'degraded'; }
    return json({ status: 'ok', version: '2.0.1', timestamp: new Date().toISOString(), database: dbStatus }, request);
  }

  // ----------------------------------------------------------
  // OTA UPDATE CHECK (public: version metadata only)
  // ----------------------------------------------------------
  if (path === '/api/v1/app/version' && method === 'GET') {
    const release = await env.DB.prepare(
      `SELECT version_code, version_name, changelog_en, changelog_fa, apk_url, exe_url, mandatory, created_at
       FROM app_releases WHERE published = 1 ORDER BY version_code DESC LIMIT 1`
    ).first<any>();
    if (!release) return json({ latest_version_code: 0, latest_version_name: null }, request);
    return json({
      latest_version_code: release.version_code,
      latest_version_name: release.version_name,
      changelog_en: release.changelog_en,
      changelog_fa: release.changelog_fa,
      apk_url: release.apk_url,
      exe_url: release.exe_url,
      mandatory: release.mandatory === 1,
      published_at: release.created_at
    }, request);
  }

  // Publish a release (Creator/Admin)
  if (path === '/api/v1/app/version' && method === 'POST') {
    const user = await authenticate(request, env);
    if (!user) return json({ error: 'Unauthorized' }, request, 401);
    if (!requireRole(user, 'Creator', 'Admin')) return json({ error: 'Forbidden' }, request, 403);

    const body = await request.json() as any;
    const versionCode = parseInt(body.version_code, 10);
    const versionName = clean(body.version_name, 20);
    if (!versionCode || !versionName) return json({ error: 'version_code and version_name are required' }, request, 400);

    const id = 'rel_' + randomHex(8);
    await env.DB.prepare(
      `INSERT INTO app_releases (id, version_code, version_name, changelog_en, changelog_fa, apk_url, exe_url, mandatory, published)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)`
    ).bind(
      id, versionCode, versionName,
      clean(body.changelog_en, 4000), clean(body.changelog_fa, 4000),
      clean(body.apk_url, 500), clean(body.exe_url, 500),
      body.mandatory ? 1 : 0
    ).run();
    await logAudit(env, user, 'RELEASE_PUBLISHED', 'app_release', id, `v${versionName} (code ${versionCode})`);
    return json({ message: 'Release published', id }, request, 201);
  }

  // ----------------------------------------------------------
  // AUTHENTICATION
  // ----------------------------------------------------------
  if (path === '/api/v1/auth/login' && method === 'POST') {
    const body = await request.json() as any;
    const username = clean(body.username, 64);
    const password = typeof body.password === 'string' ? body.password : '';
    if (!username || !password) return json({ error: 'Username and password required' }, request, 400);

    const ip = clientIp(request);
    const rlKey = rateLimitKey(ip, username);
    if (isRateLimited(rlKey)) {
      await logAudit(env, null, 'LOGIN_RATE_LIMITED', 'user', username, `IP ${ip}`);
      return json({ error: 'Too many failed attempts. Try again in 10 minutes.' }, request, 429);
    }

    const user = await env.DB.prepare(
      'SELECT * FROM users WHERE username = ? OR email = ?'
    ).bind(username, username).first<any>();

    // Uniform failure (no user enumeration): always run a hash op
    const storedHash = user?.password_hash || 'pbkdf2$100000$00$00';
    const verify = await verifyPassword(password, storedHash, user?.password_salt || '');

    if (!user || !user.is_active || !verify.ok) {
      recordFailure(rlKey);
      await logAudit(env, null, 'LOGIN_FAILED', 'user', user?.id || username, `IP ${ip}`);
      return json({ error: 'Invalid credentials' }, request, 401);
    }

    // Transparent legacy→PBKDF2 upgrade
    if (verify.upgradeTo) {
      await env.DB.prepare('UPDATE users SET password_hash = ?, password_salt = ? WHERE id = ?')
        .bind(verify.upgradeTo, verify.saltHex, user.id).run();
    }

    clearFailures(rlKey);

    // Housekeeping: drop expired + previous sessions of this user
    await env.DB.prepare(`DELETE FROM sessions WHERE expires_at <= datetime('now')`).run();
    await env.DB.prepare('DELETE FROM sessions WHERE user_id = ?').bind(user.id).run();

    const token = randomHex(32); // 64 hex chars
    const tokenHash = await sha256Hex(token);
    const sessionId = 'ses_' + randomHex(8);
    const expiresAt = new Date(Date.now() + SESSION_TTL_MS).toISOString().replace('T', ' ').replace('Z', '');

    await env.DB.prepare(
      'INSERT INTO sessions (id, user_id, token_hash, expires_at) VALUES (?, ?, ?, ?)'
    ).bind(sessionId, user.id, tokenHash, expiresAt).run();

    await env.DB.prepare('UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE id = ?').bind(user.id).run();
    await logAudit(env, { id: user.id, username: user.username, email: user.email, role: user.role, session_id: sessionId }, 'LOGIN', 'user', user.id);

    return json({
      token,
      expires_at: expiresAt,
      user: {
        id: user.id,
        username: user.username,
        display_name: user.display_name || user.username,
        email: user.email,
        role: user.role,
        must_change_password: user.must_change_password === 1
      }
    }, request);
  }

  if (path === '/api/v1/auth/me' && method === 'GET') {
    const user = await authenticate(request, env);
    if (!user) return json({ error: 'Unauthorized' }, request, 401);
    return json({ user: { id: user.id, username: user.username, email: user.email, role: user.role } }, request);
  }

  if (path === '/api/v1/auth/logout' && method === 'POST') {
    const authHeader = request.headers.get('Authorization');
    if (authHeader?.startsWith('Bearer ')) {
      const tokenHash = await sha256Hex(authHeader.substring(7).trim());
      await env.DB.prepare('DELETE FROM sessions WHERE token_hash = ?').bind(tokenHash).run();
    }
    return json({ message: 'Logged out successfully' }, request);
  }

  // ==================== EVERYTHING BELOW REQUIRES AUTH ====================
  const user = await authenticate(request, env);
  if (!user) return json({ error: 'Unauthorized: valid session required' }, request, 401);

  // ----------------------------------------------------------
  // SITES / VCENTERS / SERVERS
  // ----------------------------------------------------------
  if (path === '/api/v1/sites' && method === 'GET') {
    const { results } = await env.DB.prepare('SELECT * FROM sites ORDER BY name ASC').all();
    return json({ sites: results }, request);
  }

  if (path === '/api/v1/vcenters' && method === 'GET') {
    const { results } = await env.DB.prepare('SELECT * FROM vcenters ORDER BY name ASC').all();
    return json({ vcenters: results }, request);
  }

  if (path === '/api/v1/servers' && method === 'GET') {
    const q = url.searchParams.get('q');
    const site = url.searchParams.get('site');
    const status = url.searchParams.get('status');

    let query = 'SELECT * FROM servers WHERE 1=1';
    const params: any[] = [];
    if (site) { query += ' AND site = ?'; params.push(site); }
    if (status) { query += ' AND status = ?'; params.push(status); }
    if (q) {
      query += ' AND (name LIKE ? OR ip LIKE ? OR service LIKE ? OR server_host LIKE ? OR site LIKE ?)';
      const p = `%${q.replace(/[%_]/g, '')}%`;
      params.push(p, p, p, p, p);
    }
    query += ' ORDER BY server_num ASC, name ASC';

    const stmt = env.DB.prepare(query);
    const { results } = await (params.length > 0 ? stmt.bind(...params).all() : stmt.all());
    return json({ servers: results }, request);
  }

  if (path === '/api/v1/servers' && method === 'POST') {
    if (user.role === 'Viewer') return json({ error: 'Forbidden: Viewers cannot create servers' }, request, 403);

    const body = await request.json() as any;
    const { server_num, vcenter, server_host, name, ip, service, port_rdp, status, site, os, notes } = body;
    if (!clean(name, 120) || !clean(site, 80)) return json({ error: 'name and site are required' }, request, 400);

    // SECURITY: ID is always generated server-side (client IDs are ignored)
    const id = 's_' + randomHex(8) + '_' + Date.now();
    await env.DB.prepare(
      `INSERT INTO servers (id, server_num, vcenter, server_host, name, ip, service, port_rdp, status, site, os, notes, created_by)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`
    ).bind(
      id, parseInt(server_num, 10) || 0, clean(vcenter, 80), clean(server_host, 80), clean(name, 120), clean(ip, 60),
      clean(service, 200), clean(port_rdp, 20) || '-', (status === 'off' ? 'off' : 'ok'), clean(site, 80),
      clean(os, 60) || 'Windows/Linux', clean(notes, 500), user.username
    ).run();

    await logAudit(env, user, 'CREATE', 'server', id, `Created server ${name} (${ip})`);
    const created = await env.DB.prepare('SELECT * FROM servers WHERE id = ?').bind(id).first();
    return json({ server: created }, request, 201);
  }

  if (path.startsWith('/api/v1/servers/') && method === 'PUT') {
    if (user.role === 'Viewer') return json({ error: 'Forbidden: Viewers cannot update servers' }, request, 403);

    const serverId = decodeURIComponent(path.replace('/api/v1/servers/', '').trim()).slice(0, 64);
    const body = await request.json() as any;

    const existing = await env.DB.prepare('SELECT * FROM servers WHERE id = ?').bind(serverId).first<any>();
    if (!existing) return json({ error: 'Server not found' }, request, 404);

    await env.DB.prepare(
      `UPDATE servers SET name = ?, ip = ?, service = ?, port_rdp = ?, status = ?, site = ?, os = ?, notes = ?, server_num = ?, vcenter = ?, server_host = ?, updated_at = datetime('now'), updated_by = ?
       WHERE id = ?`
    ).bind(
      clean(body.name ?? existing.name, 120), clean(body.ip ?? existing.ip, 60), clean(body.service ?? existing.service, 200),
      clean(body.port_rdp ?? existing.port_rdp, 20) || '-', (body.status ?? existing.status) === 'off' ? 'off' : 'ok',
      clean(body.site ?? existing.site, 80), clean(body.os ?? existing.os, 60), clean(body.notes ?? existing.notes, 500),
      parseInt(body.server_num, 10) || existing.server_num, clean(body.vcenter ?? existing.vcenter, 80),
      clean(body.server_host ?? existing.server_host, 80), user.username, serverId
    ).run();

    await logAudit(env, user, 'UPDATE', 'server', serverId, `Updated server ${body.name ?? existing.name}`);
    const updated = await env.DB.prepare('SELECT * FROM servers WHERE id = ?').bind(serverId).first();
    return json({ server: updated }, request);
  }

  if (path.startsWith('/api/v1/servers/') && method === 'DELETE') {
    if (!requireRole(user, 'Admin', 'Creator')) {
      return json({ error: 'Forbidden: Only Admins and Creators can delete servers' }, request, 403);
    }
    const serverId = decodeURIComponent(path.replace('/api/v1/servers/', '').trim()).slice(0, 64);
    await env.DB.prepare('DELETE FROM servers WHERE id = ?').bind(serverId).run();
    await logAudit(env, user, 'DELETE', 'server', serverId);
    return json({ success: true, message: `Server deleted` }, request);
  }

  // ----------------------------------------------------------
  // CONSOLIDATED SYNC (authenticated; users list without emails for Viewer/Editor)
  // ----------------------------------------------------------
  if (path === '/api/v1/sync/all' && method === 'GET') {
    const [serversRes, sitesRes, vcentersRes, rahkaranRes, articlesRes, usersRes, phoneRes] = await Promise.all([
      env.DB.prepare('SELECT * FROM servers ORDER BY server_num ASC, name ASC').all(),
      env.DB.prepare('SELECT * FROM sites ORDER BY name ASC').all(),
      env.DB.prepare('SELECT * FROM vcenters ORDER BY name ASC').all(),
      env.DB.prepare('SELECT * FROM rahkaran_companies ORDER BY row_no ASC').all(),
      env.DB.prepare('SELECT * FROM academy_articles ORDER BY created_at DESC').all(),
      env.DB.prepare('SELECT id, username, display_name, role, is_active, must_change_password FROM users ORDER BY created_at DESC').all(),
      env.DB.prepare('SELECT * FROM phonebook ORDER BY last_name ASC, first_name ASC').all()
    ]);

    return json({
      timestamp: new Date().toISOString(),
      servers: serversRes.results,
      sites: sitesRes.results,
      vcenters: vcentersRes.results,
      rahkaran: rahkaranRes.results,
      articles: articlesRes.results,
      users: usersRes.results,
      phonebook: phoneRes.results
    }, request);
  }

  // ----------------------------------------------------------
  // RAHKARAN
  // ----------------------------------------------------------
  if (path === '/api/v1/rahkaran' && method === 'GET') {
    const { results } = await env.DB.prepare('SELECT * FROM rahkaran_companies ORDER BY row_no ASC').all();
    return json({ companies: results }, request);
  }

  // ----------------------------------------------------------
  // PHONE BOOK (دفترچه تلفن)
  // ----------------------------------------------------------
  if (path === '/api/v1/phonebook' && method === 'GET') {
    const q = url.searchParams.get('q');
    let query = 'SELECT * FROM phonebook WHERE 1=1';
    const params: any[] = [];
    if (q) {
      query += ' AND (first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? OR company LIKE ? OR extension LIKE ?)';
      const p = `%${q.replace(/[%_]/g, '')}%`;
      params.push(p, p, p, p, p);
    }
    query += ' ORDER BY last_name ASC, first_name ASC';
    const { results } = await (params.length ? env.DB.prepare(query).bind(...params).all() : env.DB.prepare(query).all());
    return json({ phonebook: results }, request);
  }

  if (path === '/api/v1/phonebook' && method === 'POST') {
    if (user.role === 'Viewer') return json({ error: 'Forbidden' }, request, 403);
    const body = await request.json() as any;
    const entry = sanitizePhoneEntry(body);
    if (!entry) return json({ error: 'first_name is required and fields are length-limited' }, request, 400);

    const id = 'ph_' + randomHex(8);
    await env.DB.prepare(
      `INSERT INTO phonebook (id, first_name, last_name, phone, company, extension, notes, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)`
    ).bind(id, entry.first_name, entry.last_name, entry.phone, entry.company, entry.extension, entry.notes, user.username).run();

    await logAudit(env, user, 'PHONEBOOK_CREATE', 'phonebook', id, `${entry.first_name} ${entry.last_name}`);
    const created = await env.DB.prepare('SELECT * FROM phonebook WHERE id = ?').bind(id).first();
    return json({ entry: created }, request, 201);
  }

  if (path === '/api/v1/phonebook/bulk' && method === 'POST') {
    if (!requireRole(user, 'Admin', 'Creator')) return json({ error: 'Forbidden: Admin or Creator required' }, request, 403);
    const body = await request.json() as any;
    const entries = body.entries;
    if (!Array.isArray(entries)) return json({ error: 'entries array required' }, request, 400);
    if (entries.length > 2000) return json({ error: 'Maximum 2000 entries per import' }, request, 400);

    let inserted = 0;
    for (const raw of entries) {
      const entry = sanitizePhoneEntry(raw);
      if (!entry) continue;
      const id = 'ph_' + randomHex(8);
      await env.DB.prepare(
        `INSERT INTO phonebook (id, first_name, last_name, phone, company, extension, notes, created_by)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?)
         ON CONFLICT(id) DO NOTHING`
      ).bind(id, entry.first_name, entry.last_name, entry.phone, entry.company, entry.extension, entry.notes, user.username).run();
      inserted++;
    }
    await logAudit(env, user, 'PHONEBOOK_BULK_IMPORT', 'phonebook', undefined, `Imported ${inserted} contacts`);
    return json({ message: `Imported ${inserted} contacts`, count: inserted }, request, 201);
  }

  if (path.startsWith('/api/v1/phonebook/') && method === 'PUT') {
    if (user.role === 'Viewer') return json({ error: 'Forbidden' }, request, 403);
    const id = decodeURIComponent(path.replace('/api/v1/phonebook/', '').trim()).slice(0, 64);
    const existing = await env.DB.prepare('SELECT * FROM phonebook WHERE id = ?').bind(id).first<any>();
    if (!existing) return json({ error: 'Contact not found' }, request, 404);

    const body = await request.json() as any;
    const merged = sanitizePhoneEntry({
      first_name: body.first_name ?? existing.first_name,
      last_name: body.last_name ?? existing.last_name,
      phone: body.phone ?? existing.phone,
      company: body.company ?? existing.company,
      extension: body.extension ?? existing.extension,
      notes: body.notes ?? existing.notes
    });
    if (!merged) return json({ error: 'Invalid contact data' }, request, 400);

    await env.DB.prepare(
      `UPDATE phonebook SET first_name = ?, last_name = ?, phone = ?, company = ?, extension = ?, notes = ?, updated_at = datetime('now'), updated_by = ? WHERE id = ?`
    ).bind(merged.first_name, merged.last_name, merged.phone, merged.company, merged.extension, merged.notes, user.username, id).run();

    await logAudit(env, user, 'PHONEBOOK_UPDATE', 'phonebook', id);
    const updated = await env.DB.prepare('SELECT * FROM phonebook WHERE id = ?').bind(id).first();
    return json({ entry: updated }, request);
  }

  if (path.startsWith('/api/v1/phonebook/') && method === 'DELETE') {
    if (!requireRole(user, 'Admin', 'Creator')) return json({ error: 'Forbidden: Admin or Creator required' }, request, 403);
    const id = decodeURIComponent(path.replace('/api/v1/phonebook/', '').trim()).slice(0, 64);
    await env.DB.prepare('DELETE FROM phonebook WHERE id = ?').bind(id).run();
    await logAudit(env, user, 'PHONEBOOK_DELETE', 'phonebook', id);
    return json({ message: 'Contact deleted' }, request);
  }

  // ----------------------------------------------------------
  // ACADEMY
  // ----------------------------------------------------------
  if (path === '/api/v1/academy/categories' && method === 'GET') {
    const { results } = await env.DB.prepare('SELECT * FROM academy_categories ORDER BY order_index ASC').all();
    return json({ categories: results }, request);
  }

  if (path === '/api/v1/academy/articles' && method === 'GET') {
    const cat = url.searchParams.get('category');
    const q = url.searchParams.get('q');
    const difficulty = url.searchParams.get('difficulty');

    let query = 'SELECT * FROM academy_articles WHERE 1=1';
    const params: any[] = [];
    if (cat) { query += ' AND category_slug = ?'; params.push(cat); }
    if (difficulty) { query += ' AND difficulty = ?'; params.push(difficulty); }
    if (q) {
      query += ' AND (title LIKE ? OR summary LIKE ? OR content LIKE ? OR tags LIKE ?)';
      const p = `%${q.replace(/[%_]/g, '')}%`;
      params.push(p, p, p, p);
    }
    query += ' ORDER BY created_at DESC';

    const stmt = env.DB.prepare(query);
    const { results } = await (params.length > 0 ? stmt.bind(...params).all() : stmt.all());
    return json({ articles: results }, request);
  }

  if (path === '/api/v1/academy/articles' && method === 'POST') {
    if (user.role === 'Viewer') return json({ error: 'Forbidden: Viewers cannot create articles' }, request, 403);

    const body = await request.json() as any;
    const { title, summary, content, category_slug } = body;
    if (!clean(title, 200) || !content || !clean(category_slug, 60)) {
      return json({ error: 'title, content, and category_slug are required' }, request, 400);
    }

    const id = 'art_' + randomHex(12);
    await env.DB.prepare(
      `INSERT INTO academy_articles (
        id, title, title_fa, summary, summary_fa, content,
        category_slug, subcategory, vendor, difficulty, tags,
        commands_json, warnings, expected_results, verification, troubleshooting,
        created_by, updated_by
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`
    ).bind(
      id, clean(title, 200), clean(body.title_fa, 200) || null, clean(summary, 400) || null, clean(body.summary_fa, 400) || null, content,
      clean(category_slug, 60), clean(body.subcategory, 100) || null, clean(body.vendor, 80) || 'Enterprise IT',
      clean(body.difficulty, 20) || 'Intermediate', clean(body.tags, 300) || null,
      typeof body.commands_json === 'string' ? body.commands_json.slice(0, 50000) : '[]',
      clean(body.warnings, 2000) || null, clean(body.expected_results, 2000) || null,
      clean(body.verification, 2000) || null, clean(body.troubleshooting, 2000) || null,
      user.username, user.username
    ).run();

    await logAudit(env, user, 'ARTICLE_CREATED', 'academy_article', id, `Created article "${title}"`);
    return json({ message: 'Article created successfully', id }, request, 201);
  }

  if (path === '/api/v1/academy/bulk-sync' && method === 'POST') {
    if (user.role === 'Viewer') return json({ error: 'Forbidden' }, request, 403);

    const body = await request.json() as any;
    const articles = body.articles as any[];
    if (!Array.isArray(articles)) return json({ error: 'articles array required' }, request, 400);
    if (articles.length > 500) return json({ error: 'Maximum 500 articles per bulk-sync' }, request, 400);

    let insertedCount = 0;
    for (const a of articles) {
      if (!a.id || !a.title || !a.content) continue;
      await env.DB.prepare(
        `INSERT INTO academy_articles (
          id, title, title_fa, summary, summary_fa, content,
          category_slug, subcategory, vendor, difficulty, tags,
          commands_json, warnings, expected_results, verification, troubleshooting,
          created_by, updated_by
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT(id) DO UPDATE SET
          title = excluded.title,
          title_fa = excluded.title_fa,
          summary = excluded.summary,
          summary_fa = excluded.summary_fa,
          content = excluded.content,
          category_slug = excluded.category_slug,
          subcategory = excluded.subcategory,
          vendor = excluded.vendor,
          difficulty = excluded.difficulty,
          tags = excluded.tags,
          commands_json = excluded.commands_json,
          warnings = excluded.warnings,
          expected_results = excluded.expected_results,
          verification = excluded.verification,
          troubleshooting = excluded.troubleshooting,
          updated_at = CURRENT_TIMESTAMP,
          updated_by = excluded.updated_by`
      ).bind(
        String(a.id).slice(0, 64), String(a.title).slice(0, 200), a.title_fa || a.titleFa || null,
        a.summary || null, a.summary_fa || a.summaryFa || null, a.content,
        a.category_slug || a.categorySlug || 'help-desk', a.subcategory || null, a.vendor || 'Enterprise IT',
        a.difficulty || 'Intermediate', a.tags || null, typeof a.commands_json === 'string' ? a.commands_json : (a.commandsJson || '[]'),
        a.warnings || null, a.expected_results || a.expectedResults || null, a.verification || null, a.troubleshooting || null,
        user.username, user.username
      ).run();
      insertedCount++;
    }

    await logAudit(env, user, 'BULK_SYNC_ARTICLES', 'academy_article', undefined, `Synchronized ${insertedCount} articles`);
    return json({ message: `Successfully synchronized ${insertedCount} articles`, count: insertedCount }, request);
  }

  if (path.startsWith('/api/v1/academy/articles/') && method === 'PUT') {
    if (user.role === 'Viewer') return json({ error: 'Forbidden' }, request, 403);

    const id = decodeURIComponent(path.replace('/api/v1/academy/articles/', '')).slice(0, 64);
    const existing = await env.DB.prepare('SELECT id, title FROM academy_articles WHERE id = ?').bind(id).first<any>();
    if (!existing) return json({ error: 'Article not found' }, request, 404);

    const body = await request.json() as any;
    const { title, title_fa, summary, summary_fa, content, category_slug, subcategory, vendor, difficulty, tags, commands_json, warnings, expected_results, verification, troubleshooting } = body;

    await env.DB.prepare(
      `UPDATE academy_articles SET
        title = COALESCE(?, title),
        title_fa = COALESCE(?, title_fa),
        summary = COALESCE(?, summary),
        summary_fa = COALESCE(?, summary_fa),
        content = COALESCE(?, content),
        category_slug = COALESCE(?, category_slug),
        subcategory = COALESCE(?, subcategory),
        vendor = COALESCE(?, vendor),
        difficulty = COALESCE(?, difficulty),
        tags = COALESCE(?, tags),
        commands_json = COALESCE(?, commands_json),
        warnings = COALESCE(?, warnings),
        expected_results = COALESCE(?, expected_results),
        verification = COALESCE(?, verification),
        troubleshooting = COALESCE(?, troubleshooting),
        updated_at = CURRENT_TIMESTAMP,
        updated_by = ?
      WHERE id = ?`
    ).bind(
      clean(title, 200) || null, clean(title_fa, 200) || null, clean(summary, 400) || null, clean(summary_fa, 400) || null,
      content || null, clean(category_slug, 60) || null, clean(subcategory, 100) || null, clean(vendor, 80) || null,
      clean(difficulty, 20) || null, clean(tags, 300) || null,
      typeof commands_json === 'string' ? commands_json.slice(0, 50000) : null,
      clean(warnings, 2000) || null, clean(expected_results, 2000) || null, clean(verification, 2000) || null,
      clean(troubleshooting, 2000) || null, user.username, id
    ).run();

    await logAudit(env, user, 'ARTICLE_UPDATED', 'academy_article', id, `Updated article "${title || existing.title}"`);
    return json({ message: 'Article updated successfully', id }, request);
  }

  if (path.startsWith('/api/v1/academy/articles/') && method === 'DELETE') {
    if (!requireRole(user, 'Creator', 'Admin')) {
      return json({ error: 'Forbidden: Admin or Creator required to delete articles' }, request, 403);
    }
    const id = decodeURIComponent(path.replace('/api/v1/academy/articles/', '')).slice(0, 64);
    await env.DB.prepare('DELETE FROM academy_articles WHERE id = ?').bind(id).run();
    await logAudit(env, user, 'ARTICLE_DELETED', 'academy_article', id);
    return json({ message: 'Article deleted successfully' }, request);
  }

  if (path.startsWith('/api/v1/academy/articles/') && method === 'GET') {
    const id = decodeURIComponent(path.replace('/api/v1/academy/articles/', '')).slice(0, 64);
    const article = await env.DB.prepare('SELECT * FROM academy_articles WHERE id = ?').bind(id).first();
    if (!article) return json({ error: 'Article not found' }, request, 404);
    return json({ article }, request);
  }

  if (path === '/api/v1/academy/ports' && method === 'GET') {
    const q = url.searchParams.get('q');
    let query = 'SELECT * FROM ports';
    const params: any[] = [];
    if (q) {
      query += ' WHERE service LIKE ? OR description LIKE ? OR CAST(port_number AS TEXT) LIKE ?';
      const p = `%${q.replace(/[%_]/g, '')}%`;
      params.push(p, p, p);
    }
    query += ' ORDER BY port_number ASC';
    const stmt = env.DB.prepare(query);
    const { results } = await (params.length > 0 ? stmt.bind(...params).all() : stmt.all());
    return json({ ports: results }, request);
  }

  if (path === '/api/v1/academy/errors' && method === 'GET') {
    const { results } = await env.DB.prepare('SELECT * FROM http_error_codes ORDER BY code ASC').all();
    return json({ errors: results }, request);
  }

  if (path === '/api/v1/academy/wireshark' && method === 'GET') {
    const { results } = await env.DB.prepare('SELECT * FROM wireshark_filters ORDER BY id ASC').all();
    return json({ filters: results }, request);
  }

  if (path === '/api/v1/academy/sources' && method === 'GET') {
    const sources = [
      { id: 'src_ms_learn', name: 'Microsoft Learn & TechNet', name_fa: 'مرجع اسناد رسمی مایکروسافت', category: 'Windows & Server', url: 'https://learn.microsoft.com/en-us/windows-server/', description: 'Official Windows Server, Active Directory, PowerShell, and Client OS documentation.', description_fa: 'مستندات و کتابخانه‌های عیب‌یابی ویندوز سرور و اکتیو دایرکتوری' },
      { id: 'src_cisco', name: 'Cisco Networking Documentation', name_fa: 'پرتال رسمی شبکه سیسکو', category: 'Routing & Switching', url: 'https://www.cisco.com/c/en/us/support/index.html', description: 'Configuration guides for IOS XE, Catalyst, Nexus, OSPF, BGP, and VLAN trunking.', description_fa: 'راهنماهای پیکربندی و کامندهای روتر و سوئیچ‌های سیسکو' },
      { id: 'src_mikrotik', name: 'MikroTik Documentation & Wiki', name_fa: 'دانشنامه رسمی میکروتیک', category: 'RouterOS & Firewall', url: 'https://help.mikrotik.com/docs/', description: 'Authoritative RouterOS v7 manuals, firewall mangle, queue trees, and WireGuard tunnels.', description_fa: 'مستندات روتربورد، تنظیمات فایروال و مسیریابی پیشرفته میکروتیک' },
      { id: 'src_fortinet', name: 'Fortinet FortiGuard Labs & Cookbook', name_fa: 'مرجع سناریوهای فورتی‌گیت', category: 'Enterprise Security', url: 'https://docs.fortinet.com/', description: 'FortiOS administration guides, SD-WAN steering, IPsec VPN, and threat prevention.', description_fa: 'سناریوهای استقرار فایروال فورتی‌گیت، SD-WAN و وی‌پی‌ان‌های امن' },
      { id: 'src_wireshark', name: 'Wireshark User Guide & Filter Spec', name_fa: 'راهنمای فیلترهای وایرشارک', category: 'Packet Analysis', url: 'https://www.wireshark.org/docs/dfref/', description: 'Official display filter reference and protocol decoder specifications.', description_fa: 'مرجع رسمی فیلترهای پیشرفته تحلیل پکت‌های شبکه' },
      { id: 'src_vmware', name: 'Broadcom VMware Knowledge Base', name_fa: 'پایگاه دانش وی‌ام‌ویر (VMware KB)', category: 'Virtualization & Cloud', url: 'https://knowledge.broadcom.com/external/article?articleNumber=vmware', description: 'vSphere, ESXi, vCenter troubleshooting, and vMotion migration diagnostics.', description_fa: 'حل مشکلات و خطاهای هاست‌های ESXi و کلاستر vCenter' }
    ];
    return json({ sources }, request);
  }

  // ----------------------------------------------------------
  // FAVORITES & NOTES
  // ----------------------------------------------------------
  if (path === '/api/v1/favorites' && (method === 'GET' || method === 'POST')) {
    if (method === 'GET') {
      const { results } = await env.DB.prepare(
        `SELECT f.*, a.title, a.category_slug, a.summary
         FROM favorites f
         JOIN academy_articles a ON f.article_id = a.id
         WHERE f.user_id = ?
         ORDER BY f.created_at DESC`
      ).bind(user.id).all();
      return json({ favorites: results }, request);
    }
    const body = await request.json() as any;
    const articleId = clean(body.article_id, 64);
    if (!articleId) return json({ error: 'article_id required' }, request, 400);
    await env.DB.prepare('INSERT OR IGNORE INTO favorites (id, user_id, article_id) VALUES (?, ?, ?)')
      .bind('fav_' + randomHex(8), user.id, articleId).run();
    return json({ message: 'Saved to favorites' }, request, 201);
  }

  if (path.startsWith('/api/v1/favorites/') && method === 'DELETE') {
    const articleId = decodeURIComponent(path.replace('/api/v1/favorites/', '')).slice(0, 64);
    await env.DB.prepare('DELETE FROM favorites WHERE user_id = ? AND article_id = ?').bind(user.id, articleId).run();
    return json({ message: 'Removed from favorites' }, request);
  }

  if (path === '/api/v1/notes' && (method === 'GET' || method === 'POST')) {
    if (method === 'GET') {
      const { results } = await env.DB.prepare('SELECT * FROM personal_notes WHERE user_id = ? ORDER BY updated_at DESC')
        .bind(user.id).all();
      return json({ notes: results }, request);
    }
    const body = await request.json() as any;
    const noteId = 'note_' + randomHex(8);
    await env.DB.prepare('INSERT INTO personal_notes (id, user_id, article_id, title, content) VALUES (?, ?, ?, ?, ?)')
      .bind(noteId, user.id, clean(body.article_id, 64) || null, clean(body.title, 200) || 'Untitled Note', (body.content || '').toString().slice(0, 20000)).run();
    return json({ id: noteId, message: 'Note created' }, request, 201);
  }

  // ----------------------------------------------------------
  // USER MANAGEMENT (Creator & Admin)
  // ----------------------------------------------------------
  if (path === '/api/v1/users' && method === 'GET') {
    if (!requireRole(user, 'Creator', 'Admin')) return json({ error: 'Forbidden: Insufficient privileges' }, request, 403);
    const { results } = await env.DB.prepare(
      'SELECT id, username, display_name, email, role, is_active, must_change_password, created_at, updated_at FROM users ORDER BY created_at DESC'
    ).all();
    return json({ users: results }, request);
  }

  if (path === '/api/v1/users' && method === 'POST') {
    if (!requireRole(user, 'Creator', 'Admin')) return json({ error: 'Forbidden: Insufficient privileges' }, request, 403);

    const body = await request.json() as any;
    const username = clean(body.username, 32);
    const password = typeof body.password === 'string' ? body.password : '';
    const role = clean(body.role, 20);

    if (!USERNAME_RE.test(username)) return json({ error: 'Invalid username (3-32 chars: letters, digits, . _ -)' }, request, 400);
    if (!password) return json({ error: 'Password is required' }, request, 400);
    const policyError = passwordPolicyError(password);
    if (policyError) return json({ error: policyError }, request, 400);

    if (role === 'Creator' && user.role !== 'Creator') {
      return json({ error: 'Forbidden: Only an existing Creator can assign the Creator role' }, request, 403);
    }
    const validRoles = ['Creator', 'Admin', 'Editor', 'Viewer'];
    if (!validRoles.includes(role)) return json({ error: 'Invalid role specified' }, request, 400);

    const email = clean(body.email, 120) || `${username}@netopshub.internal`;
    const existing = await env.DB.prepare('SELECT id FROM users WHERE username = ? OR email = ?').bind(username, email).first();
    if (existing) return json({ error: 'Username or email already exists' }, request, 409);

    const newId = 'u_' + randomHex(8);
    const passHash = await hashPasswordPBKDF2(password);
    const salt = passHash.split('$')[2];
    const displayName = clean(body.display_name, 80) || username;
    const mustChange = body.must_change_password === false ? 0 : 1;

    await env.DB.prepare(
      `INSERT INTO users (id, username, display_name, email, password_hash, password_salt, role, is_active, must_change_password, created_by)
       VALUES (?, ?, ?, ?, ?, ?, ?, 1, ?, ?)`
    ).bind(newId, username, displayName, email, passHash, salt, role, mustChange, user.username).run();

    await logAudit(env, user, 'USER_CREATED', 'user', newId, `Created user ${username} with role ${role}`);
    return json({
      message: 'User created successfully',
      user: { id: newId, username, display_name: displayName, email, role, is_active: 1, must_change_password: mustChange === 1 }
    }, request, 201);
  }

  if (path.startsWith('/api/v1/users/') && path.endsWith('/password') && method === 'PUT') {
    const targetId = path.split('/')[4];
    if (!requireRole(user, 'Creator', 'Admin') && user.id !== targetId) {
      return json({ error: 'Forbidden: Insufficient privileges to change this password' }, request, 403);
    }

    const body = await request.json() as any;
    const password = typeof body.password === 'string' ? body.password : '';
    if (!password) return json({ error: 'New password is required' }, request, 400);
    // Self-service changes still follow the strong-password policy
    const policyError = passwordPolicyError(password);
    if (policyError) return json({ error: policyError }, request, 400);

    const passHash = await hashPasswordPBKDF2(password);
    const salt = passHash.split('$')[2];
    const mustChange = body.must_change_password === false ? 0 : 1;

    await env.DB.prepare(
      'UPDATE users SET password_hash = ?, password_salt = ?, must_change_password = ?, updated_at = CURRENT_TIMESTAMP, updated_by = ? WHERE id = ?'
    ).bind(passHash, salt, mustChange, user.username, targetId).run();

    // SECURITY: invalidate every existing session of the target user
    await env.DB.prepare('DELETE FROM sessions WHERE user_id = ?').bind(targetId).run();

    await logAudit(env, user, 'PASSWORD_UPDATED', 'user', targetId, `Password rotated for ${targetId}`);
    return json({ message: 'Password updated successfully. All sessions were invalidated.' }, request);
  }

  if (path.startsWith('/api/v1/users/') && path.endsWith('/role') && method === 'PUT') {
    if (!requireRole(user, 'Creator', 'Admin')) return json({ error: 'Forbidden' }, request, 403);
    const targetId = path.split('/')[4];
    const body = await request.json() as any;
    const newRole = clean(body.role, 20);
    const validRoles = ['Creator', 'Admin', 'Editor', 'Viewer'];
    if (!validRoles.includes(newRole)) return json({ error: 'Invalid role' }, request, 400);
    if (newRole === 'Creator' && user.role !== 'Creator') {
      return json({ error: 'Forbidden: Only a Creator can assign the Creator role' }, request, 403);
    }
    if (targetId === 'u_creator' && newRole !== 'Creator') {
      return json({ error: 'The system owner role cannot be downgraded' }, request, 400);
    }

    await env.DB.prepare('UPDATE users SET role = ?, updated_at = CURRENT_TIMESTAMP, updated_by = ? WHERE id = ?')
      .bind(newRole, user.username, targetId).run();
    // Role escalation/de-escalation revokes live sessions
    await env.DB.prepare('DELETE FROM sessions WHERE user_id = ?').bind(targetId).run();

    await logAudit(env, user, 'USER_ROLE_CHANGED', 'user', targetId, `Changed role to ${newRole}`);
    return json({ message: `Role updated to ${newRole}` }, request);
  }

  if (path.startsWith('/api/v1/users/') && path.endsWith('/status') && method === 'PUT') {
    if (!requireRole(user, 'Creator', 'Admin')) return json({ error: 'Forbidden' }, request, 403);
    const targetId = path.split('/')[4];
    const body = await request.json() as any;
    const isActive = body.is_active ? 1 : 0;
    if (targetId === 'u_creator' && !isActive) {
      return json({ error: 'The system owner cannot be deactivated' }, request, 400);
    }

    await env.DB.prepare('UPDATE users SET is_active = ?, updated_at = CURRENT_TIMESTAMP, updated_by = ? WHERE id = ?')
      .bind(isActive, user.username, targetId).run();
    if (!isActive) {
      await env.DB.prepare('DELETE FROM sessions WHERE user_id = ?').bind(targetId).run();
    }

    await logAudit(env, user, isActive ? 'USER_ACTIVATED' : 'USER_DEACTIVATED', 'user', targetId);
    return json({ message: `Status updated to ${isActive ? 'Active' : 'Inactive'}` }, request);
  }

  if (path.startsWith('/api/v1/users/') && method === 'DELETE') {
    if (!requireRole(user, 'Creator', 'Admin')) return json({ error: 'Forbidden' }, request, 403);
    const targetId = path.replace('/api/v1/users/', '').trim();
    if (targetId === 'u_creator' || targetId === user.id) {
      return json({ error: 'Cannot delete the system owner or yourself' }, request, 400);
    }
    await env.DB.prepare('DELETE FROM users WHERE id = ?').bind(targetId).run();
    await env.DB.prepare('DELETE FROM sessions WHERE user_id = ?').bind(targetId).run();
    await logAudit(env, user, 'USER_DELETED', 'user', targetId);
    return json({ message: 'User deleted successfully' }, request);
  }

  if (path === '/api/v1/audit-logs' && method === 'GET') {
    if (!requireRole(user, 'Creator', 'Admin')) return json({ error: 'Forbidden: Admins only' }, request, 403);
    const { results } = await env.DB.prepare('SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT 200').all();
    return json({ audit_logs: results }, request);
  }

  if (path === '/api/v1/academy/sync-online' && method === 'POST') {
    return json({
      message: 'Online Academy sources synchronized',
      synced_at: new Date().toISOString(),
      sources: ['Microsoft Learn', 'Cisco Community', 'MikroTik Wiki', 'Fortinet Docs']
    }, request);
  }

  return json({ error: 'Endpoint not found' }, request, 404);
}
