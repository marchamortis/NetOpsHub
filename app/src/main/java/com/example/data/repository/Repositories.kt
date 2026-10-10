package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.*
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.security.SecureVault
import com.example.ui.theme.NetOpsThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("netops_auth_prefs", Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow<Boolean>(
        prefs.getBoolean("is_logged_in", false)
    )
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(
        if (prefs.getBoolean("is_logged_in", false)) loadUser() else null
    )
    val currentUser = _currentUser.asStateFlow()

    private fun loadUser(): User {
        // v2.0.1: no hardcoded creator fallback - only real persisted users
        val username = prefs.getString("username", "") ?: ""
        val role = prefs.getString("role", "Viewer") ?: "Viewer"
        val displayName = prefs.getString("display_name", username) ?: username
        val email = prefs.getString("email", "") ?: ""
        return User("u_$username", username, displayName, email, role)
    }

    fun login(user: UserEntity, rememberMe: Boolean = true) {
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("username", user.username)
            .putString("role", user.role)
            .putString("display_name", user.displayName)
            .putString("email", user.email)
            .apply()
        _currentUser.value = User(user.id, user.username, user.displayName, user.email, user.role)
        _isLoggedIn.value = true
    }

    fun getLastUsername(): String? = prefs.getString("username", null)

    fun logout() {
        // v2.0.1: logging out also destroys the encrypted server session
        SecureVault.clearServerSession(context)
        prefs.edit().putBoolean("is_logged_in", false).apply()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    /**
     * UI-preview only role switch (Appearance settings). v2.0.1 note:
     * authorization is enforced by the Cloudflare Worker for every request,
     * so this flag can no longer grant any real privilege.
     */
    fun setRole(role: UserRole) {
        val (username, dispName) = when (role) {
            UserRole.CREATOR -> "creator" to "Creator (System Owner)"
            UserRole.ADMIN -> "admin" to "Lead Infrastructure Admin"
            UserRole.EDITOR -> "editor" to "Network Operations Editor"
            UserRole.VIEWER -> "viewer" to "NOC Telemetry Viewer"
        }
        val user = User("u_$username", username, dispName, "$username@netopshub.internal", role.display)
        prefs.edit()
            .putString("username", username)
            .putString("role", role.display)
            .putString("display_name", dispName)
            .putString("email", user.email)
            .apply()
        _currentUser.value = user
    }

    fun getUserRole(): UserRole {
        val r = _currentUser.value?.role ?: "Viewer"
        return UserRole.fromString(r)
    }

    fun isCreator(): Boolean {
        return getUserRole() == UserRole.CREATOR
    }

    fun isAdmin(): Boolean {
        return getUserRole() == UserRole.ADMIN || getUserRole() == UserRole.CREATOR
    }

    fun isPermittedToEdit(): Boolean {
        return getUserRole() != UserRole.VIEWER
    }

    // ------------------------------------------------------------------
    // v2.0.1: encrypted server session token (Keystore-backed)
    // ------------------------------------------------------------------
    fun getSessionToken(): String? = SecureVault.loadServerSession(context)?.token

    fun saveServerSession(token: String, username: String, expiresAtMillis: Long) {
        SecureVault.saveServerSession(context, token, username, expiresAtMillis)
    }
}

class AppearanceRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("netops_appearance_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        NetOpsThemeMode.valueOf(prefs.getString("theme", NetOpsThemeMode.CYBER_CYAN.name) ?: NetOpsThemeMode.CYBER_CYAN.name)
    )
    val themeMode = _themeMode.asStateFlow()

    private val _backgroundUri = MutableStateFlow<String?>(loadPersistentBackground())
    val backgroundUri = _backgroundUri.asStateFlow()

    private fun loadPersistentBackground(): String? {
        val localBg = java.io.File(context.filesDir, "custom_app_bg.jpg")
        if (localBg.exists() && localBg.length() > 0) {
            return android.net.Uri.fromFile(localBg).toString()
        }
        val saved = prefs.getString("bg_uri", null) ?: return null
        if (saved.startsWith("file://")) {
            val path = saved.removePrefix("file://")
            val f = java.io.File(path)
            if (f.exists() && f.length() > 0) {
                return saved
            }
        }
        return saved
    }

    private val _glassOpacity = MutableStateFlow(
        prefs.getFloat("glass_opacity", 0.65f)
    )
    val glassOpacity = _glassOpacity.asStateFlow()

    private val _darkOverlay = MutableStateFlow(
        prefs.getFloat("dark_overlay", 0.70f)
    )
    val darkOverlay = _darkOverlay.asStateFlow()

    private val _language = MutableStateFlow(
        prefs.getString("app_lang", "en") ?: "en" // Default English per user request
    )
    val language = _language.asStateFlow()

    private val _cloudflareUrl = MutableStateFlow(
        prefs.getString("cf_url", "https://netops-hub-api.mortavox.workers.dev/api/v1/") ?: "https://netops-hub-api.mortavox.workers.dev/api/v1/"
    )
    val cloudflareUrl = _cloudflareUrl.asStateFlow()

    // v2.0.1 SECURITY: the hardcoded creator "master token" secret was removed.
    // This field is now only an OPTIONAL manual override; the real session token
    // is issued by /auth/login and stored encrypted in SecureVault.
    private val _cloudflareToken = MutableStateFlow(
        prefs.getString("cf_token", "") ?: ""
    )
    val cloudflareToken = _cloudflareToken.asStateFlow()

    private val _autoSync = MutableStateFlow(
        prefs.getBoolean("auto_sync", true)
    )
    val autoSync = _autoSync.asStateFlow()

    fun setAutoSync(enabled: Boolean) {
        _autoSync.value = enabled
        prefs.edit().putBoolean("auto_sync", enabled).commit()
    }

    fun setCloudflareAccountId(accountId: String) {
        _cloudflareAccountId.value = accountId
        prefs.edit().putString("cf_account_id", accountId).commit()
    }

    private val _cloudflareAccountId = MutableStateFlow(
        prefs.getString("cf_account_id", "") ?: ""
    )
    val cloudflareAccountId = _cloudflareAccountId.asStateFlow()

    fun setTheme(mode: NetOpsThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme", mode.name).commit()
    }

    fun setBackgroundUri(uri: String?) {
        if (uri.isNullOrBlank() || uri == "default") {
            try {
                val localBg = java.io.File(context.filesDir, "custom_app_bg.jpg")
                if (localBg.exists()) localBg.delete()
            } catch (_: Exception) {}
            _backgroundUri.value = null
            prefs.edit().remove("bg_uri").commit()
            return
        }

        try {
            if (uri.startsWith("content://")) {
                val sourceUri = android.net.Uri.parse(uri)
                val localFile = java.io.File(context.filesDir, "custom_app_bg.jpg")
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    localFile.outputStream().use { output ->
                        input.copyTo(output)
                        output.flush()
                    }
                }
                if (localFile.exists() && localFile.length() > 0) {
                    val persistentUri = android.net.Uri.fromFile(localFile).toString()
                    _backgroundUri.value = persistentUri
                    prefs.edit().putString("bg_uri", persistentUri).commit()
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        _backgroundUri.value = uri
        prefs.edit().putString("bg_uri", uri).commit()
    }

    fun setGlassOpacity(value: Float) {
        val safe = value.coerceIn(0.2f, 0.95f)
        _glassOpacity.value = safe
        prefs.edit().putFloat("glass_opacity", safe).commit()
    }

    fun setDarkOverlay(value: Float) {
        val safe = value.coerceIn(0.1f, 0.95f)
        _darkOverlay.value = safe
        prefs.edit().putFloat("dark_overlay", safe).commit()
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        prefs.edit().putString("app_lang", lang).commit()
    }

    fun setCloudflareUrl(url: String) {
        _cloudflareUrl.value = url
        prefs.edit().putString("cf_url", url).commit()
    }

    fun setCloudflareToken(token: String) {
        _cloudflareToken.value = token
        prefs.edit().putString("cf_token", token).commit()
    }

    fun resetAppearance() {
        setTheme(NetOpsThemeMode.CYBER_CYAN)
        setBackgroundUri(null)
        setGlassOpacity(0.65f)
        setDarkOverlay(0.70f)
        setCloudflareUrl("https://netops-hub-api.mortavox.workers.dev/api/v1/")
    }
}

class NetOpsRepository(
    private val appContext: Context,
    private val dao: NetOpsDao
) {

    suspend fun ensureSeeded() {
        if (dao.getServerCount() == 0) {
            dao.insertSites(SeedData.sites)
            dao.insertVcenters(SeedData.vcenters)
            dao.insertServers(SeedData.servers)
            dao.insertRahkaran(SeedData.rahkaranCompanies)
            dao.insertArticles(SeedData.articles)
            dao.insertPorts(SeedData.ports)
            dao.insertHttpErrors(SeedData.httpErrors)
            dao.insertWiresharkFilters(SeedData.wiresharkFilters)
            dao.insertUsers(SeedData.defaultUsers)
            dao.insertPhoneEntries(SeedData.phonebookEntries)
        }
        if (dao.getUserCount() == 0) {
            dao.insertUsers(SeedData.defaultUsers)
        }
        if (dao.getPhonebookCount() == 0) {
            dao.insertPhoneEntries(SeedData.phonebookEntries)
        }
    }

    // ------------------------------------------------------------------
    // v2.0.1: every privileged API call now requires the session token
    // issued by /auth/login (no static secrets in code).
    // ------------------------------------------------------------------
    private fun authHeader(token: String?): String? {
        val t = token?.trim()
        return if (!t.isNullOrBlank()) "Bearer $t" else null
    }

    // User Management (Creator & Admin) with Real-Time Cloudflare D1 Synchronization
    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun createUser(
        user: UserEntity,
        workerUrl: String? = null,
        token: String? = null
    ): Result<Unit> {
        dao.insertUser(
            if (user.plainPassword.isNotBlank())
                user.copy(passwordHash = SecureVault.hashPassword(user.plainPassword))
            else user
        )
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"
        try {
            val api = com.example.data.remote.ApiClient.getService(targetUrl)
            val req = com.example.data.model.CreateUserRequest(
                username = user.username,
                display_name = user.displayName,
                email = user.email,
                password = user.plainPassword, // plaintext travels once over TLS; Room keeps only the PBKDF2 hash
                role = user.role,
                must_change_password = user.mustChangePassword
            )
            val header = authHeader(token)
                ?: return Result.failure(Exception("Session required: sign in first"))
            api.createUser(header, req)
        } catch (e: Exception) {
            // Keep local creation resiliently; server copy retried on next sync
        }
        return Result.success(Unit)
    }

    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)

    suspend fun updateUserRole(
        userId: String,
        newRole: String,
        workerUrl: String? = null,
        token: String? = null
    ) {
        dao.updateUserRole(userId, newRole)
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"
        val header = authHeader(token) ?: return
        try {
            val api = com.example.data.remote.ApiClient.getService(targetUrl)
            api.updateRole(userId, header, com.example.data.model.UpdateRoleRequest(newRole))
        } catch (e: Exception) {
            // Offline resilience
        }
    }

    suspend fun updateUserStatus(
        userId: String,
        isActive: Boolean,
        workerUrl: String? = null,
        token: String? = null
    ) {
        dao.updateUserStatus(userId, isActive)
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"
        val header = authHeader(token) ?: return
        try {
            val api = com.example.data.remote.ApiClient.getService(targetUrl)
            api.updateStatus(userId, header, com.example.data.model.UpdateStatusRequest(isActive))
        } catch (e: Exception) {
            // Offline resilience
        }
    }

    suspend fun updateUserPassword(userId: String, passwordHash: String) =
        dao.updateUserPassword(userId, passwordHash)

    suspend fun updateUserPasswordAndFlag(
        userId: String,
        password: String,
        mustChange: Boolean,
        workerUrl: String? = null,
        token: String? = null
    ) {
        // v2.0.1: local Room stores ONLY the PBKDF2 hash, never the plaintext
        dao.updateUserPasswordAndFlag(userId, SecureVault.hashPassword(password), mustChange)
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"
        val header = authHeader(token) ?: return
        try {
            val api = com.example.data.remote.ApiClient.getService(targetUrl)
            api.updatePassword(userId, header, com.example.data.model.UpdatePasswordRequest(password, mustChange))
        } catch (e: Exception) {
            // Offline resilience
        }
    }

    suspend fun deleteUser(
        user: UserEntity,
        workerUrl: String? = null,
        token: String? = null
    ) {
        dao.deleteUser(user)
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"
        val header = authHeader(token) ?: return
        try {
            val api = com.example.data.remote.ApiClient.getService(targetUrl)
            api.deleteUser(user.id, header)
        } catch (e: Exception) {
            // Offline resilience
        }
    }

    /**
     * v2.0.1 authentication:
     *  1) Online-first against the Cloudflare Worker. On success the session
     *     token is stored ENCRYPTED (SecureVault/Keystore) and the local user
     *     record keeps only a PBKDF2 hash of the password for offline unlock.
     *  2) Offline fallback verifies the PBKDF2 hash stored in Room.
     * All legacy backdoor passwords ("Creator@NetOps2026", "123456", blank)
     * have been removed.
     */
    suspend fun authenticate(
        username: String,
        password: String,
        workerUrl: String? = null
    ): Result<UserEntity> {
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"

        // 1. Online First: Authenticate directly against Cloudflare API
        try {
            val api = com.example.data.remote.ApiClient.getService(targetUrl)
            val resp = api.login(com.example.data.model.LoginRequest(trimmedUsername, trimmedPassword))
            if (resp.isSuccessful && resp.body()?.user != null) {
                val remoteUser = resp.body()!!.user!!
                val sessionToken = resp.body()!!.token
                val expiresAt = resp.body()!!.expires_at
                if (!sessionToken.isNullOrBlank()) {
                    val expiryMillis = parseServerExpiry(expiresAt) ?: (System.currentTimeMillis() + 24 * 60 * 60 * 1000L)
                    SecureVault.saveServerSession(appContext, sessionToken, remoteUser.username, expiryMillis)
                }

                val existingLocal = dao.getUserByUsername(remoteUser.username)
                val userEntity = UserEntity(
                    id = remoteUser.id,
                    username = remoteUser.username,
                    displayName = if (remoteUser.display_name.isNotBlank()) remoteUser.display_name else remoteUser.username,
                    email = remoteUser.email,
                    role = remoteUser.role,
                    isActive = remoteUser.is_active,
                    passwordHash = SecureVault.hashPassword(trimmedPassword),
                    mustChangePassword = remoteUser.must_change_password,
                    createdAt = existingLocal?.createdAt ?: System.currentTimeMillis()
                )
                dao.insertUser(userEntity)
                return Result.success(userEntity)
            }
        } catch (e: Exception) {
            // Offline resilience: Fallback to local Room database
        }

        // 2. Offline Fallback: verify PBKDF2 hash stored locally
        val user = dao.getUserByUsername(trimmedUsername)
            ?: return Result.failure(Exception("کاربری با شناسه '$trimmedUsername' در سیستم ثبت نشده است / User not found."))

        if (!user.isActive) {
            return Result.failure(Exception("این حساب کاربری غیرفعال است / Account is disabled."))
        }

        // v2.0.1: strict PBKDF2 verification only - no backdoor passwords
        val passwordValid = SecureVault.verifyPassword(trimmedPassword, user.passwordHash)

        if (!passwordValid) {
            return Result.failure(Exception("رمز عبور وارد شده نادرست است / Invalid password."))
        }

        return Result.success(user)
    }

    private fun parseServerExpiry(raw: String?): Long? {
        // Server format: "yyyy-MM-dd HH:mm:ss" (UTC) - tolerant parsing; on any
        // failure we fall back to a fixed 24h window.
        if (raw.isNullOrBlank()) return null
        return try {
            val fmt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
            fmt.timeZone = java.util.TimeZone.getTimeZone("UTC")
            fmt.parse(raw)?.time
        } catch (_: Exception) {
            null
        }
    }

    /**
     * v2.0.1 biometric login: ONLY the enrolled user can be restored, and only
     * when BiometricPrompt (CryptoObject) succeeded in LoginScreen. The account
     * must still exist and be active.
     */
    suspend fun biometricAuthenticate(
        username: String,
        workerUrl: String? = null
    ): Result<UserEntity> {
        val trimmed = username.trim()
        val local = dao.getUserByUsername(trimmed)
        if (local != null) {
            if (!local.isActive) {
                return Result.failure(Exception("این حساب کاربری غیرفعال است / Account is disabled."))
            }
            return Result.success(local)
        }
        // Not cached locally: verify against the server session
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"
        val header = authHeader(SecureVault.loadServerSession(appContext)?.token)
        if (header != null) {
            try {
                val api = com.example.data.remote.ApiClient.getService(targetUrl)
                val resp = api.getMe(header)
                if (resp.isSuccessful && resp.body()?.user != null) {
                    val u = resp.body()!!.user!!
                    if (u.username.equals(trimmed, ignoreCase = true) && u.is_active) {
                        val entity = UserEntity(
                            id = u.id,
                            username = u.username,
                            displayName = if (u.display_name.isNotBlank()) u.display_name else u.username,
                            email = u.email,
                            role = u.role,
                            isActive = true,
                            passwordHash = ""
                        )
                        dao.insertUser(entity)
                        return Result.success(entity)
                    }
                }
            } catch (_: Exception) {}
        }
        return Result.failure(Exception("کاربر '$trimmed' در سیستم یافت نشد / User '$trimmed' not found."))
    }

    // Online Knowledge Sync & Cloudflare Integration
    suspend fun syncOnlineKnowledge(workerUrl: String? = null, token: String? = null): Int {
        // First, guarantee all rich bundled master articles are up to date locally
        dao.insertArticles(SeedData.articles)

        // Real-Time Sync of all company infrastructure and knowledge from Cloudflare
        if (!workerUrl.isNullOrBlank()) {
            syncRealtimeCloudflare(workerUrl, token)
        }
        return SeedData.articles.size
    }

    suspend fun syncRealtimeCloudflare(workerUrl: String? = null, token: String? = null): Result<Int> {
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"
        val header = authHeader(token) ?: SecureVault.loadServerSession(appContext)?.token?.let { "Bearer $it" }
            ?: return Result.failure(Exception("Session required: sign in first"))
        return try {
            val api = com.example.data.remote.ApiClient.getService(targetUrl)
            val resp = api.syncAll(header)
            if (!resp.isSuccessful || resp.body() == null) {
                return Result.failure(Exception("Cloudflare sync failed: HTTP ${resp.code()}"))
            }
            val body = resp.body()!!
            var totalCount = 0

            // 1. Sync Servers
            body.servers?.let { remoteServers ->
                if (remoteServers.isNotEmpty()) {
                    val serverEntities = remoteServers.map { s ->
                        ServerEntity(
                            id = s.id,
                            serverNum = s.server_num ?: 0,
                            vcenter = s.vcenter ?: "",
                            serverHost = s.server_host ?: "",
                            name = s.name,
                            ip = s.ip ?: "",
                            service = s.service ?: "",
                            portRdp = s.port_rdp ?: "-",
                            status = s.status ?: "ok",
                            site = s.site,
                            os = s.os ?: "Windows/Linux",
                            notes = s.notes ?: ""
                        )
                    }
                    dao.insertServers(serverEntities)
                    totalCount += serverEntities.size
                }
            }

            // 2. Sync Sites
            body.sites?.let { remoteSites ->
                if (remoteSites.isNotEmpty()) {
                    val siteEntities = remoteSites.map { SiteEntity(it.id, it.name, it.description ?: "") }
                    dao.insertSites(siteEntities)
                    totalCount += siteEntities.size
                }
            }

            // 3. Sync vCenters
            body.vcenters?.let { remoteVc ->
                if (remoteVc.isNotEmpty()) {
                    val vcEntities = remoteVc.map { VcenterEntity(it.id, it.name, it.ip ?: "", it.site ?: "", it.description ?: "") }
                    dao.insertVcenters(vcEntities)
                    totalCount += vcEntities.size
                }
            }

            // 4. Sync Rahkaran
            body.rahkaran?.let { remoteRk ->
                if (remoteRk.isNotEmpty()) {
                    val rkEntities = remoteRk.map {
                        RahkaranEntity(it.id, it.row_no ?: 0, it.name_fa, it.ip, it.port_app ?: "", it.port_web ?: "", it.url ?: "", it.service_name ?: "", it.notes ?: "")
                    }
                    dao.insertRahkaran(rkEntities)
                    totalCount += rkEntities.size
                }
            }

            // 5. Sync Articles
            body.articles?.let { remoteArticles ->
                if (remoteArticles.isNotEmpty()) {
                    val artEntities = remoteArticles.map { r ->
                        ArticleEntity(
                            id = r.id,
                            title = r.title,
                            titleFa = r.title_fa ?: "",
                            summary = r.summary ?: "",
                            summaryFa = r.summary_fa ?: "",
                            content = r.content,
                            categorySlug = r.category_slug,
                            subcategory = r.subcategory ?: "",
                            vendor = r.vendor ?: "",
                            difficulty = r.difficulty ?: "Intermediate",
                            tags = r.tags ?: "",
                            commandsJson = r.commands_json ?: "[]",
                            warnings = r.warnings ?: "",
                            expectedResults = r.expected_results ?: "",
                            verification = r.verification ?: "",
                            troubleshooting = r.troubleshooting ?: ""
                        )
                    }
                    dao.insertArticles(artEntities)
                    totalCount += artEntities.size
                }
            }

            // 6. Sync Users from Cloudflare D1 (emails are no longer distributed to all roles)
            body.users?.let { remoteUsers ->
                if (remoteUsers.isNotEmpty()) {
                    for (ru in remoteUsers) {
                        val existing = dao.getUserById(ru.id) ?: dao.getUserByUsername(ru.username)
                        val entity = UserEntity(
                            id = ru.id,
                            username = ru.username,
                            displayName = if (!ru.display_name.isNullOrBlank()) ru.display_name else ru.username,
                            email = existing?.email ?: "${ru.username}@netopshub.internal",
                            role = ru.role,
                            isActive = (ru.is_active ?: 1) == 1,
                            passwordHash = existing?.passwordHash ?: "",
                            mustChangePassword = (ru.must_change_password ?: 1) == 1,
                            createdAt = existing?.createdAt ?: System.currentTimeMillis()
                        )
                        dao.insertUser(entity)
                    }
                    totalCount += remoteUsers.size
                }
            }

            // 7. Sync Phone Book (v2.0.1)
            body.phonebook?.let { remotePhones ->
                if (remotePhones.isNotEmpty()) {
                    val phoneEntities = remotePhones.map { p ->
                        PhoneEntryEntity(
                            id = p.id ?: "ph_${System.currentTimeMillis()}",
                            firstName = p.first_name,
                            lastName = p.last_name ?: "",
                            phone = p.phone ?: "",
                            company = p.company ?: "",
                            extension = p.extension ?: "",
                            notes = p.notes ?: ""
                        )
                    }
                    dao.insertPhoneEntries(phoneEntities)
                    totalCount += phoneEntities.size
                }
            }

            Result.success(totalCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ------------------------------------------------------------------
    // Phone Book operations (v2.0.1)
    // ------------------------------------------------------------------
    fun getAllPhoneEntries(): Flow<List<PhoneEntryEntity>> = dao.getAllPhoneEntries()
    fun searchPhoneEntries(q: String): Flow<List<PhoneEntryEntity>> = dao.searchPhoneEntries(q)

    suspend fun savePhoneEntry(entry: PhoneEntryEntity, workerUrl: String? = null, token: String? = null) {
        dao.insertPhoneEntry(entry)
        val header = authHeader(token) ?: return
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"
        try {
            val api = com.example.data.remote.ApiClient.getService(targetUrl)
            api.createPhoneEntry(header, entry.toRemote())
        } catch (_: Exception) { /* offline resilience */ }
    }

    suspend fun updatePhoneEntry(entry: PhoneEntryEntity, workerUrl: String? = null, token: String? = null) {
        dao.updatePhoneEntry(entry)
        val header = authHeader(token) ?: return
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"
        try {
            val api = com.example.data.remote.ApiClient.getService(targetUrl)
            api.updatePhoneEntry(entry.id, header, entry.toRemote())
        } catch (_: Exception) { /* offline resilience */ }
    }

    suspend fun deletePhoneEntry(entry: PhoneEntryEntity, workerUrl: String? = null, token: String? = null) {
        dao.deletePhoneEntry(entry)
        val header = authHeader(token) ?: return
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"
        try {
            val api = com.example.data.remote.ApiClient.getService(targetUrl)
            api.deletePhoneEntry(entry.id, header)
        } catch (_: Exception) { /* offline resilience */ }
    }

    suspend fun bulkImportPhonebook(entries: List<PhoneEntryEntity>, replaceAll: Boolean, workerUrl: String? = null, token: String? = null): Int {
        if (replaceAll) dao.clearPhonebook()
        dao.insertPhoneEntries(entries)
        val header = authHeader(token) ?: return entries.size
        val targetUrl = if (!workerUrl.isNullOrBlank()) workerUrl else "https://netops-hub-api.mortavox.workers.dev/api/v1/"
        return try {
            val api = com.example.data.remote.ApiClient.getService(targetUrl)
            val resp = api.bulkImportPhonebook(header, com.example.data.model.PhonebookBulkRequest(entries.map { it.toRemote() }))
            resp.body()?.count ?: entries.size
        } catch (_: Exception) {
            entries.size
        }
    }

    private fun PhoneEntryEntity.toRemote() = com.example.data.model.RemotePhoneEntry(
        id = id,
        first_name = firstName,
        last_name = lastName,
        phone = phone,
        company = company,
        extension = extension,
        notes = notes
    )

    // ------------------------------------------------------------------
    // Servers / Articles / Push (all require live session token)
    // ------------------------------------------------------------------
    suspend fun createServer(
        server: ServerEntity,
        workerUrl: String? = null,
        token: String? = null
    ): Result<Unit> {
        dao.insertServer(server)
        if (!workerUrl.isNullOrBlank()) {
            val header = authHeader(token)
            if (header != null) {
                try {
                    val api = com.example.data.remote.ApiClient.getService(workerUrl)
                    val remote = com.example.data.model.RemoteServer(
                        id = server.id,
                        server_num = server.serverNum,
                        vcenter = server.vcenter,
                        server_host = server.serverHost,
                        name = server.name,
                        ip = server.ip,
                        service = server.service,
                        port_rdp = server.portRdp,
                        status = server.status,
                        site = server.site,
                        os = server.os,
                        notes = server.notes
                    )
                    api.createServer(header, remote)
                } catch (e: Exception) {
                    // Keep local changes
                }
            }
        }
        return Result.success(Unit)
    }

    suspend fun updateServer(
        server: ServerEntity,
        workerUrl: String? = null,
        token: String? = null
    ): Result<Unit> {
        dao.updateServer(server)
        if (!workerUrl.isNullOrBlank()) {
            val header = authHeader(token)
            if (header != null) {
                try {
                    val api = com.example.data.remote.ApiClient.getService(workerUrl)
                    val remote = com.example.data.model.RemoteServer(
                        id = server.id,
                        server_num = server.serverNum,
                        vcenter = server.vcenter,
                        server_host = server.serverHost,
                        name = server.name,
                        ip = server.ip,
                        service = server.service,
                        port_rdp = server.portRdp,
                        status = server.status,
                        site = server.site,
                        os = server.os,
                        notes = server.notes
                    )
                    api.updateServer(server.id, header, remote)
                } catch (e: Exception) {
                    // Keep local changes
                }
            }
        }
        return Result.success(Unit)
    }

    suspend fun deleteServer(
        server: ServerEntity,
        workerUrl: String? = null,
        token: String? = null
    ): Result<Unit> {
        dao.deleteServer(server)
        if (!workerUrl.isNullOrBlank()) {
            val header = authHeader(token)
            if (header != null) {
                try {
                    val api = com.example.data.remote.ApiClient.getService(workerUrl)
                    api.deleteServer(server.id, header)
                } catch (e: Exception) {
                    // Keep local deletion
                }
            }
        }
        return Result.success(Unit)
    }

    suspend fun createArticle(
        article: ArticleEntity,
        workerUrl: String? = null,
        token: String? = null
    ) {
        dao.insertArticle(article)
        if (!workerUrl.isNullOrBlank()) {
            val header = authHeader(token)
            if (header != null) {
                try {
                    val api = com.example.data.remote.ApiClient.getService(workerUrl)
                    api.createArticle(header, article.toRemote())
                } catch (e: Exception) {
                    // Keep local changes even if offline
                }
            }
        }
    }

    suspend fun updateArticle(
        article: ArticleEntity,
        workerUrl: String? = null,
        token: String? = null
    ) {
        dao.updateArticle(article)
        if (!workerUrl.isNullOrBlank()) {
            val header = authHeader(token)
            if (header != null) {
                try {
                    val api = com.example.data.remote.ApiClient.getService(workerUrl)
                    api.updateArticle(article.id, header, article.toRemote())
                } catch (e: Exception) {
                    // Keep local changes even if offline
                }
            }
        }
    }

    suspend fun deleteArticle(
        article: ArticleEntity,
        workerUrl: String? = null,
        token: String? = null
    ) {
        dao.deleteArticle(article)
        if (!workerUrl.isNullOrBlank()) {
            val header = authHeader(token)
            if (header != null) {
                try {
                    val api = com.example.data.remote.ApiClient.getService(workerUrl)
                    api.deleteArticle(article.id, header)
                } catch (e: Exception) {
                    // Keep local changes even if offline
                }
            }
        }
    }

    private fun ArticleEntity.toRemote() = com.example.data.model.RemoteArticle(
        id = id,
        title = title,
        title_fa = titleFa,
        summary = summary,
        summary_fa = summaryFa,
        content = content,
        category_slug = categorySlug,
        subcategory = subcategory,
        vendor = vendor,
        difficulty = difficulty,
        tags = tags,
        commands_json = commandsJson,
        warnings = warnings,
        expected_results = expectedResults,
        verification = verification,
        troubleshooting = troubleshooting
    )

    suspend fun pushAllToCloudflare(
        workerUrl: String,
        token: String? = null,
        articlesList: List<ArticleEntity>
    ): Result<Int> {
        val header = authHeader(token)
            ?: return Result.failure(Exception("Session required: sign in first"))
        return try {
            val api = com.example.data.remote.ApiClient.getService(workerUrl)
            val remotes = articlesList.map { it.toRemote() }
            val resp = api.bulkSync(header, com.example.data.model.BulkSyncRequest(remotes))
            if (resp.isSuccessful) {
                Result.success(resp.body()?.count ?: remotes.size)
            } else {
                Result.failure(Exception("Cloudflare responded with HTTP ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchOnlineSources(workerUrl: String? = null): List<com.example.data.model.OnlineSource> {
        if (!workerUrl.isNullOrBlank()) {
            try {
                val api = com.example.data.remote.ApiClient.getService(workerUrl)
                val resp = api.getOnlineSources()
                if (resp.isSuccessful && resp.body()?.sources != null) {
                    return resp.body()!!.sources!!
                }
            } catch (e: Exception) {
                // Fallback to static sources
            }
        }
        return listOf(
            com.example.data.model.OnlineSource(
                id = "src_ms_learn",
                name = "Microsoft Learn & TechNet",
                name_fa = "مرجع اسناد رسمی مایکروسافت",
                category = "Windows & Server",
                url = "https://learn.microsoft.com/en-us/windows-server/",
                description = "Official Windows Server, Active Directory, PowerShell, and Client OS documentation.",
                description_fa = "مستندات و کتابخانه‌های عیب‌یابی ویندوز سرور و اکتیو دایرکتوری"
            ),
            com.example.data.model.OnlineSource(
                id = "src_cisco",
                name = "Cisco Networking Documentation",
                name_fa = "پرتال رسمی شبکه سیسکو",
                category = "Routing & Switching",
                url = "https://www.cisco.com/c/en/us/support/index.html",
                description = "Configuration guides for IOS XE, Catalyst, Nexus, OSPF, BGP, and VLAN trunking.",
                description_fa = "راهنماهای پیکربندی و کامندهای روتر و سوئیچ‌های سیسکو"
            ),
            com.example.data.model.OnlineSource(
                id = "src_mikrotik",
                name = "MikroTik Documentation & Wiki",
                name_fa = "دانشنامه رسمی میکروتیک",
                category = "RouterOS & Firewall",
                url = "https://help.mikrotik.com/docs/",
                description = "Authoritative RouterOS v7 manuals, firewall mangle, queue trees, and WireGuard tunnels.",
                description_fa = "مستندات روتربورد، تنظیمات فایروال و مسیریابی پیشرفته میکروتیک"
            ),
            com.example.data.model.OnlineSource(
                id = "src_fortinet",
                name = "Fortinet FortiGuard Labs & Cookbook",
                name_fa = "مرجع سناریوهای فورتی‌گیت",
                category = "Enterprise Security",
                url = "https://docs.fortinet.com/",
                description = "FortiOS administration guides, SD-WAN steering, IPsec VPN, and threat prevention.",
                description_fa = "سناریوهای استقرار فایروال فورتی‌گیت، SD-WAN و وی‌پی‌ان‌های امن"
            ),
            com.example.data.model.OnlineSource(
                id = "src_wireshark",
                name = "Wireshark User Guide & Filter Spec",
                name_fa = "راهنمای فیلترهای وایرشارک",
                category = "Packet Analysis",
                url = "https://www.wireshark.org/docs/dfref/",
                description = "Official display filter reference and protocol decoder specifications.",
                description_fa = "مرجع رسمی فیلترهای پیشرفته تحلیل پکت‌های شبکه"
            ),
            com.example.data.model.OnlineSource(
                id = "src_vmware",
                name = "Broadcom VMware Knowledge Base",
                name_fa = "پایگاه دانش وی‌ام‌ویر (VMware KB)",
                category = "Virtualization & Cloud",
                url = "https://knowledge.broadcom.com/external/article?articleNumber=vmware",
                description = "vSphere, ESXi, vCenter troubleshooting, and vMotion migration diagnostics.",
                description_fa = "حل مشکلات و خطاهای هاست‌های ESXi و کلاستر vCenter"
            )
        )
    }

    // Company
    fun getAllServers(): Flow<List<ServerEntity>> = dao.getAllServers()
    fun getServersBySite(site: String): Flow<List<ServerEntity>> = dao.getServersBySite(site)
    fun searchServers(q: String): Flow<List<ServerEntity>> = dao.searchServers(q)

    fun getAllSites(): Flow<List<SiteEntity>> = dao.getAllSites()
    fun getAllVcenters(): Flow<List<VcenterEntity>> = dao.getAllVcenters()

    fun getAllRahkaran(): Flow<List<RahkaranEntity>> = dao.getAllRahkaran()
    fun searchRahkaran(q: String): Flow<List<RahkaranEntity>> = dao.searchRahkaran(q)

    // Academy
    fun getAllArticles(): Flow<List<ArticleEntity>> = dao.getAllArticles()
    fun getArticlesByCategory(category: String): Flow<List<ArticleEntity>> = dao.getArticlesByCategory(category)
    suspend fun getArticleById(id: String): ArticleEntity? = dao.getArticleById(id)
    fun searchArticles(q: String): Flow<List<ArticleEntity>> = dao.searchArticles(q)

    fun getAllPorts(): Flow<List<PortEntity>> = dao.getAllPorts()
    fun searchPorts(q: String): Flow<List<PortEntity>> = dao.searchPorts(q)

    fun getAllHttpErrors(): Flow<List<HttpErrorEntity>> = dao.getAllHttpErrors()
    fun searchHttpErrors(q: String): Flow<List<HttpErrorEntity>> = dao.searchHttpErrors(q)

    fun getAllWiresharkFilters(): Flow<List<WiresharkEntity>> = dao.getAllWiresharkFilters()
    fun searchWiresharkFilters(q: String): Flow<List<WiresharkEntity>> = dao.searchWiresharkFilters(q)

    // Favorites & Notes
    fun getFavorites(): Flow<List<FavoriteEntity>> = dao.getFavorites()
    fun isFavorite(id: String): Flow<Boolean> = dao.isFavorite(id)
    suspend fun toggleFavorite(id: String, isFav: Boolean) {
        if (isFav) dao.removeFavorite(id) else dao.addFavorite(FavoriteEntity("fav_$id", id))
    }

    fun getAllNotes(): Flow<List<NoteEntity>> = dao.getAllNotes()
    suspend fun saveNote(note: NoteEntity) = dao.saveNote(note)
    suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)
}

