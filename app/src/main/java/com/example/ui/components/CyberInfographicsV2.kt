package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * CyberInfographicsV2 (v2.0.1)
 *
 * New generation of visual cyber maps inspired by the CyberKid reference
 * posters provided by the product owner:
 *  - Kerberos Authentication Deep-Dive (reference image 2)
 *  - Active Directory Users / Groups / GPO Command Reference (image 3)
 *  - Active Directory Misconfigurations & Defensive Audit (image 4)
 *  - OSPF vs BGP Routing Map
 *  - IPsec VPN Tunnel Phases
 *  - Windows Networking & Helpdesk Command Arsenal
 */

// -------------------------------------------------------------
// Shared building blocks (CyberKid poster style)
// -------------------------------------------------------------

@Composable
internal fun V2PosterCard(
    titleEn: String,
    titleFa: String,
    subtitleEn: String,
    subtitleFa: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    isPersian: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalNetOpsColors.current
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, accent.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isPersian) titleFa else titleEn,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = accent,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = if (isPersian) subtitleFa else subtitleEn,
                        style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                }
            }
            content()
        }
    }
}

@Composable
internal fun V2SectionHeader(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
internal fun V2CommandRow(cmd: String, comment: String) {
    val context = LocalContext.current
    val tokens = LocalNetOpsColors.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF070B14),
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("cmd", cmd))
                Toast.makeText(context, "Copied: $cmd", Toast.LENGTH_SHORT).show()
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ">",
                color = NeonGreen,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = cmd,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFB2EBF2),
                    fontSize = 11.sp
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = comment,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = NeonYellow.copy(alpha = 0.85f),
                    fontSize = 9.sp
                )
            )
        }
    }
}

@Composable
internal fun V2CheckItem(textEn: String, textFa: String, isPersian: Boolean, color: Color = NeonGreen) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isPersian) textFa else textEn,
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD7E3F4), fontSize = 12.sp),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
internal fun V2WarnItem(textEn: String, textFa: String, isPersian: Boolean) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            tint = NeonOrange,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isPersian) textFa else textEn,
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFFD9C2), fontSize = 12.sp),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
internal fun V2StatBadge(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFB9C6DA), fontSize = 9.sp)
            )
        }
    }
}

@Composable
internal fun V2PortRow(port: String, proto: String, service: String, note: String, color: Color = NeonCyan) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0A101E))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            port,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 11.sp
            ),
            modifier = Modifier.width(64.dp)
        )
        Text(
            proto,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                color = NeonPurple,
                fontSize = 10.sp
            ),
            modifier = Modifier.width(52.dp)
        )
        Text(
            service,
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFD7E3F4), fontSize = 11.sp),
            modifier = Modifier.weight(1f)
        )
        Text(
            note,
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8593A8), fontSize = 9.sp)
        )
    }
}

