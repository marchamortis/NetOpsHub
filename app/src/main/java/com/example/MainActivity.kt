package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.data.local.NetOpsDatabase
import com.example.data.model.UserRole
import com.example.data.repository.AppearanceRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.NetOpsRepository
import com.example.data.security.BiometricSession
import com.example.data.update.UpdateManager
import com.example.ui.components.AppBackgroundScaffold
import com.example.ui.screens.*
import com.example.ui.theme.NetOpsTheme
import kotlinx.coroutines.launch

enum class Screen {
    DASHBOARD,
    COMPANY,
    ACADEMY,
    HELPDESK,
    TOOLS,
    SEARCH,
    FAVORITES,
    SETTINGS,
    USER_MANAGEMENT,
    PHONEBOOK
}

class MainActivity : androidx.fragment.app.FragmentActivity() {

    private lateinit var database: NetOpsDatabase
    private lateinit var repository: NetOpsRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var appearanceRepository: AppearanceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = NetOpsDatabase.getInstance(applicationContext)
        repository = NetOpsRepository(applicationContext, database.dao())
        authRepository = AuthRepository(applicationContext)
        appearanceRepository = AppearanceRepository(applicationContext)

        // Seed check
        lifecycleScope.launch {
            repository.ensureSeeded()
        }

        setContent {
            val themeMode by appearanceRepository.themeMode.collectAsState()
            val backgroundUri by appearanceRepository.backgroundUri.collectAsState()
            val glassOpacity by appearanceRepository.glassOpacity.collectAsState()
            val darkOverlay by appearanceRepository.darkOverlay.collectAsState()
            val language by appearanceRepository.language.collectAsState()
            val cloudflareUrl by appearanceRepository.cloudflareUrl.collectAsState()
            val cloudflareAccountId by appearanceRepository.cloudflareAccountId.collectAsState()
            val cloudflareToken by appearanceRepository.cloudflareToken.collectAsState()
            val autoSync by appearanceRepository.autoSync.collectAsState()

            val currentUser by authRepository.currentUser.collectAsState()
            val isLoggedIn by authRepository.isLoggedIn.collectAsState()
            val currentRole = authRepository.getUserRole()

            // v2.0.1: live server session token (encrypted in SecureVault)
            var sessionToken by remember { mutableStateOf(authRepository.getSessionToken()) }

            val phoneEntries by repository.getAllPhoneEntries().collectAsState(initial = emptyList())

            // v2.0.1 OTA update state
            var updateInfo by remember { mutableStateOf<UpdateManager.UpdateInfo?>(null) }
            var updateDownloading by remember { mutableStateOf(false) }
            var pendingInstallFile by remember { mutableStateOf<java.io.File?>(null) }

            val servers by repository.getAllServers().collectAsState(initial = emptyList())
            val sites by repository.getAllSites().collectAsState(initial = emptyList())
            val vcenters by repository.getAllVcenters().collectAsState(initial = emptyList())
            val rahkaran by repository.getAllRahkaran().collectAsState(initial = emptyList())
            val articles by repository.getAllArticles().collectAsState(initial = emptyList())
            val ports by repository.getAllPorts().collectAsState(initial = emptyList())
            val httpErrors by repository.getAllHttpErrors().collectAsState(initial = emptyList())
            val wiresharkFilters by repository.getAllWiresharkFilters().collectAsState(initial = emptyList())
            val favorites by repository.getFavorites().collectAsState(initial = emptyList())
            val notes by repository.getAllNotes().collectAsState(initial = emptyList())
            val users by repository.getAllUsers().collectAsState(initial = emptyList())

            val coroutineScope = rememberCoroutineScope()
            var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }

            // v2.0.1 OTA: check for a newer version on every launch (public endpoint)
            LaunchedEffect(cloudflareUrl) {
                val info = UpdateManager.checkForUpdate(cloudflareUrl)
                if (info != null && info.hasUpdate) updateInfo = info
            }

