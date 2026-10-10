package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassChip
import com.example.ui.components.GlassToolbar
import com.example.ui.theme.LocalNetOpsColors
import com.example.ui.theme.NetOpsThemeMode
import com.example.ui.theme.StatusSuccess

@Composable
fun AppearanceSettingsScreen(
    currentTheme: NetOpsThemeMode,
    onThemeChange: (NetOpsThemeMode) -> Unit,
    backgroundUri: String?,
    onBackgroundChange: (String?) -> Unit,
    glassOpacity: Float,
    onGlassOpacityChange: (Float) -> Unit,
    darkOverlay: Float,
    onDarkOverlayChange: (Float) -> Unit,
    currentRole: UserRole,
    onRoleChange: (UserRole) -> Unit,
    currentLang: String,
    onLangChange: (String) -> Unit,
    onResetAppearance: () -> Unit,
    cloudflareUrl: String = "https://netops-hub-api.mortavox.workers.dev/api/v1/",
    onCloudflareUrlChange: (String) -> Unit = {},
    cloudflareAccountId: String = "8652bde292ef4329a0c07e0bb745f93a",
    onCloudflareAccountIdChange: (String) -> Unit = {},
    cloudflareToken: String = "",
    onCloudflareTokenChange: (String) -> Unit = {},
    autoSync: Boolean = true,
    onAutoSyncChange: (Boolean) -> Unit = {},
    onSyncFromCloudflare: (() -> Unit)? = null,
    onPushAllToCloudflare: (() -> Unit)? = null,
    onNavigateToUserManagement: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    onBack: () -> Unit
) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current
    val isPersian = currentLang == "fa"
    var showDeployGuideDialog by remember { mutableStateOf(false) }
    var tempUrl by remember(cloudflareUrl) { mutableStateOf(cloudflareUrl) }
    var tempAccountId by remember(cloudflareAccountId) { mutableStateOf(cloudflareAccountId) }
    var tempToken by remember(cloudflareToken) { mutableStateOf(cloudflareToken) }

    if (showDeployGuideDialog) {
        AlertDialog(
            onDismissRequest = { showDeployGuideDialog = false },
            title = {
                Text("Cloudflare Deployment Guide", fontWeight = FontWeight.Bold, color = tokens.primary)
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Text(
                            text = "To deploy your NetOps Hub backend to Cloudflare Workers and D1 database, follow these steps in your terminal:",
                            style = MaterialTheme.typography.bodySmall.copy(color = tokens.textPrimary)
                        )
                    }
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = tokens.background,
                            border = BorderStroke(0.5.dp, tokens.glassBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "cd backend\nnpm install\nnpx wrangler d1 execute netops-hub-d1 --file=./schema.sql\nnpx wrangler d1 execute netops-hub-d1 --file=./migrations/0001_initial.sql\nnpx wrangler deploy",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = tokens.accent,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                ),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                    item {
                        Text(
                            text = "Once deployed, paste your Workers URL in the Cloudflare Endpoint field below to sync edits in real-time.",
                            style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDeployGuideDialog = false }) {
                    Text("Close", color = tokens.primary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = tokens.surface
        )
    }

    // Modern Zero-Permission Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onBackgroundChange(uri.toString())
            Toast.makeText(
                context,
                if (isPersian) "تصویر پس‌زمینه جدید با موفقیت روی حافظه دستگاه ذخیره شد" else "Custom background wallpaper saved to device storage!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        GlassToolbar(
            title = stringResource(R.string.settings_title),
            subtitle = "Customization • Visual Tokens • Security Role",
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = tokens.textPrimary)
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Theme Selector Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.theme_selection),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = tokens.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(NetOpsThemeMode.values()) { mode ->
                            GlassChip(
                                text = mode.title,
                                selected = mode == currentTheme,
                                onClick = { onThemeChange(mode) }
                            )
                        }
                    }
                }
            }

            // Background Wallpaper Personalization
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.background_customization),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = tokens.primary
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (!backgroundUri.isNullOrEmpty()) tokens.accent.copy(alpha = 0.2f) else tokens.primary.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, if (!backgroundUri.isNullOrEmpty()) tokens.accent else tokens.primary)
                        ) {
                            Text(
                                text = if (!backgroundUri.isNullOrEmpty()) "Custom Saved" else "Default NOC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (!backgroundUri.isNullOrEmpty()) tokens.accent else tokens.primary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isPersian) "تصویر پس‌زمینه را با عکس دلخواه شخصی‌سازی کنید. تصویر به صورت پایدار در حافظه داخلی گوشی ذخیره می‌شود و پس از بستن برنامه پاک نخواهد شد."
                               else "Customize the background using any local photo. Saved permanently to internal device storage and persists across restarts.",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = tokens.primary),
                            modifier = Modifier.weight(1f).testTag("pick_photo_button")
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = tokens.background, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isPersian) "انتخاب عکس" else "Pick Photo", color = tokens.background, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { 
                                onBackgroundChange(null)
                                Toast.makeText(context, if (isPersian) "پس‌زمینه به حالت پیش‌فرض بازگشت" else "Reset to default NOC wallpaper", Toast.LENGTH_SHORT).show()
                            },
                            border = BorderStroke(1.dp, tokens.glassBorder),
                            modifier = Modifier.weight(1f).testTag("reset_bg_button")
                        ) {
                            Text(if (isPersian) "پیش‌فرض NOC" else "Default NOC", color = tokens.textPrimary)
                        }
                    }
                }
            }

            // Glassmorphism & Readability Controls
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Readability & Transparency Engine",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = tokens.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${stringResource(R.string.glass_transparency)}: ${(glassOpacity * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium.copy(color = tokens.textPrimary)
                    )
                    Slider(
                        value = glassOpacity,
                        onValueChange = onGlassOpacityChange,
                        valueRange = 0.2f..0.95f,
                        colors = SliderDefaults.colors(
                            thumbColor = tokens.primary,
                            activeTrackColor = tokens.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${stringResource(R.string.dark_overlay)}: ${(darkOverlay * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium.copy(color = tokens.textPrimary)
                    )
                    Slider(
                        value = darkOverlay,
                        onValueChange = onDarkOverlayChange,
                        valueRange = 0.2f..0.95f,
                        colors = SliderDefaults.colors(
                            thumbColor = tokens.accent,
                            activeTrackColor = tokens.accent
                        )
                    )
                }
            }

            // Language Switcher
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.language_selection),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = tokens.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassChip(
                            text = "English (LTR)",
                            selected = currentLang == "en",
                            onClick = { onLangChange("en") },
                            modifier = Modifier.weight(1f)
                        )
                        GlassChip(
                            text = "فارسی (RTL)",
                            selected = currentLang == "fa",
                            onClick = { onLangChange("fa") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // RBAC Role Testing Switcher
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Active Operator Role (RBAC Simulation)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = tokens.accent
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Switch between Admin, Editor, and Viewer to verify least-privilege access rules across Company & Academy.",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserRole.values().forEach { role ->
                            GlassChip(
                                text = role.display,
                                selected = role == currentRole,
                                onClick = { onRoleChange(role) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (currentRole == UserRole.CREATOR || currentRole == UserRole.ADMIN) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onNavigateToUserManagement?.invoke() },
                            colors = ButtonDefaults.buttonColors(containerColor = tokens.primary),
                            modifier = Modifier.fillMaxWidth().testTag("settings_open_users_button")
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = tokens.background, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.user_management), color = tokens.background, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (onLogout != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onLogout,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = tokens.error),
                            border = BorderStroke(1.dp, tokens.error.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().testTag("settings_logout_button")
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, tint = tokens.error, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.btn_logout), color = tokens.error, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Cloudflare Workers & D1 Integration Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(StatusSuccess, shape = CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Cloudflare D1 Online",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = tokens.primary
                                    )
                                )
                            }
                            Text(
                                text = "Real-Time Sync Active • Account: ${tempAccountId.take(12)}...",
                                style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary, fontSize = 11.sp)
                            )
                        }

                        IconButton(onClick = { showDeployGuideDialog = true }) {
                            Icon(Icons.Default.HelpOutline, contentDescription = "Deployment Guide", tint = tokens.accent)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Real-Time Auto Sync Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Colleague Real-Time Sync",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = tokens.textPrimary)
                            )
                            Text(
                                text = "Auto-update company data & knowledge when colleagues make edits",
                                style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary, fontSize = 11.sp)
                            )
                        }
                        Switch(
                            checked = autoSync,
                            onCheckedChange = onAutoSyncChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = tokens.primary, checkedTrackColor = tokens.primary.copy(alpha = 0.5f))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tempAccountId,
                        onValueChange = {
                            tempAccountId = it
                            onCloudflareAccountIdChange(it)
                        },
                        label = { Text("Cloudflare Account ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = tempToken,
                        onValueChange = {
                            tempToken = it
                            onCloudflareTokenChange(it)
                        },
                        label = { Text("Cloudflare API Token") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = tempUrl,
                        onValueChange = {
                            tempUrl = it
                            onCloudflareUrlChange(it)
                        },
                        label = { Text("Cloudflare Worker REST API Endpoint") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                tempUrl = "https://netops-hub-api.mortavox.workers.dev/api/v1/"
                                onCloudflareUrlChange(tempUrl)
                            }) {
                                Icon(Icons.Default.RestartAlt, contentDescription = "Reset Endpoint", tint = tokens.textMuted)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onSyncFromCloudflare?.invoke() },
                            colors = ButtonDefaults.buttonColors(containerColor = tokens.primary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = tokens.background, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pull D1", color = tokens.background, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onPushAllToCloudflare?.invoke() },
                            colors = ButtonDefaults.buttonColors(containerColor = tokens.accent),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = tokens.background, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Push All to D1", color = tokens.background, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Reset All Appearance Settings
            item {
                OutlinedButton(
                    onClick = onResetAppearance,
                    border = BorderStroke(1.dp, tokens.error.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = tokens.error, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Appearance to Defaults", color = tokens.error)
                }
            }
        }
    }
}