// -------------------------------------------------------------
// 15. KERBEROS AUTHENTICATION DEEP-DIVE (reference image 2)
// -------------------------------------------------------------
@Composable
fun KerberosDeepDiveCard(isPersian: Boolean) {
    V2PosterCard(
        titleEn = "KERBEROS AUTH FLOW",
        titleFa = "جریان احراز هویت کربروس",
        subtitleEn = "Ticket-based authentication between Client, KDC and Services",
        subtitleFa = "احراز هویت مبتنی بر تیکت بین کلاینت، KDC و سرویس‌ها",
        icon = Icons.Default.VpnKey,
        accent = NeonPurple,
        isPersian = isPersian
    ) {
        // Flow steps
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            KerberosStep(1, "AS-REQ", "Client → KDC (username, pre-auth)", NeonCyan)
            KerberosStep(2, "AS-REP", "KDC → Client (TGT encrypted with krbtgt hash)", NeonCyan)
            KerberosStep(3, "TGS-REQ", "Client → KDC (presents TGT, requests service)", NeonPurple)
            KerberosStep(4, "TGS-REP", "KDC → Client (Service Ticket)", NeonPurple)
            KerberosStep(5, "AP-REQ", "Client → Service (presents ST, mutual auth)", NeonGreen)
        }

        Spacer(Modifier.height(4.dp))
        V2SectionHeader("USEFUL KERBEROS COMMANDS", Icons.Default.Terminal, NeonGreen)
        V2CommandRow("klist", if (isPersian) "مشاهده تیکت‌ها" else "view tickets")
        V2CommandRow("klist tickets", if (isPersian) "جزئیات تیکت‌ها" else "ticket details")
        V2CommandRow("kinit user@DOMAIN.LOCAL", if (isPersian) "دریافت TGT (لینوکس)" else "obtain TGT (Linux)")
        V2CommandRow("klist -k", if (isPersian) "کلید‌تبت‌های سرویس" else "keytab list")
        V2CommandRow("Get-ADUser user -Properties ServicePrincipalName", if (isPersian) "مشاهده SPNها" else "list SPNs")

        Spacer(Modifier.height(4.dp))
        V2SectionHeader("COMMON PORTS", Icons.Default.SettingsEthernet, NeonCyan)
        V2PortRow("88", "TCP/UDP", "Kerberos v5", "Authentication")
        V2PortRow("389", "TCP/UDP", "LDAP", "Directory queries")
        V2PortRow("636", "TCP", "LDAPS", "LDAP over SSL")
        V2PortRow("3268", "TCP", "Global Catalog", "Forest-wide search")
        V2PortRow("464", "TCP/UDP", "kpasswd", "Password change")

        Spacer(Modifier.height(6.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = NeonPurple.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                V2CheckItem("Uses tickets instead of sending passwords on the wire", "به‌جای ارسال رمز عبور از تیکت استفاده می‌کند", isPersian)
                V2CheckItem("Provides mutual authentication (client AND server)", "احراز هویت دوطرفه کلاینت و سرور را تضمین می‌کند", isPersian)
                V2WarnItem("Time skew > 5 minutes breaks Kerberos everywhere", "اختلاف ساعت بیش از ۵ دقیقه کربروس را از کار می‌اندازد", isPersian)
            }
        }
    }
}

@Composable
private fun KerberosStep(num: Int, title: String, desc: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text("$num", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = color))
        }
        Spacer(Modifier.width(10.dp))
        Text(
            title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.width(72.dp)
        )
        Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB9C6DA), fontSize = 11.sp))
    }
}

// -------------------------------------------------------------
// 16. AD USERS / GROUPS / GPO COMMAND REFERENCE (reference image 3)
// -------------------------------------------------------------
@Composable
fun AdUsersGroupsCommandsCard(isPersian: Boolean) {
    V2PosterCard(
        titleEn = "AD USERS, GROUPS & GPO",
        titleFa = "کاربران، گروه‌ها و GPO در اکتیو دایرکتوری",
        subtitleEn = "PowerShell command arsenal for daily AD administration",
        subtitleFa = "مجموعه کامندهای PowerShell برای مدیریت روزانه اکتیو دایرکتوری",
        icon = Icons.Default.Groups,
        accent = NeonCyan,
        isPersian = isPersian
    ) {
        V2SectionHeader("USER COMMANDS", Icons.Default.Person, NeonCyan)
        V2CommandRow("Get-ADUser -Filter *", if (isPersian) "لیست همه کاربران" else "list all users")
        V2CommandRow("Get-ADUser user1 -Properties *", if (isPersian) "همه ویژگی‌های کاربر" else "all attributes")
        V2CommandRow("Get-ADUser -Filter 'Enabled -eq \$false'", if (isPersian) "کاربران غیرفعال" else "disabled users")
        V2CommandRow("Get-ADUser -Filter * -Properties LastLogonDate", if (isPersian) "آخرین ورود کاربران" else "last logon times")

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("GROUP COMMANDS", Icons.Default.Group, NeonPurple)
        V2CommandRow("Get-ADGroup -Filter *", if (isPersian) "لیست همه گروه‌ها" else "list all groups")
        V2CommandRow("Get-ADGroupMember \"Domain Admins\"", if (isPersian) "اعضای گروه مهم" else "privileged members")
        V2CommandRow("Get-ADPrincipalGroupMembership user1", if (isPersian) "گروه‌های یک کاربر" else "groups of a user")
        V2CommandRow("Add-ADGroupMember \"Group1\" user1", if (isPersian) "افزودن عضو" else "add member")

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("GROUP SCOPES & TYPE", Icons.Default.Category, NeonGreen)
        GroupScopeRow("Domain Local", "Members from any domain, resource access in local domain", if (isPersian) "دسترسی منابع در دامین محلی" else "")
        GroupScopeRow("Global", "Members from same domain, access in any trusting domain", if (isPersian) "اعضای همان دامین، دسترسی همه‌جا" else "")
        GroupScopeRow("Universal", "Members from any domain in the forest", if (isPersian) "کل جنگل (Forest)" else "")

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("GPO MANAGEMENT", Icons.Default.Policy, NeonOrange)
        V2CommandRow("Get-GPO -All", if (isPersian) "لیست همه GPOها" else "list all GPOs")
        V2CommandRow("Get-GPReport \"Default Domain Policy\" -ReportType Html", if (isPersian) "گزارش کامل GPO" else "full GPO report")
        V2CommandRow("Get-GPInheritance -Target \"OU=IT,DC=corp,DC=local\"", if (isPersian) "ارث‌بری GPO" else "GPO inheritance")
        V2CommandRow("New-GPLink -Name \"Hardening\" -Target \"OU=PCs,DC=corp,DC=local\"", if (isPersian) "لینک کردن GPO" else "link a GPO")
    }
}

