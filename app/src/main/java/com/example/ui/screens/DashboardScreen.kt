package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.components.GlassCard
import com.example.ui.theme.LocalNetOpsColors
import com.example.ui.theme.TerminalGreen

@Composable
fun DashboardScreen(
    onNavigateToCompany: () -> Unit,
    onNavigateToAcademy: () -> Unit,
    onNavigateToHelpdesk: () -> Unit,
    onNavigateToTools: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToUserManagement: (() -> Unit)? = null,
    onNavigateToPhoneBook: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    serverCount: Int = 79,
    articleCount: Int = 25,
    currentRole: UserRole = UserRole.CREATOR,
    currentLang: String = "en"
) {
    val tokens = LocalNetOpsColors.current
    val isPersian = currentLang == "fa"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // NOC Top Header Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(TerminalGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NOC TELEMETRY ONLINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TerminalGreen,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = tokens.textPrimary,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = stringResource(R.string.app_subtitle),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = tokens.textSecondary
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Global Search quick button
                    IconButton(
                        onClick = onNavigateToSearch,
                        modifier = Modifier
                            .testTag("dashboard_search_button")
                            .clip(CircleShape)
                            .background(tokens.glassSurface)
                            .border(1.dp, tokens.glassBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = tokens.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Settings quick button
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .testTag("dashboard_settings_button")
                            .clip(CircleShape)
                            .background(tokens.glassSurface)
                            .border(1.dp, tokens.glassBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = tokens.textSecondary
                        )
                    }

                    if (onLogout != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        // Logout quick button
                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier
                                .testTag("dashboard_logout_button")
                                .clip(CircleShape)
                                .background(tokens.error.copy(alpha = 0.15f))
                                .border(1.dp, tokens.error.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Sign Out",
                                tint = tokens.error
                            )
                        }
                    }
                }
            }
        }

        // Active Operator Role & Telemetry Pill
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = tokens.glassSurface.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, tokens.glassBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = tokens.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Role: ${currentRole.display}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = tokens.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentRole == UserRole.CREATOR || currentRole == UserRole.ADMIN) {
                            TextButton(
                                onClick = { onNavigateToUserManagement?.invoke() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("dashboard_user_mgmt_button")
                            ) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = tokens.primary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Users", color = tokens.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Row(
                            modifier = Modifier.clickable { onNavigateToFavorites() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Favorites",
                                tint = tokens.accent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.favorites),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = tokens.accent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Live Metric Counters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "SERVERS",
                    value = "$serverCount",
                    subtitle = "Ardakan & Jahan",
                    icon = Icons.Default.Dns,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "SITES",
                    value = "10",
                    subtitle = "Active Branches",
                    icon = Icons.Default.Domain,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "KNOWLEDGE",
                    value = "100+",
                    subtitle = "Guides & Tools",
                    icon = Icons.Default.School,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ========================================================
        // 1. COMPANY INFORMATION
        // ========================================================
        item {
            PrimarySectionCard(
                title = stringResource(R.string.section_company),
                description = stringResource(R.string.section_company_desc),
                icon = Icons.Default.Storage,
                accentColor = tokens.primary,
                tags = listOf("Servers (80)", "Sites", "vCenter", "Rahkaran ERP (12)", "Ports & RDP"),
                onClick = onNavigateToCompany,
                testTag = "company_section_card"
            )
        }

        // ========================================================
        // 2. NETWORK ACADEMY
        // ========================================================
        item {
            PrimarySectionCard(
                title = stringResource(R.string.section_academy),
                description = stringResource(R.string.section_academy_desc),
                icon = Icons.Default.MenuBook,
                accentColor = tokens.accent,
                tags = listOf("Visual Cyber Maps", "URL Anatomy", "Hashing/Encoding", "Active Directory 1-4", "Network+", "CCNA", "CCNP"),
                onClick = onNavigateToAcademy,
                testTag = "academy_section_card"
            )
        }

        // ========================================================
        // 3. TROUBLESHOOTING HELPDESK
        // ========================================================
        item {
            PrimarySectionCard(
                title = stringResource(R.string.section_helpdesk),
                description = stringResource(R.string.section_helpdesk_desc),
                icon = Icons.Default.SupportAgent,
                accentColor = Color(0xFFFF5252),
                tags = listOf("NOC Incident Triage", "LAN Packet Loss", "DHCP APIPA", "DNS Host Failures", "AD Lockouts", "ESXi Host Down"),
                onClick = onNavigateToHelpdesk,
                testTag = "helpdesk_section_card"
            )
        }

        // ========================================================
        // 5. PHONE BOOK (دفترچه تلفن) - v2.0.1
        // ========================================================
        item {
            PrimarySectionCard(
                title = if (isPersian) "دفترچه تلفن سازمانی" else "Corporate Phone Book",
                description = if (isPersian)
                    "نام و نام خانوادگی، شماره تماس، نام شرکت و شماره داخلی - با ورود گروهی از فایل اکسل"
                else
                    "First & last name, phone number, company and internal extension - with bulk Excel import",
                icon = Icons.Default.ContactPhone,
                accentColor = Color(0xFF66BB6A),
                tags = if (isPersian)
                    listOf("جستجوی سریع", "شرکت‌ها", "شماره داخلی", "ورود از اکسل", "تماس مستقیم")
                else
                    listOf("Quick Search", "Company Filter", "Extensions", "Excel Import", "One-tap Call"),
                onClick = { onNavigateToPhoneBook?.invoke() },
                testTag = "phonebook_section_card"
            )
        }

        // ========================================================
        // 4. ENGINEERING TOOLS
        // ========================================================
        item {
            PrimarySectionCard(
                title = stringResource(R.string.section_tools),
                description = stringResource(R.string.section_tools_desc),
                icon = Icons.Default.Calculate,
                accentColor = tokens.secondary,
                tags = listOf("Subnet Calculator", "VLSM Planner", "Wildcard Mask", "Bandwidth", "Port Reference (100+)", "Wireshark"),
                onClick = onNavigateToTools,
                testTag = "network_tools_section_card"
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val tokens = LocalNetOpsColors.current
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = tokens.textMuted,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                ),
                maxLines = 1,
                softWrap = false,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tokens.primary,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = tokens.textPrimary,
                fontSize = 18.sp
            ),
            maxLines = 1
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall.copy(
                color = tokens.textSecondary,
                fontSize = 10.sp
            ),
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PrimarySectionCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    tags: List<String>,
    onClick: () -> Unit,
    testTag: String
) {
    val tokens = LocalNetOpsColors.current

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f)),
        contentPadding = PaddingValues(18.dp),
        onClick = onClick
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Top Bar: Icon with glowing container + Navigation Arrow Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.35f),
                                    accentColor.copy(alpha = 0.12f)
                                )
                            )
                        )
                        .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXPLORE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Title & Description (Full Card Width, spacious and comfortably legible)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = tokens.textPrimary,
                        fontSize = 18.sp
                    ),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = tokens.textSecondary,
                        lineHeight = 19.sp,
                        fontSize = 13.sp
                    ),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            // Horizontally Scrollable Tags Row (Prevents vertical text wrapping completely)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tokens.surface.copy(alpha = 0.7f),
                        border = BorderStroke(0.5.dp, tokens.glassBorder)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = tokens.textPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            softWrap = false,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}