            // v2.0.1: auto real-time synchronization now requires an authenticated session.
            // (The previous pre-login anonymous sync leaked infrastructure data - fixed.)
            LaunchedEffect(isLoggedIn, autoSync, cloudflareUrl) {
                if (isLoggedIn && autoSync) {
                    while (true) {
                        try {
                            repository.syncRealtimeCloudflare(cloudflareUrl, sessionToken)
                            sessionToken = authRepository.getSessionToken()
                        } catch (e: Exception) {
                            // Offline resilience: silent catch for network drops
                        }
                        kotlinx.coroutines.delay(20_000) // Poll every 20s for colleague changes
                    }
                }
            }

            // Back handler support
            if (isLoggedIn && currentScreen != Screen.DASHBOARD) {
                BackHandler {
                    currentScreen = Screen.DASHBOARD
                }
            }

            // Direction: RTL for Persian ("fa"), LTR for English ("en")
            val layoutDirection = if (language == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr

            // v2.0.1: biometric session is revoked on logout (done in AuthRepository.logout)
            val biometricEnrolled = remember { BiometricSession.isEnrolled(applicationContext) }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                NetOpsTheme(themeMode = themeMode, glassOpacity = glassOpacity) {
                    AppBackgroundScaffold(
                        backgroundUri = backgroundUri,
                        darkOverlay = darkOverlay,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (!isLoggedIn || currentUser == null) {
                            LoginScreen(
                                currentLang = language,
                                onLangChange = { appearanceRepository.setLanguage(it) },
                                onLoginSuccess = { user, rememberMe ->
                                    authRepository.login(user, rememberMe)
                                    sessionToken = authRepository.getSessionToken()
                                    currentScreen = Screen.DASHBOARD
                                },
                                onAuthenticate = { u, p ->
                                    repository.authenticate(u, p, cloudflareUrl)
                                },
                                onChangePassword = { userId, newPassword ->
                                    repository.updateUserPasswordAndFlag(userId, newPassword, false, cloudflareUrl, sessionToken)
                                },
                                onBiometricLogin = { u ->
                                    repository.biometricAuthenticate(u, cloudflareUrl)
                                },
                                lastUsername = authRepository.getLastUsername()
                            )
                        } else {
                            when (currentScreen) {
                                Screen.DASHBOARD -> DashboardScreen(
                                    onNavigateToCompany = { currentScreen = Screen.COMPANY },
                                    onNavigateToAcademy = { currentScreen = Screen.ACADEMY },
                                    onNavigateToHelpdesk = { currentScreen = Screen.HELPDESK },
                                    onNavigateToTools = { currentScreen = Screen.TOOLS },
                                    onNavigateToSearch = { currentScreen = Screen.SEARCH },
                                    onNavigateToFavorites = { currentScreen = Screen.FAVORITES },
                                    onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                                    onNavigateToUserManagement = { currentScreen = Screen.USER_MANAGEMENT },
                                    onNavigateToPhoneBook = { currentScreen = Screen.PHONEBOOK },
                                    onLogout = { authRepository.logout() },
                                    serverCount = servers.size.coerceAtLeast(79),
                                    articleCount = articles.size.coerceAtLeast(35),
                                    currentRole = currentRole,
                                    currentLang = language
                                )

                                Screen.PHONEBOOK -> PhoneBookScreen(
                                    entries = phoneEntries,
                                    userRole = currentRole,
                                    currentLang = language,
                                    onBack = { currentScreen = Screen.DASHBOARD },
                                    onCreateEntry = { entry ->
                                        coroutineScope.launch {
                                            repository.savePhoneEntry(entry, cloudflareUrl, sessionToken)
                                        }
                                    },
                                    onUpdateEntry = { entry ->
                                        coroutineScope.launch {
                                            repository.updatePhoneEntry(entry, cloudflareUrl, sessionToken)
                                        }
                                    },
                                    onDeleteEntry = { entry ->
                                        coroutineScope.launch {
                                            repository.deletePhoneEntry(entry, cloudflareUrl, sessionToken)
                                        }
                                    },
                                    onImportEntries = { list, replace ->
                                        coroutineScope.launch {
                                            val count = repository.bulkImportPhonebook(list, replace, cloudflareUrl, sessionToken)
                                            android.widget.Toast.makeText(
                                                applicationContext,
                                                "$count contacts imported",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )

                                Screen.HELPDESK -> TroubleshootingHelpdeskScreen(
                                    currentLang = language,
                                    onBack = { currentScreen = Screen.DASHBOARD }
                                )

                                Screen.COMPANY -> CompanyScreen(
                                    servers = servers,
                                    sites = sites,
                                    vcenters = vcenters,
                                    rahkaran = rahkaran,
                                    userRole = currentRole,
                                    onBack = { currentScreen = Screen.DASHBOARD },
                                    onUpdateServer = { server, pushRemote ->
                                        coroutineScope.launch {
                                            repository.updateServer(
                                                server = server,
                                                workerUrl = if (pushRemote) cloudflareUrl else null,
                                                token = sessionToken
                                            )
                                        }
                                    },
                                    onCreateServer = { server, pushRemote ->
                                        coroutineScope.launch {
                                            repository.createServer(
                                                server = server,
                                                workerUrl = if (pushRemote) cloudflareUrl else null,
                                                token = sessionToken
                                            )
                                        }
                                    },
                                    onSyncRealtime = {
                                        coroutineScope.launch {
                                            val res = repository.syncRealtimeCloudflare(cloudflareUrl, sessionToken)
                                            if (res.isSuccess) {
                                                android.widget.Toast.makeText(
                                                    applicationContext,
                                                    "Cloudflare live sync updated ${res.getOrNull()} items",
                                                    android.widget.Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                )

                                Screen.ACADEMY -> AcademyScreen(
                                    articles = articles,
                                    userRole = currentRole,
                                    onNavigateToTools = { currentScreen = Screen.TOOLS },
                                    onBack = { currentScreen = Screen.DASHBOARD },
                                    favoritesIds = favorites.map { it.articleId }.toSet(),
                                    onToggleFavorite = { id, isFav ->
                                        coroutineScope.launch { repository.toggleFavorite(id, isFav) }
                                    },
                                    onSaveNote = { articleId, title, content ->
                                        coroutineScope.launch {
                                            repository.saveNote(
                                                com.example.data.local.NoteEntity(
                                                    id = "note_${System.currentTimeMillis()}",
                                                    title = title,
                                                    content = content,
                                                    articleId = articleId
                                                )
                                            )
                                        }
                                    },
                                    onSyncOnline = {
                                        coroutineScope.launch {
                                            repository.syncOnlineKnowledge(cloudflareUrl)
                                        }
                                    },
                                    onCreateArticle = { article, pushRemote ->
                                        coroutineScope.launch {
                                            repository.createArticle(
                                                article = article,
                                                workerUrl = if (pushRemote) cloudflareUrl else null,
                                                token = sessionToken
                                            )
                                        }
                                    },
                                    onUpdateArticle = { article, pushRemote ->
                                        coroutineScope.launch {
                                            repository.updateArticle(
                                                article = article,
                                                workerUrl = if (pushRemote) cloudflareUrl else null,
                                                token = sessionToken
                                            )
                                        }
                                    },
                                    onDeleteArticle = { article, deleteRemote ->
                                        coroutineScope.launch {
                                            repository.deleteArticle(
                                                article = article,
                                                workerUrl = if (deleteRemote) cloudflareUrl else null,
                                                token = sessionToken
                                            )
                                        }
                                    },
                                    cloudflareUrl = cloudflareUrl,
                                    currentLang = language
                                )

                                Screen.TOOLS -> NetworkToolsScreen(
                                    ports = ports,
                                    httpErrors = httpErrors,
                                    wiresharkFilters = wiresharkFilters,
                                    onBack = { currentScreen = Screen.DASHBOARD }
                                )

                                Screen.SEARCH -> SearchScreen(
                                    servers = servers,
                                    articles = articles,
                                    ports = ports,
                                    onBack = { currentScreen = Screen.DASHBOARD }
                                )

                                Screen.FAVORITES -> FavoritesNotesScreen(
                                    favorites = favorites,
                                    notes = notes,
                                    allArticles = articles,
                                    onDeleteNote = { note ->
                                        coroutineScope.launch { repository.deleteNote(note) }
                                    },
                                    onBack = { currentScreen = Screen.DASHBOARD }
                                )

                                Screen.SETTINGS -> AppearanceSettingsScreen(
                                    currentTheme = themeMode,
                                    onThemeChange = { appearanceRepository.setTheme(it) },
                                    backgroundUri = backgroundUri,
                                    onBackgroundChange = { appearanceRepository.setBackgroundUri(it) },
                                    glassOpacity = glassOpacity,
                                    onGlassOpacityChange = { appearanceRepository.setGlassOpacity(it) },
                                    darkOverlay = darkOverlay,
                                    onDarkOverlayChange = { appearanceRepository.setDarkOverlay(it) },
                                    currentRole = currentRole,
                                    onRoleChange = { authRepository.setRole(it) },
                                    currentLang = language,
                                    onLangChange = { appearanceRepository.setLanguage(it) },
                                    onResetAppearance = { appearanceRepository.resetAppearance() },
                                    cloudflareUrl = cloudflareUrl,
                                    onCloudflareUrlChange = { appearanceRepository.setCloudflareUrl(it) },
                                    cloudflareAccountId = cloudflareAccountId,
                                    onCloudflareAccountIdChange = { appearanceRepository.setCloudflareAccountId(it) },
                                    cloudflareToken = cloudflareToken,
                                    onCloudflareTokenChange = { appearanceRepository.setCloudflareToken(it) },
                                    autoSync = autoSync,
                                    onAutoSyncChange = { appearanceRepository.setAutoSync(it) },
                                    onSyncFromCloudflare = {
                                        coroutineScope.launch {
                                            val count = repository.syncOnlineKnowledge(cloudflareUrl, sessionToken)
                                            android.widget.Toast.makeText(applicationContext, "Synced $count articles from Cloudflare D1", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onPushAllToCloudflare = {
                                        coroutineScope.launch {
                                            val res = repository.pushAllToCloudflare(cloudflareUrl, sessionToken, articles)
                                            if (res.isSuccess) {
                                                android.widget.Toast.makeText(applicationContext, "Successfully uploaded ${res.getOrNull()} articles to Cloudflare D1!", android.widget.Toast.LENGTH_SHORT).show()
                                            } else {
                                                android.widget.Toast.makeText(applicationContext, "Upload to Cloudflare D1 failed: ${res.exceptionOrNull()?.message}", android.widget.Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onNavigateToUserManagement = { currentScreen = Screen.USER_MANAGEMENT },
                                    onLogout = { authRepository.logout() },
                                    onBack = { currentScreen = Screen.DASHBOARD }
                                )

                                Screen.USER_MANAGEMENT -> UserManagementScreen(
                                    users = users,
                                    currentRole = currentRole,
                                    onCreateUser = { newUser ->
                                        coroutineScope.launch {
                                            repository.createUser(newUser, cloudflareUrl, sessionToken)
                                        }
                                    },
                                    onUpdateRole = { userId, newRole ->
                                        coroutineScope.launch {
                                            repository.updateUserRole(userId, newRole, cloudflareUrl, sessionToken)
                                        }
                                    },
                                    onUpdateStatus = { userId, isActive ->
                                        coroutineScope.launch {
                                            repository.updateUserStatus(userId, isActive, cloudflareUrl, sessionToken)
                                        }
                                    },
                                    onUpdatePassword = { userId, newPass, mustChange ->
                                        coroutineScope.launch {
                                            repository.updateUserPasswordAndFlag(userId, newPass, mustChange, cloudflareUrl, sessionToken)
                                        }
                                    },
                                    onDeleteUser = { userToDelete ->
                                        coroutineScope.launch {
                                            repository.deleteUser(userToDelete, cloudflareUrl, sessionToken)
                                        }
                                    },
                                    onBack = { currentScreen = Screen.DASHBOARD }
                                )
                            }
                        }
                    }
                }
            }

            // ==================================================================
            // v2.0.1 OTA IN-APP UPDATE DIALOG
            // Shows a "new version available" message right after entering the app
            // and activates the update flow in-place (DownloadManager + installer).
            // ==================================================================
            updateInfo?.let { info ->
                AppUpdateDialog(
                    info = info,
                    isPersian = language == "fa",
                    isDownloading = updateDownloading,
                    downloadedFile = pendingInstallFile,
                    onDismiss = { if (!info.mandatory) updateInfo = null },
                    onDownload = {
                        if (info.apkUrl.isNotBlank()) {
                            updateDownloading = true
                            val downloadId = UpdateManager.enqueueDownload(applicationContext, info.apkUrl, info.versionName)
                            var receiver: android.content.BroadcastReceiver? = null
                            receiver = UpdateManager.registerDownloadReceiver(applicationContext) { file ->
                                updateDownloading = false
                                if (file != null) {
                                    pendingInstallFile = file
                                } else {
                                    android.widget.Toast.makeText(
                                        applicationContext,
                                        "Download failed. Please retry.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                                try { receiver?.let { applicationContext.unregisterReceiver(it) } } catch (_: Exception) {}
                            }
                        } else {
                            // No direct APK URL: open the GitHub release page
                            try {
                                startActivity(
                                    android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse("https://github.com/marchamortis/NetOpsHub/releases/latest")
                                    )
                                )
                            } catch (_: Exception) {}
                        }
                    },
                    onInstall = { file ->
                        try {
                            UpdateManager.installDownloadedUpdate(applicationContext, file)
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(
                                applicationContext,
                                "Install permission needed: allow \"Install unknown apps\" for NetOps Hub",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            }
        }
    }
}

/**
 * v2.0.1 in-app update dialog - styled with the existing glass/cyber tokens so
 * it blends with the current design language.
 */
@Composable
fun AppUpdateDialog(
    info: UpdateManager.UpdateInfo,
    isPersian: Boolean,
    isDownloading: Boolean,
    downloadedFile: java.io.File?,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    onInstall: (java.io.File) -> Unit
) {
    val tokens = com.example.ui.theme.LocalNetOpsColors.current
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Default.SystemUpdateAlt,
                    contentDescription = null,
                    tint = tokens.primary
                )
                androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
                androidx.compose.material3.Text(
                    text = if (isPersian) "نسخه جدید در دسترس است!" else "New Version Available!",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = tokens.textPrimary
                )
            }
        },
        text = {
            androidx.compose.foundation.layout.Column {
                androidx.compose.material3.Text(
                    text = (if (isPersian) "نسخه " else "Version ") + "v${info.versionName}" +
                        (if (isPersian) " آماده به‌روزرسانی است. همین‌جا آپدیت کنید:" else " is ready. Update right from here:"),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = tokens.textPrimary
                )
                if ((if (isPersian) info.changelogFa else info.changelogEn).isNotBlank()) {
                    androidx.compose.foundation.layout.Spacer(Modifier.height(10.dp))
                    androidx.compose.material3.Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                        color = tokens.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, tokens.glassBorder)
                    ) {
                        androidx.compose.material3.Text(
                            text = if (isPersian) info.changelogFa else info.changelogEn,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = {
                    val f = downloadedFile
                    if (f != null) onInstall(f) else onDownload()
                },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = tokens.primary),
                enabled = !isDownloading
            ) {
                if (isDownloading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = tokens.background
                    )
                    Spacer(Modifier.width(8.dp))
                    androidx.compose.material3.Text(if (isPersian) "در حال دانلود..." else "Downloading...", color = tokens.background)
                } else if (downloadedFile != null) {
                    androidx.compose.material3.Text(
                        if (isPersian) "نصب نسخه جدید" else "Install v${info.versionName}",
                        color = tokens.background,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    androidx.compose.material3.Text(
                        if (isPersian) "به‌روزرسانی همان‌جا" else "Update Now",
                        color = tokens.background,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            if (!info.mandatory) {
                androidx.compose.material3.TextButton(onClick = onDismiss) {
                    androidx.compose.material3.Text(
                        if (isPersian) "بعداً" else "Later",
                        color = tokens.textMuted
                    )
                }
            }
        },
        containerColor = tokens.surface
    )
}