@Composable
private fun GroupScopeRow(scope: String, descEn: String, descFa: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0A101E))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            scope,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NeonGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            ),
            modifier = Modifier.width(90.dp)
        )
        Text(
            descEn,
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFB9C6DA), fontSize = 10.sp)
        )
    }
}

// -------------------------------------------------------------
// 17. AD MISCONFIGURATIONS & DEFENSIVE AUDIT (reference image 4)
// -------------------------------------------------------------
@Composable
fun AdMisconfigDefenseCard(isPersian: Boolean) {
    V2PosterCard(
        titleEn = "AD SECURITY AUDIT",
        titleFa = "پیکربندی‌های غلط و ممیزی دفاعی اکتیو دایرکتوری",
        subtitleEn = "Identify - Assess - Mitigate - Monitor",
        subtitleFa = "شناسایی، ارزیابی، مقابله و مانیتورینگ",
        icon = Icons.Default.Security,
        accent = NeonPink,
        isPersian = isPersian
    ) {
        V2SectionHeader("1. COMMON MISCONFIGURATIONS", Icons.Default.Report, NeonPink)
        V2WarnItem("Weak or guessable passwords on admin & service accounts", "رمزهای ضعیف روی اکانت‌های مدیریتی و سرویس", isPersian)
        V2WarnItem("Over-privileged user and service account memberships", "دسترسی بیش از حد اکانت‌ها", isPersian)
        V2WarnItem("Kerberoastable accounts (SPN without pre-auth)", "اکانت‌های قابل حمله Kerberoasting", isPersian)
        V2WarnItem("Unconstrained or overly permissive delegation", "Delegation نامحدود و پرخطر", isPersian)
        V2WarnItem("LAPS not implemented (local admin password reuse)", "عدم پیاده‌سازی LAPS و تکرار رمز ادمین محلی", isPersian)
        V2WarnItem("SMB signing / LDAP signing not enforced", "عدم الزام امضای SMB و LDAP", isPersian)
        V2WarnItem("Stale / unused accounts and computers", "اکانت‌های راکد و بدون استفاده", isPersian)

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("2. PRIVILEGED GROUP AUDIT", Icons.Default.AdminPanelSettings, NeonCyan)
        V2CommandRow("Get-ADGroupMember \"Domain Admins\"", if (isPersian) "اعضای Domain Admins" else "domain admins")
        V2CommandRow("Get-ADGroupMember \"Enterprise Admins\"", if (isPersian) "اعضای Enterprise Admins" else "enterprise admins")
        V2CommandRow("Get-ADGroupMember \"Backup Operators\"", if (isPersian) "اعضای Backup Operators" else "backup operators")

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("3. KERBEROASTABLE ACCOUNTS", Icons.Default.NoEncryption, NeonOrange)
        V2CommandRow("Get-ADUser -Filter {DoesNotRequirePreAuth -eq \$true}", if (isPersian) "بدون Pre-Auth" else "no pre-auth")
        V2CommandRow("Get-ADUser -Filter {ServicePrincipalName -ne \$null}", if (isPersian) "اکانت‌های دارای SPN" else "accounts with SPN")

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("4. PASSWORD & LOCKOUT POLICY", Icons.Default.Password, NeonGreen)
        V2CommandRow("Get-ADDefaultDomainPasswordPolicy", if (isPersian) "پالیسی رمز دامین" else "domain password policy")
        V2CommandRow("Get-ADFineGrainedPasswordPolicy -Filter *", if (isPersian) "پالیسی‌های جزئی" else "fine-grained policies")
        V2CommandRow("net accounts", if (isPersian) "تنظیمات لاک‌آوت محلی" else "local lockout settings")

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("KEY TAKEAWAYS", Icons.Default.Lightbulb, NeonYellow)
        V2CheckItem("Follow least privilege - give only the required access", "حداقل دسترسی لازم را بدهید", isPersian)
        V2CheckItem("Remove unnecessary rights; keep privileged groups clean", "دسترسی‌های اضافه را حذف کنید", isPersian)
        V2CheckItem("Monitor and audit changes with Event IDs 4728/4732/4756", "تغییرات گروه‌ها را با ایونت‌آی‌دی‌های مذکور مانیتور کنید", isPersian)
        V2CheckItem("Harden protocols: enforce LDAP/SMB signing, disable NTLMv1", "امضای پروتکل‌ها را الزامی و NTLMv1 را خاموش کنید", isPersian)
        V2CheckItem("Use LAPS & MFA for admin access", "برای دسترسی ادمین از LAPS و MFA استفاده کنید", isPersian)
    }
}

