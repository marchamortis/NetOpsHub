package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// Colors inspired by CyberKid Infographics
val NeonCyan = Color(0xFF00E5FF)
val NeonPurple = Color(0xFF8C52FF)
val NeonGreen = Color(0xFF00E676)
val NeonOrange = Color(0xFFFF5722)
val NeonYellow = Color(0xFFFFD600)
val NeonPink = Color(0xFFFF4081)
val DarkCardBg = Color(0xFF0E1424)
val DarkHeaderBg = Color(0xFF131B30)

@Composable
fun CyberInfographicsSection(
    isPersian: Boolean = false
) {
    val tokens = LocalNetOpsColors.current
    var selectedMapIndex by remember { mutableIntStateOf(0) }

    val maps = listOf(
        "Anatomy of a URL" to "کالبدشکافی URL",
        "Hashing vs Encryption" to "مفاهیم هشینگ و انکودینگ",
        "OSI 7 Layers vs TCP/IP" to "مدل ۷ لایه OSI و کپسوله‌سازی دیتا",
        "TCP 3-Way Handshake" to "فرایند ۳ مرحله‌ای اتصال TCP",
        "DNS Resolution Flow" to "مراحل سلسله‌مراتب ترجمه DNS",
        "DHCP DORA Process" to "فرایند ۴ مرحله‌ای DORA در DHCP",
        "VLAN 802.1Q & Trunk" to "تگ‌گذاری ترانک 802.1Q و VLAN",
        "Subnetting & CIDR" to "تقلب‌نامه ساب‌نتینگ و فرمول جادویی",
        "Ports & Protocols" to "ماتریس پورت‌های حیاتی و سرویس‌ها",
        "NAT & PAT Overload" to "انواع NAT و ترجمه Inside/Outside",
        "AD: Architecture" to "اکتیو دایرکتوری: معماری",
        "AD: Protocols" to "پروتکل‌ها: Kerberos/LDAP/NTLM",
        "AD: Users & GPO" to "کاربران، گروه‌ها و GPO",
        "AD: Security Audit" to "بررسی امنیتی و Hardening",
        // ---- v2.0.1 new visual maps ----
        "Kerberos Deep-Dive" to "کربروس: جریان کامل تیکت‌ها",
        "AD Command Arsenal" to "آرسنال کامندهای اکتیو دایرکتوری",
        "AD Audit & Defense" to "ممیزی و دفاع اکتیو دایرکتوری",
        "OSPF vs BGP" to "مقایسه OSPF و BGP",
        "IPsec VPN Phases" to "فازهای تونل IPsec",
        "Windows Net Commands" to "کامندهای شبکه ویندوز"
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Tab Selector for Visual Infographic Posters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            maps.forEachIndexed { index, (nameEn, nameFa) ->
                val isSelected = selectedMapIndex == index
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) tokens.primary.copy(alpha = 0.25f) else tokens.glassSurface,
                    border = BorderStroke(1.dp, if (isSelected) tokens.primary else tokens.glassBorder),
                    modifier = Modifier.clickable { selectedMapIndex = index }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) tokens.primary else tokens.textMuted)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPersian) nameFa else nameEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) tokens.primary else tokens.textSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Active Infographic Map Viewer
        when (selectedMapIndex) {
            0 -> UrlAnatomyCard(isPersian)
            1 -> HashingConceptCard(isPersian)
            2 -> OsiTcpIpEncapsulationCard(isPersian)
            3 -> TcpHandshakeCard(isPersian)
            4 -> DnsResolutionFlowCard(isPersian)
            5 -> DhcpDoraCard(isPersian)
            6 -> VlanTrunkingCard(isPersian)
            7 -> SubnettingCheatSheetCard(isPersian)
            8 -> PortsProtocolsMatrixCard(isPersian)
            9 -> NatMechanicsCard(isPersian)
            10 -> ActiveDirectoryArchitectureCard(isPersian)
            11 -> ActiveDirectoryProtocolsCard(isPersian)
            12 -> ActiveDirectoryUsersGpoCard(isPersian)
            13 -> ActiveDirectoryDefensiveCard(isPersian)
            // ---- v2.0.1 new visual maps ----
            14 -> KerberosDeepDiveCard(isPersian)
            15 -> AdUsersGroupsCommandsCard(isPersian)
            16 -> AdMisconfigDefenseCard(isPersian)
            17 -> OspfBgpRoutingCard(isPersian)
            18 -> IpsecVpnPhasesCard(isPersian)
            19 -> WindowsNetCommandsCard(isPersian)
        }
    }
}

