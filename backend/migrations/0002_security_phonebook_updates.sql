-- Migration 0002: Security hardening (v2.0.1) + Phone Book + OTA App Releases
--
-- SECURITY NOTES (v2.0.1 hardening):
--   * Legacy SHA-256 password hashes (64-char hex, static global salt) are kept
--     ONLY for backward compatibility: on the next successful login each account
--     is transparently upgraded by the Worker to PBKDF2-SHA256 (100k iterations,
--     per-user random salt). No password reset is required for the upgrade.
--   * Sessions now store a SHA-256 hash of the bearer token (token_hash column).
--     Plaintext tokens are never persisted; existing plaintext tokens become
--     invalid after this migration (all users simply log in again).
--   * The static creator "master token" (cfat_*) auth bypass is REMOVED in the
--     Worker code with this release.
--   * After deploying, rotate the seeded passwords from User Management inside
--     the app (they were documented in migration 0001 and must be considered
--     burned). v2.0.1 enforces strong combination passwords.

-- 1. Users: per-user salt column for PBKDF2
ALTER TABLE users ADD COLUMN password_salt TEXT DEFAULT '';

-- 2. Sessions: store only token hashes
ALTER TABLE sessions ADD COLUMN token_hash TEXT;

-- Drop any plaintext/legacy session tokens (forces a clean re-login on update)
DELETE FROM sessions;

-- 3. Phone Book (دفترچه تلفن)
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

-- 4. OTA in-app update releases
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