// -------------------------------------------------------------
// 18. OSPF vs BGP ROUTING MAP
// -------------------------------------------------------------
@Composable
fun OspfBgpRoutingCard(isPersian: Boolean) {
    V2PosterCard(
        titleEn = "OSPF vs BGP ROUTING",
        titleFa = "مقایسه مسیریابی OSPF و BGP",
        subtitleEn = "Interior gateway protocols vs the Internet's routing backbone",
        subtitleFa = "پروتکل‌های داخلی سازمان در برابر ستون فقرات مسیریابی اینترنت",
        icon = Icons.Default.Hub,
        accent = NeonGreen,
        isPersian = isPersian
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            V2StatBadge("LSA/LSDB", "OSPF Database", NeonCyan, modifier = Modifier.weight(1f))
            V2StatBadge("AS-Path", "BGP Attribute", NeonPurple, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(2.dp))

        V2SectionHeader("QUICK COMPARISON", Icons.Default.CompareArrows, NeonCyan)
        RouteCompareRow("Purpose", "Inside the org (IGP)", "Between orgs (EGP)")
        RouteCompareRow("Algorithm", "Dijkstra SPF", "Path-Vector + Policies")
        RouteCompareRow("Convergence", "Fast (seconds)", "Slower (policy-driven)")
        RouteCompareRow("Scale", "Enterprise campus", "Internet / MPLS / DC interconnect")
        RouteCompareRow("Port/Proto", "IP proto 89, multicast 224.0.0.5/6", "TCP 179")

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("USEFUL COMMANDS", Icons.Default.Terminal, NeonGreen)
        V2CommandRow("show ip ospf neighbor", if (isPersian) "همسایه‌های OSPF" else "OSPF neighbors")
        V2CommandRow("show ip ospf database", if (isPersian) "دیتابیس LSA" else "LSA database")
        V2CommandRow("show ip bgp summary", if (isPersian) "خلاصه همسایه‌های BGP" else "BGP neighbor summary")
        V2CommandRow("show ip route bgp", if (isPersian) "روت‌های یادگرفته از BGP" else "BGP routes")
        V2CommandRow("/routing ospf neighbor print (MikroTik)", if (isPersian) "همسایه‌های OSPF میکروتیک" else "MikroTik OSPF neighbors")

        Spacer(Modifier.height(6.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = NeonGreen.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                V2CheckItem("OSPF areas limit LSA flooding - keep Area 0 backbone clean", "ناحیه ۰ (Backbone) را تمیز نگه دارید", isPersian)
                V2CheckItem("BGP attributes: Local-Pref decides OUTBOUND, MED influences INBOUND", "Local-Pref برای خروجی و MED برای ورودی", isPersian)
                V2WarnItem("Never originate a BGP announcement without prefix-list filtering", "بدون فیلتر prefix-list آنانس BGP نکنید", isPersian)
            }
        }
    }
}

