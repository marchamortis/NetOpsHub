-- NetOps Hub Cloudflare D1 Database Schema
-- Master Schema Definition

CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    display_name TEXT,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('Creator', 'Admin', 'Editor', 'Viewer')),
    is_active INTEGER DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT,
    updated_by TEXT,
    last_login_at DATETIME
);

CREATE TABLE IF NOT EXISTS sessions (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    token TEXT UNIQUE NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sites (
    id TEXT PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vcenters (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    ip TEXT,
    site TEXT NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS servers (
    id TEXT PRIMARY KEY,
    server_num INTEGER,
    vcenter TEXT,
    server_host TEXT,
    name TEXT NOT NULL,
    ip TEXT,
    service TEXT,
    port_rdp TEXT,
    status TEXT DEFAULT 'ok',
    site TEXT NOT NULL,
    os TEXT,
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT,
    updated_by TEXT
);

CREATE TABLE IF NOT EXISTS rahkaran_companies (
    id TEXT PRIMARY KEY,
    row_no INTEGER,
    name_fa TEXT NOT NULL,
    ip TEXT NOT NULL,
    port_app TEXT,
    port_web TEXT,
    url TEXT,
    service_name TEXT,
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS academy_categories (
    id TEXT PRIMARY KEY,
    slug TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    name_fa TEXT,
    icon TEXT,
    description TEXT,
    order_index INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS academy_articles (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    title_fa TEXT,
    summary TEXT,
    summary_fa TEXT,
    content TEXT NOT NULL,
    category_slug TEXT NOT NULL,
    subcategory TEXT,
    vendor TEXT,
    difficulty TEXT DEFAULT 'Intermediate',
    tags TEXT,
    commands_json TEXT,
    warnings TEXT,
    expected_results TEXT,
    verification TEXT,
    troubleshooting TEXT,
    related_articles TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT,
    updated_by TEXT
);

CREATE TABLE IF NOT EXISTS ports (
    id TEXT PRIMARY KEY,
    port_number INTEGER NOT NULL,
    protocol TEXT NOT NULL,
    service TEXT NOT NULL,
    description TEXT,
    common_usage TEXT,
    security_notes TEXT
);

CREATE TABLE IF NOT EXISTS http_error_codes (
    id TEXT PRIMARY KEY,
    code INTEGER NOT NULL,
    category TEXT NOT NULL,
    name TEXT NOT NULL,
    meaning TEXT NOT NULL,
    causes TEXT,
    troubleshooting TEXT,
    example TEXT
);

CREATE TABLE IF NOT EXISTS wireshark_filters (
    id TEXT PRIMARY KEY,
    filter_syntax TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    purpose TEXT,
    example TEXT,
    use_case TEXT
);

CREATE TABLE IF NOT EXISTS favorites (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    article_id TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, article_id),
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS personal_notes (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    article_id TEXT,
    title TEXT,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id TEXT PRIMARY KEY,
    user_id TEXT,
    username TEXT,
    action TEXT NOT NULL,
    resource_type TEXT NOT NULL,
    resource_id TEXT,
    details TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for high performance
CREATE INDEX IF NOT EXISTS idx_servers_ip ON servers(ip);
CREATE INDEX IF NOT EXISTS idx_servers_site ON servers(site);
CREATE INDEX IF NOT EXISTS idx_servers_name ON servers(name);
CREATE INDEX IF NOT EXISTS idx_servers_service ON servers(service);
CREATE INDEX IF NOT EXISTS idx_rahkaran_name ON rahkaran_companies(name_fa);
CREATE INDEX IF NOT EXISTS idx_articles_category ON academy_articles(category_slug);
CREATE INDEX IF NOT EXISTS idx_ports_num ON ports(port_number);
CREATE INDEX IF NOT EXISTS idx_ports_service ON ports(service);
CREATE INDEX IF NOT EXISTS idx_http_code ON http_error_codes(code);
CREATE INDEX IF NOT EXISTS idx_sessions_token ON sessions(token);
CREATE INDEX IF NOT EXISTS idx_favorites_user ON favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_notes_user ON personal_notes(user_id);


-- v2.0.1 hardening: PBKDF2 per-user salt + hashed session tokens
-- (columns added by migration 0002: users.password_salt, sessions.token_hash)

CREATE TABLE IF NOT EXISTS phonebook (
    id TEXT PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT DEFAULT '',
    phone TEXT DEFAULT '',
    company TEXT DEFAULT '',
    extension TEXT DEFAULT '',
    notes TEXT DEFAULT '',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT,
    updated_by TEXT
);
CREATE INDEX IF NOT EXISTS idx_phonebook_company ON phonebook(company);
CREATE INDEX IF NOT EXISTS idx_phonebook_last ON phonebook(last_name);

CREATE TABLE IF NOT EXISTS app_releases (
    id TEXT PRIMARY KEY,
    version_code INTEGER NOT NULL,
    version_name TEXT NOT NULL,
    changelog_en TEXT DEFAULT '',
    changelog_fa TEXT DEFAULT '',
    apk_url TEXT DEFAULT '',
    exe_url TEXT DEFAULT '',
    mandatory INTEGER DEFAULT 0,
    published INTEGER DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