// -------------------------------------------------------------
// 1. ANATOMY OF A URL (Inspired by Image 1)
// -------------------------------------------------------------
@Composable
fun UrlAnatomyCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ANATOMY OF A URL",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = if (isPersian) "تجزیه ساختار پیوند و کشف الگوهای فیشینگ" else "Break it down. See the bigger picture. Spot red flags.",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                }
            }

            // Browser URL Bar Mockup
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF070B14),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    UrlSegmentBadge("https://", NeonGreen, "Protocol")
                    UrlSegmentBadge("www.", NeonCyan, "Subdomain")
                    UrlSegmentBadge("example", NeonYellow, "Domain")
                    UrlSegmentBadge(".com", NeonPurple, "TLD")
                    UrlSegmentBadge(":443", NeonOrange, "Port")
                    UrlSegmentBadge("/login", NeonPink, "Path")
                    UrlSegmentBadge("?user=admin&ref=home", NeonCyan, "Query")
                    UrlSegmentBadge("#section", NeonPurple, "Fragment")
                }
            }

            // Breakdown Cards
            val segments = listOf(
                SegmentDetail("Protocol (https://)", "Defines communication encryption.", "پروتکل امن رمزنگاری‌شده بین مرورگر و وب‌سرور.", NeonGreen),
                SegmentDetail("Subdomain (www. / mail.)", "Prefix before the main domain.", "پیشوند دامنه برای تفکیک سرویس‌ها (وب، میل و غیره).", NeonCyan),
                SegmentDetail("Domain (example)", "Core identity of the website.", "نام اصلی دامنه که نشان‌دهنده هویت واقعی وب‌سایت است.", NeonYellow),
                SegmentDetail("TLD (.com / .ir / .org)", "Top-level extension.", "پسوند سطح بالا (مراقب پسوندهای ناشناخته فیشینگ باشید).", NeonPurple),
                SegmentDetail("Port (:443 HTTPS / :80 HTTP)", "Network socket on server.", "پورت سرویس که معمولاً برای HTTPS مخفی می‌شود.", NeonOrange),
                SegmentDetail("Path (/login)", "Resource path on server.", "مسیر دسترسی به صفحه یا فایل در وب‌سرور.", NeonPink),
                SegmentDetail("Query (?user=admin)", "Parameters passed to server.", "پارامترهای ارسالی (بررسی احتمال SQLi / XSS).", NeonCyan),
                SegmentDetail("Fragment (#section)", "Client-side anchor in page.", "ارجاع به بخش خاصی از صفحه بدون ارسال به سرور.", NeonPurple)
            )

            segments.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkHeaderBg,
                    border = BorderStroke(1.dp, item.color.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(item.color))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(item.name, color = item.color, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Text(if (isPersian) item.descFa else item.descEn, color = tokens.textSecondary, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        }
                    }
                }
            }
        }
    }
}

private data class SegmentDetail(val name: String, val descEn: String, val descFa: String, val color: Color)

@Composable
private fun UrlSegmentBadge(text: String, color: Color, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 2.dp)) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = color.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
        ) {
            Text(
                text = text,
                color = color,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// 2. HASHING CONCEPT (Inspired by Image 2)
// -------------------------------------------------------------
@Composable
fun HashingConceptCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "1. HASHING (DIGITAL FINGERPRINT)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonCyan)
                    )
                    Text(
                        text = if (isPersian) "تبدیل داده به یک اثر انگشت با طول ثابت" else "Turns data into a fixed-length unique fingerprint",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }

                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(32.dp))
            }

            // Key Properties
            Surface(shape = RoundedCornerShape(10.dp), color = DarkHeaderBg, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("KEY PROPERTIES", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    CheckItem("One-way process (cannot be mathematically reversed)", "فرآیند یک‌طرفه (غیرقابل بازگشت ریاضی)")
                    CheckItem("Same input ALWAYS produces the exact same hash", "ورودی یکسان همواره هش دقیقاً یکسان ایجاد می‌کند")
                    CheckItem("Small change = Completely different hash (Avalanche effect)", "تغییر یک حرف کل هش را دگرگون می‌کند (اثر بهمن)")
                    CheckItem("Fixed-length output (e.g. SHA-256 = 256 bits)", "طول خروجی همواره ثابت است")
                }
            }

            // Algorithm Comparison
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("COMMON HASH ALGORITHMS", color = tokens.textPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                HashAlgoRow("MD5", "128-bit", "BROKEN (Collisions found)", StatusError)
                HashAlgoRow("SHA-1", "160-bit", "LEGACY (Not recommended)", StatusWarning)
                HashAlgoRow("SHA-256", "256-bit", "INDUSTRY STANDARD (Secure)", TerminalGreen)
                HashAlgoRow("SHA-512", "512-bit", "HIGH SECURITY (Strong)", NeonCyan)
                HashAlgoRow("BLAKE3", "256-bit", "MODERN & ULTRA FAST", NeonPurple)
            }
        }
    }
}