@Composable
private fun RouteCompareRow(feature: String, ospf: String, bgp: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0A101E))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            feature,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NeonYellow, fontSize = 10.sp),
            modifier = Modifier.width(78.dp)
        )
        Text(
            ospf,
            style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan, fontSize = 10.sp),
            modifier = Modifier.weight(1f)
        )
        Text(
            bgp,
            style = MaterialTheme.typography.labelSmall.copy(color = NeonPurple, fontSize = 10.sp),
            modifier = Modifier.weight(1.2f)
        )
    }
}

// -------------------------------------------------------------
// 19. IPSEC VPN TUNNEL PHASES
// -------------------------------------------------------------
@Composable
fun IpsecVpnPhasesCard(isPersian: Boolean) {
    V2PosterCard(
        titleEn = "IPSEC VPN PHASES",
        titleFa = "فازهای تونل IPsec VPN",
        subtitleEn = "IKE Phase 1 & 2 - the handshake behind every site-to-site VPN",
        subtitleFa = "فازهای IKE پشت هر VPN سایت‌به‌سایت سازمانی",
        icon = Icons.Default.VpnLock,
        accent = NeonOrange,
        isPersian = isPersian
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            VpnPhaseRow("Phase 1", "ISAKMP SA", "DH group, encryption + hash, Main/Aggressive mode, UDP 500/4500", NeonCyan)
            VpnPhaseRow("Phase 2", "IPsec SA", "PFS, ESP tunnel mode, proxy-IDs / traffic selectors", NeonPurple)
            VpnPhaseRow("Data Path", "ESP (proto 50)", "Encapsulate + encrypt payloads; NAT-T wraps in UDP 4500", NeonGreen)
        }

        Spacer(Modifier.height(4.dp))
        V2SectionHeader("USEFUL COMMANDS", Icons.Default.Terminal, NeonGreen)
        V2CommandRow("show crypto isakmp sa", if (isPersian) "وضعیت فاز ۱ سیسکو" else "Phase 1 status (Cisco)")
        V2CommandRow("show crypto ipsec sa", if (isPersian) "وضعیت فاز ۲ و کانترها" else "Phase 2 + counters")
        V2CommandRow("/ip ipsec sa print (MikroTik)", if (isPersian) "SAهای میکروتیک" else "MikroTik SAs")
        V2CommandRow("diag vpn ike gateway list (FortiGate)", if (isPersian) "گیت‌وی‌های فورتی‌گیت" else "FortiGate gateways")

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("TROUBLESHOOTING CHECKLIST", Icons.Default.BuildCircle, NeonYellow)
        V2CheckItem("Match ISAKMP proposals exactly: DH group, AES, SHA, lifetime", "پروپوزال‌های فاز ۱ باید دقیقاً یکسان باشند", isPersian)
        V2CheckItem("Mirror proxy-IDs / subnets on both peers", "ساب‌نت‌های دو طرف باید قرینه باشند", isPersian)
        V2WarnItem("NAT between peers requires NAT-T (UDP 4500) enabled", "وجود NAT نیازمند فعال‌بودن NAT-T است", isPersian)
        V2WarnItem("Asymmetric routing silently drops ESP packets", "مسیریابی متقارن نباشد پکت‌های ESP دراپ می‌شوند", isPersian)
    }
}

