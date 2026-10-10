package com.example.ui.components

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * HelpdeskVisualMaps (v2.0.1)
 *
 * Graphical troubleshooting mode for the Helpdesk section: interactive
 * decision-tree "flow posters" in the same CyberKid infographic language as
 * the Academy visual maps. Each map walks the NOC engineer from symptom to
 * resolution with YES/NO branches and copyable verification commands.
 */
@Composable
fun HelpdeskVisualMapsSection(isPersian: Boolean = false) {
    val tokens = LocalNetOpsColors.current
    var selectedMapIndex by remember { mutableIntStateOf(0) }

    val maps = listOf(
        "Internet Down" to "قطعی اینترنت سازمان",
        "RDP Not Connecting" to "قطعی اتصال RDP",
        "Slow Network" to "کندی شبکه و اینترنت",
        "AD Account Lockout" to "قفل شدن اکانت اکتیو دایرکتوری",
        "Printer Offline" to "قطعی پرینتر شبکه",
        "ESXi Host Down" to "قطعی هاست ESXi"
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

        when (selectedMapIndex) {
            0 -> InternetDownFlowCard(isPersian)
            1 -> RdpFailureFlowCard(isPersian)
            2 -> SlowNetworkFlowCard(isPersian)
            3 -> AdLockoutFlowCard(isPersian)
            4 -> PrinterOfflineFlowCard(isPersian)
            5 -> EsxiHostDownFlowCard(isPersian)
        }
    }
}

// -------------------------------------------------------------
// Shared flow building blocks
// -------------------------------------------------------------

@Composable
internal fun FlowPosterHeader(titleEn: String, titleFa: String, subtitleEn: String, subtitleFa: String, accent: Color, isPersian: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    val tokens = LocalNetOpsColors.current
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
}

@Composable
internal fun FlowStep(stepNum: Int, questionEn: String, questionFa: String, accent: Color, isPersian: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$stepNum",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, color = accent)
            )
        }
        Spacer(Modifier.width(10.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF0A101E),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.4f)),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (isPersian) questionFa else questionEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFD7E3F4),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