@Composable
private fun CheckItem(textEn: String, textFa: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TerminalGreen, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(textEn, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
private fun HashAlgoRow(name: String, bit: String, status: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = DarkHeaderBg,
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(name, fontWeight = FontWeight.Bold, color = color, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text("($bit)", color = Color.Gray, fontSize = 10.sp)
            }
            Text(status, color = color, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
    }
}

// -------------------------------------------------------------
// 3. ENCODING CONCEPT (Inspired by Image 7)
// -------------------------------------------------------------
@Composable
fun EncodingConceptCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "3. ENCODING (DATA TRANSFORMATION)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonPurple)
            )
            Text(
                text = if (isPersian) "تغییر قالب داده بدون رمزنگاری (با قابلیت بازگشت کامل بدون کلید)" else "Converts data into different format using predefined rules (NOT encryption).",
                style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
            )

            // Comparison Matrix: Hashing vs Encryption vs Encoding
            Surface(shape = RoundedCornerShape(10.dp), color = DarkHeaderBg, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("QUICK COMPARISON", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    MatrixRow("Concept", "Reversible?", "Key Required?", "Primary Goal")
                    MatrixRow("Hashing", "NO", "NO", "Integrity & Passwords")
                    MatrixRow("Encryption", "YES", "YES", "Confidentiality")
                    MatrixRow("Encoding", "YES", "NO", "Usability & Transport")
                }
            }

            // Common Formats
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("COMMON ENCODING FORMATS", color = tokens.textPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                HashAlgoRow("Base64", "6-bit groups", "Used in binary email, tokens, icons", NeonCyan)
                HashAlgoRow("URL Encoding", "Percent (%)", "Replaces spaces with %20, symbols", NeonYellow)
                HashAlgoRow("UTF-8", "Variable 1-4B", "Global character standard", TerminalGreen)
                HashAlgoRow("HEX", "Base 16", "Used in byte buffers & payloads", NeonOrange)
            }
        }
    }
}

@Composable
private fun MatrixRow(col1: String, col2: String, col3: String, col4: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(col1, color = NeonYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(col2, color = Color.White, fontSize = 10.sp, modifier = Modifier.weight(0.9f))
        Text(col3, color = Color.White, fontSize = 10.sp, modifier = Modifier.weight(0.9f))
        Text(col4, color = Color.LightGray, fontSize = 10.sp, modifier = Modifier.weight(1.2f))
    }
}

// -------------------------------------------------------------
// 4. ACTIVE DIRECTORY: ARCHITECTURE (Inspired by Image 3)
// -------------------------------------------------------------
@Composable
fun ActiveDirectoryArchitectureCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "ACTIVE DIRECTORY: ARCHITECTURE (1/4)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonCyan)
            )

            // Architecture Components Diagram
            Surface(shape = RoundedCornerShape(10.dp), color = DarkHeaderBg, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("CORE AD HIERARCHY", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    AdCompItem("Forest", "Highest level container sharing common schema and global catalog.", NeonPurple)
                    AdCompItem("Domain", "Logical group of objects (users, computers, groups) with security boundaries.", NeonCyan)
                    AdCompItem("Domain Controller (DC)", "Hosts the AD database (NTDS.dit), handles auth (Kerberos/LDAP).", NeonYellow)
                    AdCompItem("Organizational Unit (OU)", "Container for delegation and applying Group Policy Objects (GPOs).", NeonGreen)
                    AdCompItem("Trust Relationships", "Enables access across domains or forests (Two-way, transitive).", NeonOrange)
                }
            }

            // FSMO Roles Card
            Surface(shape = RoundedCornerShape(10.dp), color = DarkHeaderBg, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("5 FSMO MASTER ROLES", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    RoleItem("Schema Master", "Forest-wide: Manages directory schema updates")
                    RoleItem("Domain Naming Master", "Forest-wide: Controls adding/removing domains")
                    RoleItem("PDC Emulator", "Domain-wide: Password changes, time synchronization, GPO")
                    RoleItem("RID Master", "Domain-wide: Allocates relative identifier pools")
                    RoleItem("Infrastructure Master", "Domain-wide: Manages inter-domain cross-references")
                }
            }
        }
    }
}

