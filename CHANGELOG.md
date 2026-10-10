# Changelog — NetOps Hub

## [2.0.1] — 2026-10-10

### Security (Critical Fixes)
- **REMOVED** the static creator master-token bypass (`cfat_*` prefix) in the Cloudflare Worker — the most critical finding of the v1.x security assessment. Every privileged request now requires a session issued by `/auth/login`.
- **REMOVED** all client-side password backdoors: `Creator@NetOps2026`, `Admin@NetOps2026`, `Editor@NetOps2026`, `Viewer@NetOps2026`, the `123456` blank-password fallback and the `getCreatorUser()` synthetic owner.
- **ADDED** PBKDF2-SHA256 password hashing (100,000 iterations, per-user 16-byte random salt) on the Worker with transparent upgrade of legacy SHA-256(static-salt) hashes at next successful login.
- **ADDED** login rate limiting (8 failures / 10 min per IP+username) plus failed-login and rate-limit audit logging.
- **SECURED** previously anonymous GET endpoints (`/sync/all`, `/servers`, `/sites`, `/vcenters`, `/rahkaran`, academy data, favorites/notes) — authentication is now mandatory.
- **HARDENED** session lifecycle: tokens persisted only as SHA-256 hashes, 24h TTL, automatic revocation on password change, role change and deactivation; expired-session cleanup at login.
- **FIXED** client-controlled `INSERT OR REPLACE` on server creation — IDs are now generated server-side.
- **RESTRICTED** CORS from wildcard `*` to an explicit origin allowlist (native apps unaffected).
- **ADDED** security headers (`X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy`, `Cache-Control: no-store`) and an 8 MB request-size guard.
- **CLIENT**: local Room passwords migrated to PBKDF2 hashes only; server session token encrypted at rest with Android Keystore AES-256-GCM (`SecureVault`); HTTP body logging disabled in release; `allowBackup=false` + backup/extraction rules excluding databases and credential prefs; R8 minification enabled with Moshi/Retrofit/Room keep rules; debug keystore removed from .gitignore exclusions so CI builds carry a stable OTA signing key; burned password documentation redacted from migration 0001.

### Biometric Login (kept, upgraded)
- Replaced lookup-only biometric login with the canonical **BiometricPrompt + CryptoObject** flow (`BiometricSession`): enrollment right after a successful password login, hardware-backed AES-GCM key requiring per-operation user authentication, automatic invalidation on new biometric enrollment. Only the enrolled account can be restored.

### In-App OTA Updates (برنامه آنلاین)
- New `GET /api/v1/app/version` endpoint backed by the `app_releases` D1 table (publish releases with `POST`, Creator/Admin only).
- New `UpdateManager` + `AppUpdateDialog`: on every launch the app checks for a newer version and shows an in-app "new version" message with the changelog (EN/FA) and a one-tap **Update Now** → DownloadManager → package installer flow (`REQUEST_INSTALL_PACKAGES` + FileProvider). Mandatory updates cannot be dismissed.
- Content updates (servers, articles, phone book, users) continue to stream automatically through the authenticated 20-second `/sync/all` channel — most changes reach users with zero reinstall.

### Phone Book (دفترچه تلفن) — New Module
- New local entity + cloud table + `/api/v1/phonebook` CRUD & `/bulk` endpoints.
- Fields: First name, Last name, Phone number, Company, Internal extension.
- **Excel import**: native `.xlsx` parser (ZipInputStream + XmlPullParser, zero dependencies) plus `.csv` fallback; bilingual header detection (نام/نام خانوادگی/شماره/شرکت/داخلی) with positional fallback; Append or Replace-all modes (Creator/Admin only).
- Manual add/edit/delete for permitted roles; quick search, company filter chips, one-tap dial and copy-friendly monospace extension badges.

### Academy & Helpdesk — New Graphical Content
- **6 new CyberKid-style visual maps** in the Academy: Kerberos Deep-Dive, AD Command Arsenal, AD Audit & Defense, OSPF vs BGP, IPsec VPN Phases, Windows Net Commands (all with copy-to-clipboard commands, bilingual).
- **Helpdesk visual mode**: new toggle between classic triage runbooks and 6 graphical decision-tree flow maps (Internet Down, RDP Not Connecting, Slow Network, AD Account Lockout, Printer Offline, ESXi Host Down).

### Platform & Release
- `versionName 2.0.1`, `versionCode 201`.
- CI now builds a **release APK** (stable signing via committed debug keystore fallback), a native **`NetOpsHub-Windows-Setup.exe`** NSIS installer bundling the APK + WSA helper, the portable WSA zip and the full source bundle, and publishes them automatically to GitHub Releases.
- Migration `0002_security_phonebook_updates.sql` (users.password_salt, sessions.token_hash, phonebook, app_releases).

### Upgrade notes
1. Deploy the Worker, then run migration 0002 against remote D1.
2. Rotate the seeded account passwords from in-app User Management (v1.x passwords were documented in the repo and must be considered burned).
3. Users will see a one-time re-login (all old sessions invalidated by migration).