internal fun FlowOutcome(textEn: String, textFa: String, isPersian: Boolean, positive: Boolean) {
    val color = if (positive) NeonGreen else NeonOrange
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (positive) Icons.Default.CheckCircle else Icons.Default.Build,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isPersian) textFa else textEn,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD7E3F4), fontSize = 12.sp),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
internal fun FlowBranchArrow(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 34.dp)) {
        Icon(
            Icons.Default.ArrowDownward,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun FlowPoster(
    accent: Color,
    isPersian: Boolean,
    titleEn: String, titleFa: String, subtitleEn: String, subtitleFa: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkCardBg,
        border = BorderStroke(1.5.dp, accent.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FlowPosterHeader(titleEn, titleFa, subtitleEn, subtitleFa, accent, isPersian, icon)
            content()
        }
    }
}

// -------------------------------------------------------------
// 1. INTERNET DOWN
// -------------------------------------------------------------
@Composable
private fun InternetDownFlowCard(isPersian: Boolean) {
    FlowPoster(
        NeonCyan, isPersian,
        "INTERNET DOWN TRIAGE", "تریاژ قطعی اینترنت",
        "From the user's PC up to the ISP - in 6 steps",
        "از سیستم کاربر تا ISP، در ۶ مرحله",
        Icons.Default.WifiOff
    ) {
        FlowStep(1, "Is the link light / adapter UP? (ipconfig /all)", "لینک کارت شبکه بالا است؟ (ipconfig /all)", NeonCyan, isPersian)
        FlowOutcome("NO → check cable, switch port, NIC driver", "خیر → کابل، پورت سوئیچ و درایور کارت را چک کنید", isPersian, positive = false)
        FlowStep(2, "Does the PC have a valid IP (not 169.254.x.x)?", "IP معتبر دارد؟ (نه 169.254.x.x یعنی DHCP قطع است)", NeonCyan, isPersian)
        FlowOutcome("APIPA → DHCP server / scope exhausted", "APIPA → سرور DHCP یا اتمام محدوده را بررسی کنید", isPersian, positive = false)
        FlowStep(3, "Can you ping the default gateway?", "پیش‌فرض گیت‌وی پینگ می‌شود؟", NeonGreen, isPersian)
        FlowBranchArrow(if (isPersian) "خیر → لایه ۲/۳ داخلی" else "NO → internal L2/L3", NeonOrange)
        V2CommandRow("ping 192.168.1.1 -n 4", if (isPersian) "تست گیت‌وی" else "gateway test")
        FlowStep(4, "Can you ping a public IP (e.g. 4.2.2.4)?", "IP عمومی پینگ می‌شود؟ (مثل 4.2.2.4)", NeonGreen, isPersian)
        FlowOutcome("NO → router WAN / ISP uplink issue", "خیر → مشکل WAN روتر یا ISP", isPersian, positive = false)
        FlowStep(5, "Does DNS resolve? (nslookup google.com)", "DNS جواب می‌دهد؟ (nslookup google.com)", NeonPurple, isPersian)
        FlowOutcome("IP ok but DNS fail → DNS server / forwarder issue", "IP برقرار اما DNS قطع → مشکل سرور DNS یا فورواردر", isPersian, positive = false)
        FlowStep(6, "Only one user or everyone?", "فقط یک کاربر یا همه؟", NeonOrange, isPersian)
        FlowOutcome("Everyone → core switch / firewall / ISP; One user → local port or PC", "همه → سوئیچ هسته، فایروال یا ISP؛ یک نفر → پورت یا سیستم محلی", isPersian, positive = true)
    }
}

// -------------------------------------------------------------
// 2. RDP FAILURE
// -------------------------------------------------------------
@Composable
private fun RdpFailureFlowCard(isPersian: Boolean) {
    FlowPoster(
        NeonPurple, isPersian,
        "RDP NOT CONNECTING", "رفع مشکل اتصال RDP",
        "The classic server-remote-access decision tree",
        "درخت تصمیم کلاسیک دسترسی ریموت سرورها",
        Icons.Default.DesktopWindows
    ) {
        FlowStep(1, "Is the server online? (ping / ICMP allowed?)", "سرور روشن است؟ (پینگ مجاز است؟)", NeonPurple, isPersian)
        FlowStep(2, "Is port 3389 listening? (Test-NetConnection srv -Port 3389)", "پورت ۳۳۸۹ گوش می‌دهد؟ (Test-NetConnection)", NeonPurple, isPersian)
        FlowOutcome("Port closed → service TermService stopped or firewall rule", "پورت بسته → سرویس TermService یا فایروال", isPersian, positive = false)
        FlowStep(3, "RDP enabled at all? (System Properties / registry)", "RDP اصلاً فعال است؟", NeonCyan, isPersian)
        V2CommandRow("reg query \"HKLM\\SYSTEM\\CurrentControlSet\\Control\\Terminal Server\" /v fDenyTSConnections", "fDenyTSConnections")
        FlowStep(4, "Firewall rule 'Remote Desktop' enabled on profile?", "قانون فایروال Remote Desktop فعال است؟", NeonCyan, isPersian)
        V2CommandRow("Get-NetFirewallRule -DisplayGroup \"Remote Desktop\"", if (isPersian) "بررسی قانون" else "check rule")
        FlowStep(5, "Licensing / session limit? (qwinsta /server:srv)", "محدودیت لایسنس یا نشست؟", NeonGreen, isPersian)
        FlowOutcome("Two admin sessions max → shadow or log off idle sessions", "حداکثر ۲ نشست ادمین → نشست بیکار را ببندید", isPersian, positive = true)
        Spacer(Modifier.height(2.dp))
        V2SectionHeader("PRO TIPS", Icons.Default.TipsAndUpdates, NeonYellow)
        V2CheckItem("NLA mismatch blocks clients before the logon screen", "ناسازگاری NLA قبل از صفحه لاگین جلوی اتصال را می‌گیرد", isPersian)
        V2CheckItem("RDP Shadowing needs 'Remote Desktop Services' policies", "شدو کردن نیازمند پالیسی‌های RDS است", isPersian)
    }
}

// -------------------------------------------------------------
// 3. SLOW NETWORK
// -------------------------------------------------------------
@Composable
private fun SlowNetworkFlowCard(isPersian: Boolean) {
    FlowPoster(
        NeonYellow, isPersian,
        "SLOW NETWORK DIAGNOSIS", "تشخیص کندی شبکه",
        "Latency? Loss? Duplex? Or just a heavy evening?",
        "تأخیر؟ پکت‌لاس؟ دوپلکس؟ یا ترافیک سنگین؟",
        Icons.Default.Speed
    ) {
        FlowStep(1, "Baseline ping to gateway: latency < 2ms, 0% loss?", "پینگ گیت‌وی: تأخیر زیر ۲ms و صفر درصد لاس؟", NeonYellow, isPersian)
        FlowOutcome("Loss to gateway → cable / switch port / duplex mismatch", "لاس تا گیت‌وی → کابل، پورت سوئیچ یا دوپلکس", isPersian, positive = false)
        FlowStep(2, "Traceroute: which hop starts the latency?", "کدام هاپ تأخیر را شروع می‌کند؟ (tracert)", NeonYellow, isPersian)
        FlowStep(3, "Link speed correct? (1Gbps not 100Mbps?)", "سرعت لینک درست است؟ (۱ گیگ نه ۱۰۰ مگ)", NeonCyan, isPersian)
        V2CommandRow("Get-NetAdapter | ft Name,Status,LinkSpeed", if (isPersian) "سرعت لینک" else "link speed")
        FlowStep(4, "Interface errors? (CRC, collisions)", "خطای CRC یا کولیژن روی پورت؟", NeonCyan, isPersian)
        V2CommandRow("netsh interface statistics", if (isPersian) "آمار پکت خطا" else "interface error stats")
        FlowStep(5, "Bandwidth saturation? (monitoring / NetFlow)", "اشباع پهنای باند؟ (مانیتورینگ/NetFlow)", NeonPurple, isPersian)
        FlowOutcome("QoS policy or traffic shaping for heavy users", "پالیسی QoS یا شکل‌دهی ترافیک اعمال کنید", isPersian, positive = true)
        Spacer(Modifier.height(2.dp))
        V2SectionHeader("QUICK WINS", Icons.Default.Bolt, NeonGreen)
        V2CheckItem("Duplex mismatch shows as slow + CRC errors, not as down", "ناهماهنگی دوپلکس به‌صورت کندی و CRC دیده می‌شود نه قطعی", isPersian)
        V2CheckItem("Always test wired before blaming Wi-Fi", "قبل از مقصر دانستن وای‌فای، تست سیمی انجام دهید", isPersian)
    }
}

// -------------------------------------------------------------
// 4. AD ACCOUNT LOCKOUT
// -------------------------------------------------------------
@Composable
private fun AdLockoutFlowCard(isPersian: Boolean) {
    FlowPoster(
        NeonPink, isPersian,
        "AD ACCOUNT LOCKOUT", "رفع قفل شدن اکانت دامنه",
        "Find the offending device in 5 steps",
        "پیدا کردن دستگاه مسبب قفل شدن در ۵ مرحله",
        Icons.Default.LockPerson
    ) {
        FlowStep(1, "Unlock the account temporarily (Active Directory UC)", "ابتدا اکانت را موقتاً آنلاک کنید", NeonPink, isPersian)
        FlowStep(2, "Identify the PDC and read lockout events 4740", "روی PDC ایونت ۴۷۴۰ را بخوانید", NeonPink, isPersian)
        V2CommandRow("Get-WinEvent -FilterHashtable @{LogName='Security';Id=4740} -MaxEvents 20", if (isPersian) "منبع قفل‌ها" else "lockout sources")
        FlowStep(3, "Note the 'Caller Computer Name' in the event", "نام کامپیوتر مسبب را یادداشت کنید", NeonCyan, isPersian)
        FlowStep(4, "Common culprits: saved credentials, mapped drives, services, mobile mail app", "متهمان همیشگی: رمز ذخیره‌شده، درایو متصل، سرویس، اپ میل موبایل", NeonCyan, isPersian)
        V2CommandRow("cmdkey /list", if (isPersian) "رمزهای ذخیره‌شده" else "saved credentials")
        V2CommandRow("rundll32.exe keymgr.dll,KRShowKeyMgr", if (isPersian) "مدیریت Credentials" else "credential manager")
        FlowStep(5, "Stale token? Reboot the offending device after cleanup", "بعد از پاک‌سازی، دستگاه مسبب را ریستارت کنید", NeonGreen, isPersian)
        FlowOutcome("Lockout stops → root cause was cached old password", "قفل‌شدن قطع می‌شود → ریشه، رمز قدیمی کش‌شده بود", isPersian, positive = true)
        Spacer(Modifier.height(2.dp))
        V2SectionHeader("PREVENTION", Icons.Default.Shield, NeonGreen)
        V2CheckItem("Educate users: change password on ALL devices, not just the PC", "کاربران: رمز جدید را روی همه دستگاه‌ها وارد کنید", isPersian)
        V2CheckItem("Audit service accounts running with user credentials", "سرویس‌های تحت رمز کاربران را ممیزی کنید", isPersian)
    }
}

// -------------------------------------------------------------
// 5. PRINTER OFFLINE
// -------------------------------------------------------------
@Composable
private fun PrinterOfflineFlowCard(isPersian: Boolean) {
    FlowPoster(
        NeonGreen, isPersian,
        "NETWORK PRINTER OFFLINE", "قطعی پرینتر شبکه",
        "Print server, IP change, or spooler?",
        "پرینت‌سرور، تغییر IP یا اسپولر؟",
        Icons.Default.Print
    ) {
        FlowStep(1, "Can you ping the printer IP? (menu → print config page)", "آی‌پی پرینتر پینگ می‌شود؟", NeonGreen, isPersian)
        FlowOutcome("NO → cable / switch port / printer powered off", "خیر → کابل، پورت سوئیچ یا خاموشی دستگاه", isPersian, positive = false)
        FlowStep(2, "IP changed by DHCP? Fix a DHCP reservation or static IP", "IP عوض شده؟ رزرو DHCP یا IP ثابت بدهید", NeonGreen, isPersian)
        FlowStep(3, "Print queue stuck? Clear spooler on the print server", "صف چاپ گیر کرده؟ اسپولر سرور را پاک کنید", NeonCyan, isPersian)
        V2CommandRow("net stop spooler && del /Q %systemroot%\\System32\\spool\\PRINTERS\\* && net start spooler", if (isPersian) "پاک‌سازی اسپولر" else "clean spooler")
        FlowStep(4, "Driver mismatch after firmware update?", "درایور بعد از آپدیت فریم‌ور به‌هم ریخته؟", NeonCyan, isPersian)
        FlowStep(5, "Port monitor matches protocol (RAW 9100 / LPR / IPP)?", "پروتکل پورت درست است؟ (RAW 9100 / LPR / IPP)", NeonPurple, isPersian)
        FlowOutcome("Queue flows → job test page on the server", "صف برقرار شد → چاپ صفحه تست از سرور", isPersian, positive = true)
    }
}

// -------------------------------------------------------------
// 6. ESXI HOST DOWN
// -------------------------------------------------------------
@Composable
private fun EsxiHostDownFlowCard(isPersian: Boolean) {
    FlowPoster(
        NeonOrange, isPersian,
        "ESXi HOST DOWN", "قطعی هاست ESXi",
        "vCenter red alert - what to check before you panic",
        "هشدار قرمز vCenter - قبل از وحشت چه چیزهایی را چک کنیم",
        Icons.Default.Dns
    ) {
        FlowStep(1, "Ping host management IP (vmk0) directly", "آی‌پی مدیریت هاست را مستقیم پینگ کنید", NeonOrange, isPersian)
        FlowOutcome("NO ping → physical link, iDRAC/iLO console, or power", "بدون پینگ → لینک فیزیکی، کنسول iDRAC/iLO یا برق", isPersian, positive = false)
        FlowStep(2, "vCenter shows 'not responding' but host pings?", "هاست پینگ می‌شود ولی vCenter قطع می‌گوید؟", NeonOrange, isPersian)
        V2CommandRow("Get-VMHost | ft Name,ConnectionState,PowerState", if (isPersian) "وضعیت هاست‌ها" else "host states")
        FlowStep(3, "Management agents restarted?", "ایجنت‌های مدیریت ریستارت شده؟", NeonCyan, isPersian)
        V2CommandRow("/etc/init.d/hostd restart && /etc/init.d/vpxa restart", if (isPersian) "از DCUI/SSH" else "via DCUI/SSH")
        FlowStep(4, "Storage heartbeats alive? (datastore browser)", "دیتاستورها سالم هستند؟", NeonCyan, isPersian)
        FlowStep(5, "If host is truly dead: vMotion VMs from remaining hosts, then HA admission control", "اگر هاست مرده است: VMها را با vMotion جابه‌جا و HA را چک کنید", NeonPurple, isPersian)
        FlowOutcome("VMs back online → document RCA and RMA the host", "ماشین‌های مجازی برگشتند → RCA و تعمیر هاست را ثبت کنید", isPersian, positive = true)
        Spacer(Modifier.height(2.dp))
        V2SectionHeader("CRITICAL RULES", Icons.Default.Report, NeonPink)
        V2WarnItem("Never hard-reset a host with running VMs unless HA already failed", "هاست با VMهای روشن را هارد ریست نکنید مگر وقتی HA شکست خورده", isPersian)
        V2WarnItem("Check HA admission control BEFORE maintenance, not after a crash", "کنترل HA را قبل از تعمیرات بررسی کنید نه بعد از خرابی", isPersian)
    }
}