@Composable
private fun AdCompItem(name: String, desc: String, color: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color).padding(top = 4.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(name, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(desc, color = Color.LightGray, fontSize = 10.sp)
        }
    }
}

@Composable
private fun RoleItem(name: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("• ", color = NeonGreen, fontWeight = FontWeight.Bold)
        Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text("- $desc", color = Color.Gray, fontSize = 10.sp)
    }
}

// -------------------------------------------------------------
// 5. ACTIVE DIRECTORY: PROTOCOLS (Inspired by Image 4)
// -------------------------------------------------------------
@Composable
fun ActiveDirectoryProtocolsCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "AD AUTHENTICATION PROTOCOLS (2/4)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonGreen)
            )

            // Protocols Breakdown
            AdCompItem("KERBEROS (Port 88)", "Default & Secure: Uses tickets (TGT, TGS) instead of transmitting passwords.", TerminalGreen)
            AdCompItem("LDAP / LDAPS (Port 389 / 636)", "Directory queries, user synchronization, SSL encrypted with 636.", NeonCyan)
            AdCompItem("NTLM (Port 445 SMB)", "Legacy Challenge-Response. Vulnerable to pass-the-hash and relay attacks.", StatusWarning)

            // Ports Reference Table
            Surface(shape = RoundedCornerShape(10.dp), color = DarkHeaderBg, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ACTIVE DIRECTORY CORE PORTS", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    PortBadgeRow("88 TCP/UDP", "Kerberos Authentication")
                    PortBadgeRow("389 / 636 TCP", "LDAP / LDAPS Directory")
                    PortBadgeRow("445 TCP", "SMB File Sharing / NetBIOS")
                    PortBadgeRow("3268 / 3269", "Global Catalog (GC / SSL)")
                    PortBadgeRow("53 TCP/UDP", "DNS (Domain Name System)")
                    PortBadgeRow("135 TCP", "RPC Endpoint Mapper")
                }
            }
        }
    }
}

@Composable
private fun PortBadgeRow(port: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(port, color = NeonGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Text(desc, color = Color.LightGray, fontSize = 11.sp)
    }
}