@Composable
private fun VpnPhaseRow(phase: String, sa: String, desc: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0A101E))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            phase,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            ),
            modifier = Modifier.width(64.dp)
        )
        Column {
            Text(sa, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = color, fontSize = 11.sp))
            Text(desc, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFB9C6DA), fontSize = 10.sp))
        }
    }
}

// -------------------------------------------------------------
// 20. WINDOWS NETWORKING & HELPDESK COMMAND ARSENAL
// -------------------------------------------------------------
@Composable
fun WindowsNetCommandsCard(isPersian: Boolean) {
    V2PosterCard(
        titleEn = "WINDOWS NET COMMANDS",
        titleFa = "کامندهای شبکه ویندوز و هلپ‌دسک",
        subtitleEn = "Daily-driver CLI & PowerShell for troubleshooting Windows clients",
        subtitleFa = "کامندهای روزمره برای عیب‌یابی کلاینت‌های ویندوزی",
        icon = Icons.Default.Terminal,
        accent = NeonCyan,
        isPersian = isPersian
    ) {
        V2SectionHeader("IP & ADAPTER", Icons.Default.SettingsEthernet, NeonCyan)
        V2CommandRow("ipconfig /all", if (isPersian) "تنظیمات کامل آداپتور" else "full adapter config")
        V2CommandRow("ipconfig /release + /renew", if (isPersian) "تمدید اجاره DHCP" else "renew DHCP lease")
        V2CommandRow("Get-NetAdapter | ft Name,Status,LinkSpeed", if (isPersian) "وضعیت کارت شبکه" else "adapter status")
        V2CommandRow("Get-NetIPAddress -AddressFamily IPv4", if (isPersian) "IPهای v4" else "IPv4 addresses")

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("DNS & NAME RESOLUTION", Icons.Default.Dns, NeonPurple)
        V2CommandRow("ipconfig /flushdns", if (isPersian) "خالی کردن کش DNS" else "flush DNS cache")
        V2CommandRow("nslookup dc01.corp.local", if (isPersian) "تست سرور DNS" else "test DNS server")
        V2CommandRow("Resolve-DnsName mail.corp.local -Type MX", if (isPersian) "کوئری MX با پاورشل" else "PowerShell MX query")
        V2CommandRow("nltest /dsgetdc:corp.local", if (isPersian) "پیدا کردن DC" else "discover domain controller")

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("SMB / SHARES / RDP", Icons.Default.FolderShared, NeonGreen)
        V2CommandRow("net use \\\\srv\\share /user:corp\\user", if (isPersian) "اتصال به شیر" else "map a share")
        V2CommandRow("Test-NetConnection srv01 -Port 3389", if (isPersian) "تست پورت RDP" else "test RDP port")
        V2CommandRow("qwinsta /server:srv01", if (isPersian) "نشست‌های RDP فعال" else "active RDP sessions")
        V2CommandRow("mstsc /shadow:sessionId /v:srv01 /control", if (isPersian) "شدو کردن نشست کاربر" else "shadow a session")

        Spacer(Modifier.height(6.dp))
        V2SectionHeader("AD / DOMAIN / GPO", Icons.Default.Domain, NeonOrange)
        V2CommandRow("whoami /upn + /groups", if (isPersian) "کاربر فعلی و گروه‌ها" else "current user & groups")
        V2CommandRow("gpresult /r", if (isPersian) "GPOهای اعمال‌شده" else "applied GPOs")
        V2CommandRow("gpupdate /force", if (isPersian) "به‌روزرسانی فوری پالیسی" else "force policy refresh")
        V2CommandRow("netdom verify dc01", if (isPersian) "سلامت کانال دامین" else "verify secure channel")
    }
}