// -------------------------------------------------------------
// 6. ACTIVE DIRECTORY: USERS, GROUPS & GPO (Inspired by Image 5)
// -------------------------------------------------------------
@Composable
fun ActiveDirectoryUsersGpoCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "USERS, GROUPS & GPO MANAGEMENT (3/4)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonYellow)
            )

            // Group Scopes
            Surface(shape = RoundedCornerShape(10.dp), color = DarkHeaderBg, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("GROUP SCOPES & USAGE", color = NeonYellow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    RoleItem("Security - Global", "Can contain users/computers from same domain. Assigns role.")
                    RoleItem("Security - Domain Local", "Assigns permissions to resources in local domain.")
                    RoleItem("Security - Universal", "Contains objects from any domain in the forest.")
                }
            }

            // GPO Best Practice
            Surface(shape = RoundedCornerShape(10.dp), color = DarkHeaderBg, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("GROUP POLICY (GPO) PROCESSING ORDER (LSDOU)", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("1. Local -> 2. Site -> 3. Domain -> 4. OU (Last applied wins)", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 7. ACTIVE DIRECTORY: DEFENSIVE AUDIT (Inspired by Image 6)
// -------------------------------------------------------------
@Composable
fun ActiveDirectoryDefensiveCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "COMMON MISCONFIGURATIONS & AUDIT (4/4)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonPink)
            )

            val checks = listOf(
                "Kerberoasting Attack: Service Principal Names (SPNs) with weak passwords",
                "Over-privileged Admin accounts (members of Domain Admins for daily tasks)",
                "Unconstrained Delegation exposing TGT tickets in memory",
                "SMBv1 enabled on legacy servers (Disable immediately via PowerShell)",
                "Stale Computer and User Accounts (> 90 days inactive)",
                "Lack of LAPS (Local Administrator Password Solution) for local admin"
            )

            checks.forEach { check ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(check, color = Color.White, fontSize = 11.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 8. OSI 7 LAYERS VS TCP/IP & PDU ENCAPSULATION
// -------------------------------------------------------------
@Composable
fun OsiTcpIpEncapsulationCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OSI 7 LAYERS & ENCAPSULATION",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonCyan)
                    )
                    Text(
                        text = if (isPersian) "کپسوله‌سازی بسته‌ها از Data به Bits و معادل TCP/IP" else "PDU Data flow, Protocol Stacks & Diagnostic tools",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(NeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
            }

            // Layer Visual Stack (7 to 1)
            val layers = listOf(
                OsiLayerUi("L7", "Application", "کاربرد", "Data", "HTTP, DNS, SSH, DHCP", NeonPink),
                OsiLayerUi("L6", "Presentation", "ارائه", "Data", "SSL/TLS, JPEG, ASCII", NeonPurple),
                OsiLayerUi("L5", "Session", "نشست", "Data", "NetBIOS, RPC, Sockets", NeonPurple),
                OsiLayerUi("L4", "Transport", "انتقال", "Segment", "TCP (Reliable), UDP (Fast)", NeonCyan),
                OsiLayerUi("L3", "Network", "شبکه", "Packet", "IPv4, IPv6, ICMP, OSPF, BGP", NeonYellow),
                OsiLayerUi("L2", "Data Link", "پیوند داده", "Frame", "Ethernet 802.3, MAC, VLAN, ARP", NeonGreen),
                OsiLayerUi("L1", "Physical", "فیزیکی", "Bits", "Cat6 RJ45, Single/Multi Fiber, SFP+", NeonOrange)
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                layers.forEach { l ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkHeaderBg,
                        border = BorderStroke(1.dp, l.color.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = l.color.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, l.color)
                                ) {
                                    Text(
                                        text = l.num,
                                        color = l.color,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isPersian) "${l.name} (${l.nameFa})" else l.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(text = l.protocols, color = Color.Gray, fontSize = 10.sp)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = l.color.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "PDU: ${l.pdu}",
                                    color = l.color,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Encapsulation Rule Note
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NeonCyan.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isPersian) "💡 قانون کپسوله‌سازی: هنگام ارسال (Tx)، هر لایه هدر خود را به دیتا اضافه می‌کند (Data -> +TCP -> +IP -> +MAC -> Bits). هنگام دریافت (Rx)، هدرها به ترتیب بازگشایی می‌شوند."
                           else "💡 Encapsulation Rule: Transmission prepends headers (Data -> Segment -> Packet -> Frame -> Bits). De-encapsulation strips headers at destination host.",
                    color = tokens.textPrimary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

private data class OsiLayerUi(val num: String, val name: String, val nameFa: String, val pdu: String, val protocols: String, val color: Color)

// -------------------------------------------------------------
// 9. TCP 3-WAY HANDSHAKE & TEARDOWN
// -------------------------------------------------------------
@Composable
fun TcpHandshakeCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TCP 3-WAY HANDSHAKE",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonGreen)
                    )
                    Text(
                        text = if (isPersian) "برقراری نشست مطمئن با SYN، SYN-ACK و ACK" else "Connection establishment & 4-way termination flow",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(NeonGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SyncAlt, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
                }
            }

            // Handshake Steps
            val handshakeSteps = listOf(
                HandshakeStepUi(1, "Client -> Server", "[SYN] Flags: SYN=1, ACK=0", "Seq = 1000, Ack = 0", "درخواست شروع ارتباط و تبادل شماره ترتیبی اولیه (ISN)", NeonCyan),
                HandshakeStepUi(2, "Server -> Client", "[SYN-ACK] Flags: SYN=1, ACK=1", "Seq = 5000, Ack = 1001", "پذیرش درخواست و ارسال شماره ترتیبی سرور همراه تایید کلاینت", NeonYellow),
                HandshakeStepUi(3, "Client -> Server", "[ACK] Flags: SYN=0, ACK=1", "Seq = 1001, Ack = 5001", "تایید نهایی و ورود اتصال به حالت ESTABLISHED برای تبادل دیتا", NeonGreen)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                handshakeSteps.forEach { step ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkHeaderBg,
                        border = BorderStroke(1.dp, step.color.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(20.dp).clip(CircleShape).background(step.color),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${step.step}", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(step.direction, color = step.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Text(step.flags, color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            }
                            Text(step.seqAck, color = NeonGreen, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            Text(if (isPersian) step.descFa else "Initial handshake state machine transition", color = Color.LightGray, fontSize = 10.sp)
                        }
                    }
                }
            }

            // Wireshark Filter Cheat
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black,
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("WIRESHARK FILTERS:", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Text("tcp.flags.syn == 1 && tcp.flags.ack == 0  # Filter SYN requests", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    Text("tcp.flags.reset == 1                      # Filter RST connection drops", color = NeonOrange, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                }
            }
        }
    }
}

private data class HandshakeStepUi(val step: Int, val direction: String, val flags: String, val seqAck: String, val descFa: String, val color: Color)

// -------------------------------------------------------------
// 10. DNS RESOLUTION HIERARCHY
// -------------------------------------------------------------
@Composable
fun DnsResolutionFlowCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DNS RESOLUTION HIERARCHY",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonPurple)
                    )
                    Text(
                        text = if (isPersian) "فرآیند ترجمه نام FQDN به آی‌پی و کش محلی" else "Recursive query lifecycle from Browser to Authoritative NS",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(NeonPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Dns, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                }
            }

            // Steps
            val dnsSteps = listOf(
                "1. Browser & OS Cache" to "بررسی حافظه کش مرورگر و فایل hosts محلی ویندوز (C:\\Windows\\System32\\drivers\\etc\\hosts)",
                "2. Recursive Resolver" to "ارسال کوئری به DNS سرور تعیین‌شده شبکه (مانند 192.168.1.11 یا 8.8.8.8)",
                "3. Root Nameservers (.)" to "ارجاع به سرورهای ریشه جهان (۱۳ روت‌سرور) جهت پیدا کردن سرور پسوند TLD",
                "4. TLD Nameservers (.com / .ir)" to "ارجاع به سرورهای مدیریت پسوند دامنه برای یافتن Nameserver معتبر دامنه",
                "5. Authoritative Nameserver" to "پاسخ قطعی سرور اصلی هاستینگ دامنه با رکورد A یا AAAA همراه با TTL",
                "6. Client Cache & Connect" to "ذخیره در کش کلاینت برای جلوگیری از درخواست‌های تکراری و برقراری ارتباط با IP"
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                dnsSteps.forEachIndexed { idx, (en, fa) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkHeaderBg,
                        border = BorderStroke(0.8.dp, NeonPurple.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(en, color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(140.dp))
                            Text(if (isPersian) fa else en, color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }

            // DNS Record Types Matrix
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black,
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("KEY DNS RECORD TYPES:", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("A: IPv4 address", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("AAAA: IPv6 address", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("CNAME: Canonical Alias", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("MX: Mail Exchange", color = NeonYellow, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("TXT: SPF / DKIM verification", color = NeonYellow, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("PTR: Reverse Lookup", color = NeonYellow, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 11. DHCP DORA PROCESS & RELAY
// -------------------------------------------------------------
@Composable
fun DhcpDoraCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DHCP DORA 4-STEP PROCESS",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonOrange)
                    )
                    Text(
                        text = if (isPersian) "مراحل دریافت آدرس IP خودکار و رله DHCP (Port 67/68)" else "Discover -> Offer -> Request -> Acknowledge lifecycle",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(NeonOrange.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lan, contentDescription = null, tint = NeonOrange, modifier = Modifier.size(20.dp))
                }
            }

            val doraSteps = listOf(
                DoraStepUi("D", "DISCOVER", "Client -> Broadcast (255.255.255.255:67)", "کلاینت به صورت همه‌پخشی اعلام نیاز به IP می‌کند (UDP 67)", NeonOrange),
                DoraStepUi("O", "OFFER", "Server -> Unicast/Broadcast (Client:68)", "سرور DHCP یک آدرس پیشنهادی آزاد همراه ماسک و مدت Lease ارسال می‌کند", NeonYellow),
                DoraStepUi("R", "REQUEST", "Client -> Broadcast (Selects Server)", "کلاینت آدرس پیشنهادی را تایید و رسماً درخواست رزرو آن را اعلام می‌کند", NeonCyan),
                DoraStepUi("A", "ACKNOWLEDGE", "Server -> Client (ACK confirmed)", "سرور تایید نهایی را به همراه Default Gateway و DNS سرورها صادر می‌کند", NeonGreen)
            )

            doraSteps.forEach { step ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkHeaderBg,
                    border = BorderStroke(1.dp, step.color.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(26.dp).clip(CircleShape).background(step.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(step.letter, color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(step.title, color = step.color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(step.tech, color = Color.LightGray, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                            }
                            Text(if (isPersian) step.descFa else step.tech, color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }

            // APIPA Warning Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = StatusWarning.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, StatusWarning),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPersian) "هشدار APIPA (169.254.x.x): اگر کلاینت پاسخی از DHCP دریافت نکند، این رنج آی‌پی را خودکار برمی‌دارد که نشان‌دهنده قطعی کابل، عدم تنظیم Helper-Address روی روتر یا پر شدن Pool است."
                               else "APIPA Warning (169.254.x.x): Means DHCP Discover received no response. Check cable, VLAN, or router 'ip helper-address'.",
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

private data class DoraStepUi(val letter: String, val title: String, val tech: String, val descFa: String, val color: Color)

// -------------------------------------------------------------
// 12. VLAN 802.1Q & TRUNKING
// -------------------------------------------------------------
@Composable
fun VlanTrunkingCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VLAN 802.1Q & TRUNKING",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonCyan)
                    )
                    Text(
                        text = if (isPersian) "جداسازی ترافیک لایه ۲، هدر ۴ بایتی 802.1Q و Native VLAN" else "Access vs Trunk ports, Native VLAN & ROAS routing",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(NeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SettingsEthernet, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
            }

            // Port Types Comparison
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkHeaderBg,
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("ACCESS PORT", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("• Carries traffic for 1 single VLAN\n• Frames are UNTAGGED\n• Connected to end-hosts/PCs", color = Color.White, fontSize = 10.sp)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkHeaderBg,
                    border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("TRUNK PORT (802.1Q)", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("• Carries traffic for multiple VLANs\n• Adds 4-byte 802.1Q tag (VID 12-bit)\n• Interconnects switch-to-switch", color = Color.White, fontSize = 10.sp)
                    }
                }
            }

            // Cisco CLI Cheat Sheet
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black,
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("CISCO SWITCHPORT SYNTAX:", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Text("interface GigabitEthernet0/1\n switchport mode trunk\n switchport trunk allowed vlan 10,20,30\n switchport trunk native vlan 99", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 13. SUBNETTING & CIDR CHEAT SHEET
// -------------------------------------------------------------
@Composable
fun SubnettingCheatSheetCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "IPV4 SUBNETTING & MAGIC NUMBER",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonYellow)
                    )
                    Text(
                        text = if (isPersian) "فرمول جادویی ۲۵۶ منهای ماسک و جدول سریع CIDR" else "Magic Number Block Size calculation & Usable Hosts",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(NeonYellow.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = NeonYellow, modifier = Modifier.size(20.dp))
                }
            }

            // CIDR Quick Matrix
            val subnets = listOf(
                Triple("/24", "255.255.255.0", "254 hosts (Block: 256)"),
                Triple("/25", "255.255.255.128", "126 hosts (Block: 128)"),
                Triple("/26", "255.255.255.192", "62 hosts (Block: 64)"),
                Triple("/27", "255.255.255.224", "30 hosts (Block: 32)"),
                Triple("/28", "255.255.255.240", "14 hosts (Block: 16)"),
                Triple("/29", "255.255.255.248", "6 hosts (Block: 8)"),
                Triple("/30", "255.255.255.252", "2 hosts (Point-to-Point WAN)")
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                subnets.forEach { (prefix, mask, hosts) ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkHeaderBg,
                        border = BorderStroke(0.6.dp, NeonYellow.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(prefix, color = NeonYellow, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            Text(mask, color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            Text(hosts, color = NeonGreen, fontSize = 10.sp)
                        }
                    }
                }
            }

            // Magic Number Explanation
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NeonYellow.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, NeonYellow.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isPersian) "فرمول Magic Number: اندازه بلاک شبکه برابر است با ۲۵۶ منهای آخرین اکتت Subnet Mask. مثلاً در ماسک 255.255.255.224، اندازه بلاک = 256 - 224 = 32 است. بنابراین ساب‌نت‌ها با گام‌های ۳۲تایی شروع می‌شوند: 0، 32، 64، 96 و..."
                           else "Magic Number Formula: Block Size = 256 - (Last non-zero octet of subnet mask). Example: /27 (mask .224) -> Block size = 256 - 224 = 32. Networks jump by 32 (0, 32, 64, 96...).",
                    color = Color.White,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 14. NETWORK PORTS & PROTOCOLS MATRIX
// -------------------------------------------------------------
@Composable
fun PortsProtocolsMatrixCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WELL-KNOWN PORTS MATRIX",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonGreen)
                    )
                    Text(
                        text = if (isPersian) "ماتریس پورت‌های استاندارد IANA و پروتکل‌های لایه ۴" else "Standard network ports, transport types & security notes",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(NeonGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Numbers, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
                }
            }

            val ports = listOf(
                PortMatrixUi("20/21", "FTP", "TCP", "File Transfer (Cleartext, insecure)"),
                PortMatrixUi("22", "SSH / SFTP", "TCP", "Secure Shell encrypted remote management"),
                PortMatrixUi("25", "SMTP", "TCP", "Mail routing between MTAs"),
                PortMatrixUi("53", "DNS", "UDP/TCP", "Domain Name System queries & zone transfers"),
                PortMatrixUi("67/68", "DHCP", "UDP", "Server (67) and Client (68) IP assignment"),
                PortMatrixUi("80", "HTTP", "TCP", "Unencrypted World Wide Web"),
                PortMatrixUi("123", "NTP", "UDP", "Network Time Protocol clock synchronization"),
                PortMatrixUi("161", "SNMP", "UDP", "Network management & monitoring polling"),
                PortMatrixUi("389/636", "LDAP / LDAPS", "TCP", "Active Directory directory service queries"),
                PortMatrixUi("443", "HTTPS", "TCP", "HTTP over TLS encrypted web transport"),
                PortMatrixUi("445", "SMB", "TCP", "Server Message Block file shares (Windows)"),
                PortMatrixUi("1433", "MSSQL", "TCP", "Microsoft SQL Server database connectivity"),
                PortMatrixUi("3389", "RDP", "TCP/UDP", "Remote Desktop Protocol desktop access")
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ports.forEach { p ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkHeaderBg,
                        border = BorderStroke(0.6.dp, NeonGreen.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(p.port, color = NeonGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(60.dp))
                                Text(p.service, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(90.dp))
                            }
                            Text(p.proto, color = NeonCyan, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            Text(p.desc, color = Color.Gray, fontSize = 9.sp, modifier = Modifier.width(130.dp))
                        }
                    }
                }
            }
        }
    }
}

private data class PortMatrixUi(val port: String, val service: String, val proto: String, val desc: String)

// -------------------------------------------------------------
// 15. NAT & PAT MECHANICS
// -------------------------------------------------------------
@Composable
fun NatMechanicsCard(isPersian: Boolean) {
    val tokens = LocalNetOpsColors.current

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, tokens.glassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NAT & PAT (NETWORK ADDRESS TRANSLATION)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NeonPink)
                    )
                    Text(
                        text = if (isPersian) "ترجمه آدرس‌های محلی خصوصی به عمومی با PAT Overload" else "Inside Local, Inside Global, Outside Global & Port overloading",
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(NeonPink.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Transform, contentDescription = null, tint = NeonPink, modifier = Modifier.size(20.dp))
                }
            }

            // NAT Types
            val natTypes = listOf(
                "Static NAT (1:1)" to "نگاشت ۱ به ۱ یک آی‌پی خصوصی به یک آی‌پی عمومی (مناسب وب‌سرور یا میل‌سرور داخلی)",
                "Dynamic NAT (Pool)" to "نگاشت گروهی به مجموعه‌ای از IPهای عمومی بر اساس درخواست ورود اول (First Come)",
                "PAT (NAT Overload)" to "نگاشت هزاران سیستم داخلی به تنها ۱ IP عمومی با استفاده از شماره پورت‌های تصادفی سورس"
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                natTypes.forEach { (type, desc) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkHeaderBg,
                        border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(type, color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(if (isPersian) desc else type, color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }

            // NAT Terminology Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black,
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("CISCO NAT TERMINOLOGY:", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Text("• Inside Local: 192.168.1.50 (Private IP of internal host)\n• Inside Global: 217.218.10.5:45212 (Public IP allocated on router WAN)\n• Outside Global: 8.8.8.8:53 (Public IP of target internet server)\n• Outside Local: IP of destination host as known on inside network", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
