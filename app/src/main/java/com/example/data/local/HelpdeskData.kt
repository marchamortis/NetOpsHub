package com.example.data.local

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TerminalGreen

enum class IssueSeverity(val label: String, val labelFa: String, val color: Color) {
    CRITICAL("CRITICAL", "بحرانی", StatusError),
    HIGH("HIGH", "بالا", StatusWarning),
    MEDIUM("MEDIUM", "متوسط", CyanAccent),
    LOW("LOW", "عادی", TerminalGreen)
}

data class TriageStep(
    val stepNum: Int,
    val title: String,
    val titleFa: String,
    val action: String,
    val actionFa: String
)

data class HelpdeskIssue(
    val id: String,
    val title: String,
    val titleFa: String,
    val category: String,
    val categoryFa: String,
    val severity: IssueSeverity,
    val symptom: String,
    val symptomFa: String,
    val rootCauses: List<String>,
    val rootCausesFa: List<String>,
    val triageSteps: List<TriageStep>,
    val diagnosticCommand: String,
    val verificationCommand: String,
    val escalationNote: String,
    val escalationNoteFa: String
)

object HelpdeskData {
    val categories = listOf(
        "All" to "همه دسته‌ها",
        "Connectivity" to "اتصال و فیزیکی",
        "Switching & STP" to "سوئیچینگ و لوپ",
        "DHCP & IP" to "DHCP و آدرس‌دهی",
        "DNS" to "DNS و نام دامنه",
        "Active Directory" to "اکتیو دایرکتوری",
        "Virtualization" to "مجازی‌سازی و ESXi",
        "Firewall & Security" to "فایروال و امنیت",
        "VPN & Remote" to "VPN و دسترسی ریموت",
        "Database & ERP" to "دیتابیس و راهکاران",
        "VoIP & Telephony" to "ویپ و تلفن شبکه",
        "File & Print" to "اشتراک فایل و پرینت"
    )

    val allIssues = listOf(
        // 1. LAN Packet Loss & Latency
        HelpdeskIssue(
            id = "issue_lan_drop",
            title = "Local Network Packet Loss / High Latency",
            titleFa = "افت پکت و لتنسی شدید در شبکه محلی (LAN)",
            category = "Connectivity",
            categoryFa = "اتصال و فیزیکی",
            severity = IssueSeverity.HIGH,
            symptom = "Users experience intermittent disconnects, ERP slowness, and high ping latency to default gateway.",
            symptomFa = "قطع و وصل مکرر کاربران، کندی در سامانه راهکاران و پینگ بالای گیت‌وی پیش‌فرض.",
            rootCauses = listOf(
                "Duplex / Speed mismatch on access switch port (Half vs Full Duplex)",
                "Spanning Tree Protocol (STP) topology change recalculation loop",
                "Defective patch cable or keystone termination introducing CRC errors",
                "Broadcasting storm caused by unmanaged desktop mini-switch"
            ),
            rootCausesFa = listOf(
                "عدم تطابق Speed/Duplex در پورت سوئیچ (Half-Duplex به جای Full)",
                "لوپ در لایه دو و تغییرات مداوم توپولوژی STP",
                "خرابی پچ‌کورد یا کیستون نود شبکه و ایجاد خطاهای فریم CRC",
                "طوفان برودکست ناشی از هاب یا سوئیچ غیرمدیریتی متصل به شبکه"
            ),
            triageSteps = listOf(
                TriageStep(1, "Verify Gateway Ping", "تست پینگ به گیت‌وی", "Execute continuous ping to default gateway with 1472 byte MTU payload.", "پینگ مداوم به آدرس گیت‌وی با طول بسته استاندارد MTU."),
                TriageStep(2, "Inspect Switch Port CRC", "بررسی خطاهای CRC پورت", "Check for input errors, runts, and CRC frame drops on the interface.", "بررسی خطاهای CRC و Runts روی اینترفیس سوئیچ سیسکو."),
                TriageStep(3, "Isolate Broadcast Storm", "شناسایی پورت لوپ", "Check CPU utilization and interface broadcast rate thresholds.", "بررسی درصد مصرف CPU سوئیچ و نرخ بسته‌های برودکست.")
            ),
            diagnosticCommand = "ping 192.168.1.1 -n 50 -l 1400",
            verificationCommand = "show interfaces GigabitEthernet1/0/24 | include CRC|errors|duplex",
            escalationNote = "If CRC errors increase rapidly, re-terminate network keystone or patch into an alternate core switch port.",
            escalationNoteFa = "در صورت افزایش خطاهای CRC، سوکت کابل را مجدداً پانچ کرده یا پورت سوئیچ را تعویض نمایید."
        ),

        // 2. Broadcast Storm & STP Loops
        HelpdeskIssue(
            id = "issue_stp_loop",
            title = "L2 Broadcast Storm & STP Loop (Switch CPU 99%)",
            titleFa = "طوفان برودکست لایه ۲ و لوپ STP (اشغال ۹۹٪ پردازنده سوئیچ)",
            category = "Switching & STP",
            categoryFa = "سوئیچینگ و لوپ",
            severity = IssueSeverity.CRITICAL,
            symptom = "Network switches become unresponsive, link LEDs flash synchronously like strobe lights, all workstations drop off LAN.",
            symptomFa = "سوئیچ‌های شبکه از دسترس خارج شده، چراغ پورت‌ها همزمان و دیوانه‌وار چشمک می‌زنند و ارتباط تمامی سیستم‌ها قطع می‌شود.",
            rootCauses = listOf(
                "A patch cable looped back into two wall outlets connected to the same switch",
                "An unmanaged desktop 5-port mini-switch connected in a dual loop",
                "BPDU Guard disabled on access ports configured with PortFast"
            ),
            rootCausesFa = listOf(
                "اتصال تصادفی دو سر یک پچ‌کورد به دو پریز شبکه مجاور در یک اتاق",
                "وصل شدن یک هاب یا مینی‌سوئیچ رومیزی با دو کابل به سوئیچ اصلی",
                "غیرفعال بودن قابلیت امنیتی BPDU Guard روی پورت‌های PortFast"
            ),
            triageSteps = listOf(
                TriageStep(1, "Console Access & Identify Flapping MAC", "ورود با کنسول و یافتن MAC", "Connect via serial console and check for MAC flapping log entries.", "اتصال با کابل کنسول و بررسی لاگ‌های حرکت سریع مک‌آدرس (MAC flapping)."),
                TriageStep(2, "Enable Storm Control & BPDU Guard", "فعالسازی Storm Control", "Shut down the rogue access port identified by high packet rate counters.", "غیرفعال کردن موقت پورت خطاکار که نرخ بسته‌های ورودی نجومی دارد."),
                TriageStep(3, "Verify Root Bridge Stability", "بررسی پایداری Root Bridge", "Ensure the Core Switch remains the permanent STP Root Bridge.", "اطمینان از اینکه سوئیچ مرکزی Core به عنوان روت بریج ثابت باقی مانده است.")
            ),
            diagnosticCommand = "show mac address-table notification mac-move",
            verificationCommand = "show spanning-tree root | include Root",
            escalationNote = "Enforce 'spanning-tree portfast bpduguard default' globally to automatically err-disable looping ports.",
            escalationNoteFa = "دستور bpduguard default را به صورت سراسری فعال کنید تا به محض تشخیص لوپ، پورت خاطی خودکار خاموش شود."
        ),

        // 3. DHCP Scope Exhaustion & APIPA
        HelpdeskIssue(
            id = "issue_dhcp_exhaustion",
            title = "DHCP Scope Exhaustion (IP 169.254.x.x APIPA)",
            titleFa = "اتمام ظرفیت IP در سرور DHCP و دریافت آدرس APIPA",
            category = "DHCP & IP",
            categoryFa = "DHCP و آدرس‌دهی",
            severity = IssueSeverity.CRITICAL,
            symptom = "New client workstations get 169.254.x.x addresses with 'No Internet Access' yellow warning.",
            symptomFa = "کلاینت‌های جدید آدرس خودکار 169.254.x.x دریافت کرده و دسترسی به شبکه و اینترنت قطع می‌شود.",
            rootCauses = listOf(
                "Active DHCP lease pool has reached 100% capacity",
                "Lease duration is too long (8 days) for transient mobile and guest devices",
                "Rogue DHCP server on access VLAN distributing conflicting IP addresses",
                "DHCP Server service hung or blocked by firewall port 67/68 UDP"
            ),
            rootCausesFa = listOf(
                "تکمیل ۱۰۰ درصدی اسکوپ فعال DHCP سرور در دامین کنترلر",
                "طولانی بودن زمان Lease Time (مثلاً ۸ روز) برای گوشی‌ها و لپ‌تاپ‌های مهمان",
                "وجود یک مودم یا روتر خانگی در شبکه که به عنوان Rogue DHCP آی‌پی اشتباه می‌دهد",
                "متوقف شدن سرویس DHCP سرور یا مسدود شدن پورت 67/68 UDP"
            ),
            triageSteps = listOf(
                TriageStep(1, "Release & Renew Lease", "آزادسازی و درخواست مجدد", "Run ipconfig release/renew to test DHCP Discover response.", "اجرای دستور رلیز و رینیو برای سنجش پاسخگویی سرور."),
                TriageStep(2, "Shorten Lease Duration", "کاهش مدت‌زمان اجاره", "Reduce lease time to 8 hours for dynamic offices.", "کاهش Lease Time به ۸ ساعت در اسکوپ DHCP."),
                TriageStep(3, "Purge Inactive Leases", "حذف رکوردهای منقضی", "Reconcile DHCP database and clear inactive leases.", "یکپارچه‌سازی پایگاه داده DHCP و پاکسازی رکوردهای راکد.")
            ),
            diagnosticCommand = "ipconfig /release && ipconfig /renew",
            verificationCommand = "Get-DhcpServerv4ScopeStatistics -ComputerName srv-dc",
            escalationNote = "Expand subnet prefix from /24 to /23 or add a helper-address IP forwarder on the routing VLAN.",
            escalationNoteFa = "اسکوپ شبکه را از /24 به /23 ارتقا داده یا IP Helper Address را در VLAN روتر بررسی فرمایید."
        ),

        // 4. Rogue DHCP Server Collision
        HelpdeskIssue(
            id = "issue_rogue_dhcp",
            title = "Rogue DHCP Server Colliding on Access VLAN",
            titleFa = "تداخل سرور DHCP ناشناس (Rogue DHCP) و توزیع رنج آی‌پی اشتباه",
            category = "DHCP & IP",
            categoryFa = "DHCP و آدرس‌دهی",
            severity = IssueSeverity.HIGH,
            symptom = "Users receive 192.168.0.x or 192.168.1.1 gateway from a TP-Link/D-Link home router instead of enterprise IP.",
            symptomFa = "کاربران به جای رنج سازمانی، از یک روتر یا مودم خانگی رنج 192.168.0.x دریافت کرده و به سرورهای شرکت دسترسی ندارند.",
            rootCauses = listOf(
                "An employee plugged a personal Wi-Fi router LAN port directly into an enterprise wall jack",
                "DHCP Snooping disabled on access switch, allowing untrusted DHCP Offers"
            ),
            rootCausesFa = listOf(
                "اتصال پورت LAN یک اکسس‌پوینت یا روتر خانگی توسط پرسنل به پریز شبکه شرکت",
                "غیرفعال بودن قابلیت DHCP Snooping روی سوئیچ جهت فیلتر پکت‌های Offer غیرمجاز"
            ),
            triageSteps = listOf(
                TriageStep(1, "Locate Rogue Server IP", "یافتن آدرس گیت‌وی اشتباه", "Run ipconfig to find the rogue DHCP Server and Default Gateway IP.", "اجرای ipconfig /all و استخراج آدرس DHCP Server غیرمجاز."),
                TriageStep(2, "Lookup Rogue MAC Address", "یافتن مک‌آدرس با ARP", "Query ARP table for the rogue IP to extract its hardware MAC.", "اجرای arp -a و استخراج مک‌آدرس روتر خانگی متصل به شبکه."),
                TriageStep(3, "Trace and Shut Port", "یافتن پورت در سوئیچ", "Track the MAC on core switch address table and shut down the offending port.", "جستجوی مک‌آدرس در جدول سوئیچ و خاموش کردن فوری پورت مربوطه.")
            ),
            diagnosticCommand = "arp -a | findstr 192.168.0.1",
            verificationCommand = "show mac address-table address 00e0.4c68.0123",
            escalationNote = "Enable 'ip dhcp snooping' and set 'ip dhcp snooping trust' ONLY on core switch uplink trunk ports.",
            escalationNoteFa = "قابلیت DHCP Snooping را فعال کرده و تنها پورت ترانک متصل به سرور را به عنوان Trust معرفی کنید."
        ),

        // 5. Active Directory Account Lockout Storm
        HelpdeskIssue(
            id = "issue_ad_lockout",
            title = "Active Directory Account Lockout Storm",
            titleFa = "قفل مکرر حساب کاربری اکتیو دایرکتوری (Account Lockout)",
            category = "Active Directory",
            categoryFa = "اکتیو دایرکتوری",
            severity = IssueSeverity.HIGH,
            symptom = "Employee account locks out repeatedly every 5 minutes even after password reset.",
            symptomFa = "اکانت کاربر بعد از هر بار ریست پسورد ظرف چند دقیقه مجدداً قفل (Locked) می‌شود.",
            rootCauses = listOf(
                "Cached stale credentials in Windows Credential Manager on a secondary PC",
                "A mapped network drive or scheduled task running under old password",
                "Mobile phone Wi-Fi (802.1X) or Outlook app hammering DC with expired token"
            ),
            rootCausesFa = listOf(
                "رمز عبور قدیمی در Credential Manager ویندوز کلاینت یا سیستم دیگر کاربر",
                "درایو شیر یا Task Scheduler با پسورد قدیمی ذخیره‌شده",
                "تلاش مداوم گوشی همراه کاربر با پسورد قبلی برای اتصال به Wi-Fi سازمانی"
            ),
            triageSteps = listOf(
                TriageStep(1, "Unlock Account & Query DC", "آنلاک حساب و استعلام لاگ", "Identify caller computer from Security Event ID 4740 on PDC.", "استخراج کامپیوتر مبدا از طریق لاگ Event 4740 در دامین کنترلر."),
                TriageStep(2, "Clear Credential Cache", "پاکسازی کریدنشال منیجر", "Remove old enterprise credentials from Windows Vault.", "حذف تمامی اطلاعات ذخیره‌شده در Windows Credential Manager."),
                TriageStep(3, "Disconnect Wi-Fi Mobile", "قطع موقت وای‌فای موبایل", "Turn off client mobile device Wi-Fi while verifying password update.", "خاموش کردن موقت وای‌فای گوشی تا تکمیل احراز هویت.")
            ),
            diagnosticCommand = "rundll32.exe keymgr.dll,KRShowKeyMgr",
            verificationCommand = "Get-WinEvent -FilterHashtable @{LogName='Security';Id=4740} -MaxEvents 5",
            escalationNote = "Inspect Event 4740 'Caller Computer Name' field to isolate the exact machine triggering bad passwords.",
            escalationNoteFa = "در لاگ 4740 فیلد Caller Computer Name نام دقیق دستگاهی که پسورد اشتباه ارسال می‌کند را مشخص می‌نماید."
        ),

        // 6. Kerberos Clock Skew / NTP Time Drift
        HelpdeskIssue(
            id = "issue_kerberos_time",
            title = "Kerberos Clock Skew & Authentication Failure (Time Drift > 5 Min)",
            titleFa = "خطای احراز هویت کربروس ناشی از اختلاف ساعت کلاینت با دامین (Clock Skew)",
            category = "Active Directory",
            categoryFa = "اکتیو دایرکتوری",
            severity = IssueSeverity.HIGH,
            symptom = "Users cannot log into domain, receive 'The system cannot log you on due to time difference' or RPC errors.",
            symptomFa = "کاربران نمی‌توانند به دامین لاگین کنند یا در دسترسی به منابع با خطای اختلاف ساعت سیستم مواجه می‌شوند.",
            rootCauses = listOf(
                "Client CMOS battery failure or time zone mismatch causing > 5 minutes drift",
                "Domain Controller VM time sync with VMware ESXi host overriding NTP hierarchy",
                "Windows Time service (w32time) stopped or misconfigured on client"
            ),
            rootCausesFa = listOf(
                "خالی شدن باتری مادربورد کلاینت یا تغییر تایم‌زون و ایجاد اختلاف بیش از ۵ دقیقه",
                "همگام‌سازی ساعت ماشین مجازی دامین کنترلر با هایپروایزر ESXi به جای منبع NTP",
                "متوقف شدن سرویس Windows Time در ویندوز کلاینت"
            ),
            triageSteps = listOf(
                TriageStep(1, "Check Time Offset", "سنجش اختلاف زمان", "Compare client local time with domain controller NTP output.", "مقایسه ساعت دقیق کلاینت با ساعت سرور دامین کنترلر."),
                TriageStep(2, "Resync Windows Time", "سینک اجباری ساعت", "Force resynchronization with domain hierarchy PDC emulator.", "اجرای دستور resync برای دریافت فوری ساعت از سرور PDC."),
                TriageStep(3, "Disable ESXi Host Time Sync", "غیرفعالسازی سینک با هاست", "Uncheck 'Synchronize guest time with host' in VM Options for Domain Controllers.", "غیرفعال کردن تیک Time Sync با هاست در تنظیمات ماشین مجازی دامین کنترلر.")
            ),
            diagnosticCommand = "w32tm /query /status",
            verificationCommand = "w32tm /resync /rediscover",
            escalationNote = "Kerberos ticket granting protocol allows a maximum 5-minute skew window before rejecting tickets.",
            escalationNoteFa = "پروتکل امنیتی Kerberos حداکثر ۵ دقیقه اختلاف ساعت را مجاز می‌داند و فراتر از آن تیکت لاگین صادر نمی‌کند."
        ),

        // 7. Internal DNS Host Resolution Failure
        HelpdeskIssue(
            id = "issue_dns_resolution",
            title = "Internal DNS Resolution Failure / Host Unreachable",
            titleFa = "اختلال در رزولو نام‌های داخلی DNS و عدم دسترسی به سرورها",
            category = "DNS",
            categoryFa = "DNS و نام دامنه",
            severity = IssueSeverity.HIGH,
            symptom = "Users can ping servers by IP (e.g. 192.168.1.10) but fail to access using FQDN (e.g. srv-dc.jahan.local).",
            symptomFa = "دسترسی با IP سرور امکان‌پذیر است اما اتصال با نام سرور (FQDN) با خطای پیدا نشدن آدرس مواجه می‌شود.",
            rootCauses = listOf(
                "Local workstation DNS cache contains stale or corrupted records",
                "Client network adapter is using public DNS (8.8.8.8) instead of enterprise DC",
                "Forward lookup zone record missing or scavenged in DNS server"
            ),
            rootCausesFa = listOf(
                "خرابی یا رکورد منقضی در کش DNS کلاینت",
                "تنظیم بودن DNS پابلیک به جای DNS سرور داخلی شبکه",
                "حذف رکورد سرور در Forward Lookup Zone اکتیو دایرکتوری"
            ),
            triageSteps = listOf(
                TriageStep(1, "Flush DNS Cache", "تخلیه کش DNS", "Purge DNS cache and re-register network adapters.", "دستور تخلیه کش DNS و ثبت مجدد کارت شبکه."),
                TriageStep(2, "Query Internal DC Directly", "استعلام مستقیم از سرور", "Perform nslookup specifying internal DC IP address.", "اجرای nslookup با مشخص کردن IP دامین کنترلر داخلی."),
                TriageStep(3, "Check DNS Suffix", "بررسی پسوند دامین", "Ensure Primary DNS Suffix matches enterprise Active Directory.", "اطمینان از تطابق پسوند دامین در تنظیمات شبکه.")
            ),
            diagnosticCommand = "ipconfig /flushdns && ipconfig /registerdns",
            verificationCommand = "nslookup srv-dc.jahan.local 192.168.1.11",
            escalationNote = "Ensure primary network adapter DNS 1 points to local DC and DNS 2 points to secondary DC (never public DNS).",
            escalationNoteFa = "دی‌ان‌اس اول کلاینت باید دامین کنترلر ۱ و دی‌ان‌اس دوم دامین کنترلر ۲ باشد؛ هرگز نباید 8.8.8.8 تنظیم شود."
        ),

        // 8. VMware ESXi Host Disconnected in vCenter
        HelpdeskIssue(
            id = "issue_esxi_host_down",
            title = "VMware ESXi Host Disconnected / Not Responding in vCenter",
            titleFa = "عدم پاسخگویی و Disconnect شدن هاست ESXi در vCenter",
            category = "Virtualization",
            categoryFa = "مجازی‌سازی و ESXi",
            severity = IssueSeverity.CRITICAL,
            symptom = "Host 50.15 or 1.17 shows 'Not Responding' in vCenter management portal.",
            symptomFa = "هاست 50.15 در اردکان یا 1.17 در جهان در پنل vCenter به وضعیت Not Responding درآمده است.",
            rootCauses = listOf(
                "Management agent (hostd / vpxa) hung on physical ESXi hypervisor",
                "Management network IP conflict or physical switch uplink failure",
                "Host storage APD (All Paths Down) freezing management daemons"
            ),
            rootCausesFa = listOf(
                "هنگ کردن سرویس‌های مدیریتی Hostd یا VPXA روی هایپروایزر فیزیکی",
                "قطعی آپ‌لینک سوئیچ شبکه منجمنت هاست",
                "فریز شدن هاست به دلیل قطع اتصال استوریج (All Paths Down)"
            ),
            triageSteps = listOf(
                TriageStep(1, "Test Direct Host Web UI", "ورود مستقیم به هاست", "Navigate directly to ESXi management IP via HTTPS bypass vCenter.", "ورود مستقیم با مرورگر به IP هاست جهت تفکیک سلامت از vCenter."),
                TriageStep(2, "Restart Management Agents", "ریستارت ایجنت‌های منجمنت", "Access DCUI via KVM/iLO and restart management network & agents.", "ورود از طریق کنسول iLO و ریستارت ایجنت‌های Hostd و VPXA."),
                TriageStep(3, "Verify vCenter Ping", "بررسی پینگ سرور vCenter", "Ensure TCP port 902 and 443 are reachable between host and vCenter.", "بررسی باز بودن پورت‌های ۹۰۲ و ۴۴۳ بین هاست و vCenter سرور.")
            ),
            diagnosticCommand = "/etc/init.d/hostd restart && /etc/init.d/vpxa restart",
            verificationCommand = "nc -zv 192.168.50.20 902",
            escalationNote = "If host is unresponsive via SSH, access via HPE iLO / Dell iDRAC console to restart agents safely.",
            escalationNoteFa = "در صورت بسته بودن SSH، از طریق کنسول سخت‌افزاری iLO وارد شده و از منوی DCUI ایجنت‌ها را ریستارت کنید."
        ),

        // 9. ESXi All Paths Down (APD) & Storage Freeze
        HelpdeskIssue(
            id = "issue_esxi_apd",
            title = "VMware vSphere All Paths Down (APD) Storage Incident",
            titleFa = "قطعی کامل مسیرهای استوریج (APD) و قفل شدن ماشین‌های مجازی",
            category = "Virtualization",
            categoryFa = "مجازی‌سازی و ESXi",
            severity = IssueSeverity.CRITICAL,
            symptom = "VMs pause execution, datastores appear inactive (grayed out), I/O operations hang indefinitely.",
            symptomFa = "ماشین‌های مجازی فریز شده، دیتااستورها در کنسول طوسی شده و عملیات خواندن/نوشتن متوقف می‌شود.",
            rootCauses = listOf(
                "SAN Switch Fiber Channel zoning drop or iSCSI storage portal network partition",
                "Storage controller failover failure during firmware update",
                "MTU mismatch on iSCSI VMkernel port (Jumbo Frames 9000 vs 1500 on switch)"
            ),
            rootCausesFa = listOf(
                "قطع شدن ارتباط فابریک سوئیچ SAN یا قطع ارتباط شبکه اختصاصی iSCSI",
                "اختلال در Failover کنترلر دوم سن‌استوریج در حین نگهداری",
                "عدم تطابق Jumbo Frame (MTU 9000) روی سوئیچ استوریج و پورت VMkernel"
            ),
            triageSteps = listOf(
                TriageStep(1, "Ping Storage Portal with Jumbo Frames", "تست پینگ سن با MTU 9000", "Execute vmkping with DF bit set to 9000 to verify iSCSI path.", "دستور vmkping با پکت ۹۰۰۰ بدون فرگمنت به IP استوریج."),
                TriageStep(2, "Rescan Storage Adapters", "اسکن مجدد HBA", "Perform storage adapter rescan to recover cleared paths.", "اجرای Rescan روی کارت‌های HBA برای بازیافت مسیرها."),
                TriageStep(3, "Check VMkernel Logs", "بررسی لاگ هسته", "Inspect /var/log/vmkernel.log for ScsiDevice and NMP path failover alerts.", "بررسی لاگ vmkernel برای مشاهده پیام‌های مسیرهای دیسک.")
            ),
            diagnosticCommand = "vmkping -d -s 8972 192.168.100.10",
            verificationCommand = "esxcli storage core path list --state=active",
            escalationNote = "Never hard-reboot an APD host while storage is recovering to prevent VMFS lock metadata corruption.",
            escalationNoteFa = "در وضعیت APD هرگز هاست را به صورت فیزیکی ریستارت سخت‌افزاری نکنید تا دیتابیس قفل‌های VMFS آسیب نبیند."
        ),

        // 10. FortiGate & Firewall Traffic Drop
        HelpdeskIssue(
            id = "issue_firewall_drop",
            title = "FortiGate / NGFW Policy Drop & Asymmetric Routing Timeout",
            titleFa = "مسدود شدن ترافیک توسط فایروال فورتی‌گیت و خطای روتینگ نامتقارن",
            category = "Firewall & Security",
            categoryFa = "فایروال و امنیت",
            severity = IssueSeverity.HIGH,
            symptom = "Branch office workstations reach Internet but specific internal ERP server traffic drops silently.",
            symptomFa = "اینترنت کاربران وصل است اما بسته‌های ارسالی به سرور راهکاران یا پورت‌های خاص توسط فایروال Drop می‌شوند.",
            rootCauses = listOf(
                "Missing firewall policy allowing traffic between source and destination interface zones",
                "Asymmetric routing: outbound packet exits via WAN1, return packet enters via WAN2 (anti-spoofing drop)",
                "IPS or Application Control profile triggering false-positive reset packet"
            ),
            rootCausesFa = listOf(
                "نبود رول مجاز در فایروال بین زون مبدا و مقصد",
                "روتینگ نامتقارن: بسته از یک اینترفیس خارج شده ولی پاسخ از اینترفیس دیگر برمی‌گردد",
                "مسدود شدن ترافیک توسط موتور IPS یا Application Control به اشتباه"
            ),
            triageSteps = listOf(
                TriageStep(1, "Run Realtime CLI Packet Trace", "رهگیری زنده بسته در کنسول", "Use FortiGate diag debug flow trace to see exact drop reason.", "اجرای دستور debug flow در کنسول فورتی‌گیت جهت مشاهده علت دقیق Drop."),
                TriageStep(2, "Verify Matching Policy ID", "یافتن آیدی پالیسی", "Check policy ID matching the session and verify source NAT settings.", "بررسی رول فایروال که بسته به آن اصابت می‌کند."),
                TriageStep(3, "Temporarily Disable Asymmetric Inspection", "تست روتینگ نامتقارن", "Enable asymmetric routing mode temporarily to test theory.", "فعال کردن موقت asymmetric-routing جهت تست بازگشت پکت‌ها.")
            ),
            diagnosticCommand = "diagnose debug flow filter saddr 192.168.50.12 && diagnose debug flow show console enable && diagnose debug flow trace start 100",
            verificationCommand = "diagnose sys session filter dport 7300 && diagnose sys session list",
            escalationNote = "Always ensure symmetric return routing or configure source NAT on the egress interface.",
            escalationNoteFa = "در صورت وجود چند مسیر خروجی، روت‌های بازگشت را بررسی کرده یا روی پالیسی NAT را فعال کنید."
        ),

        // 11. IPsec VPN Tunnel Down / Phase 1 Timeout
        HelpdeskIssue(
            id = "issue_vpn_disconnect",
            title = "Site-to-Site IPsec VPN Tunnel Down (Phase 1 / Phase 2 Timeout)",
            titleFa = "قطع تونل ارتباطی شعبات IPsec VPN (خطای فاز ۱ و فاز ۲)",
            category = "VPN & Remote",
            categoryFa = "VPN و دسترسی ریموت",
            severity = IssueSeverity.CRITICAL,
            symptom = "Ardakan - Jahan site tunnel drops, Phase 1 negotiation times out, no traffic crosses inter-site links.",
            symptomFa = "تونل ارتباطی بین شعبات قطع شده و احراز هویت IKE Phase 1 یا Phase 2 با خطای تایم‌اوت مواجه می‌شود.",
            rootCauses = listOf(
                "ISP WAN packet loss or MTU fragmentation breaking large IKE key exchanges",
                "IKE Phase 1 Pre-Shared Key (PSK) or encryption proposal mismatch",
                "UDP port 500 / 4500 blocked by upstream telecom provider"
            ),
            rootCausesFa = listOf(
                "افت پکت در لینک مخابرات یا فرگمنت شدن بسته‌ها به علت عدم تنظیم MTU",
                "تغییر یا عدم تطابق پسورد مشترک (PSK) یا الگوریتم رمزنگاری در دو طرف",
                "فیلتر شدن پورت‌های UDP 500 یا 4500 توسط سرویس‌دهنده اینترنت"
            ),
            triageSteps = listOf(
                TriageStep(1, "Test WAN Ping with DF Bit", "تست پینگ با بیت DF", "Ping remote public WAN IP with 1360 byte payload and DF bit set.", "پینگ به آی‌پی پابلیک طرف مقابل با بسته ۱۳۶۰ بایتی بدون اجازه فرگمنت."),
                TriageStep(2, "Inspect IKE Log on Router", "بررسی لاگ لاگین IKE", "Examine MikroTik/FortiGate IPSec debug logs for negotiation state.", "بررسی دقیق لاگ‌های IPsec در میکروتیک جهت کشف مرحله توقف مذاکره."),
                TriageStep(3, "Verify MSS Clamping", "اعمال رول MSS Clamping", "Ensure firewall Mangle rule clamps TCP MSS to 1360.", "اطمینان از وجود رول Change TCP MSS به ۱۳۶۰ در فایروال.")
            ),
            diagnosticCommand = "ping 192.168.50.20 -f -l 1360",
            verificationCommand = "/ip ipsec installed-sa print",
            escalationNote = "Set MSS clamping in firewall mangle to 1360 for all IPsec tunnel traffic to prevent fragmentation drops.",
            escalationNoteFa = "در میکروتیک رول Change MSS را در فایروال روی ۱۳۶۰ تنظیم کنید تا بسته‌های بزرگ در تونل فرگمنت نشوند."
        ),

        // 12. Rahkaran ERP & SQL Server Connection Timeout
        HelpdeskIssue(
            id = "issue_rahkaran_sql",
            title = "Rahkaran ERP Client Error: SQL Server Connection Timeout (Error 10060)",
            titleFa = "خطای عدم اتصال کلاینت‌های راهکاران به دیتابیس (SQL Server Timeout)",
            category = "Database & ERP",
            categoryFa = "دیتابیس و راهکاران",
            severity = IssueSeverity.HIGH,
            symptom = "Users open Rahkaran ERP client and receive 'Connection Timeout' or 'A network-related error occurred'.",
            symptomFa = "نرم‌افزار راهکاران روی سیستم کاربران باز نشده و خطای خطای شبکه یا تایم‌اوت ارتباط با دیتابیس می‌دهد.",
            rootCauses = listOf(
                "TCP port 1433 or SQL Server Browser UDP port 1434 blocked by Windows Firewall on DB server",
                "SQL Server service or SQL Browser service stopped on database host (e.g. 192.168.1.10)",
                "TCP/IP protocol disabled in SQL Server Configuration Manager",
                "Maximum client connections pool exhausted on SQL Server instance"
            ),
            rootCausesFa = listOf(
                "مسدود شدن پورت TCP 1433 یا UDP 1434 در فایروال سرور دیتابیس (192.168.1.10)",
                "متوقف شدن سرویس MSSQLSERVER یا SQL Server Browser در ویندوز سرور",
                "غیرفعال بودن پروتکل TCP/IP در SQL Server Configuration Manager",
                "تکمیل سقف کانکشن‌های مجاز در پایگاه داده"
            ),
            triageSteps = listOf(
                TriageStep(1, "Test Port 1433 via PowerShell", "تست پورت ۱۴۳۳ با پاورشل", "Execute Test-NetConnection to database server IP on port 1433.", "تست باز بودن پورت ۱۴۳۳ سرور دیتابیس با Test-NetConnection."),
                TriageStep(2, "Check SQL Server Services", "بررسی سرویس‌های SQL", "Ensure MSSQLSERVER and SQLBrowser services are running.", "بررسی وضعیت Running بودن سرویس‌های SQL Server در سرور دیتابیس."),
                TriageStep(3, "Verify TCP/IP Protocol Enabled", "بررسی فعال بودن پروتکل شبکه", "Open SQL Configuration Manager and verify TCP/IP is enabled on IPAll.", "اطمینان از Enable بودن پروتکل TCP/IP و تنظیم پورت روی ۱۴۳۳.")
            ),
            diagnosticCommand = "Test-NetConnection -ComputerName 192.168.1.10 -Port 1433",
            verificationCommand = "Get-Service -Name MSSQLSERVER, SQLBrowser",
            escalationNote = "Create an inbound rule for TCP 1433 and UDP 1434 in Windows Defender Firewall with Advanced Security.",
            escalationNoteFa = "در فایروال ویندوز سرور دیتابیس، دو رول ورودی برای TCP 1433 و UDP 1434 ایجاد و فعال نمایید."
        ),

        // 13. VoIP One-Way Audio & SIP ALG Drop
        HelpdeskIssue(
            id = "issue_voip_audio",
            title = "VoIP / SIP Phone One-Way Audio & Dropped RTP Streams",
            titleFa = "مشکل صدای یک‌طرفه در تلفن‌های تحت شبکه (VoIP / SIP)",
            category = "VoIP & Telephony",
            categoryFa = "ویپ و تلفن شبکه",
            severity = IssueSeverity.MEDIUM,
            symptom = "Caller can hear the recipient, but recipient cannot hear caller, or calls drop after exactly 30 seconds.",
            symptomFa = "تماس‌گیرنده صدای طرف مقابل را می‌شنود اما صدای خودش نمی‌رود، یا تماس دقیقاً بعد از ۳۰ ثانیه قطع می‌شود.",
            rootCauses = listOf(
                "SIP ALG (Application Layer Gateway) enabled on perimeter router corrupting SIP headers",
                "UDP RTP port range (10000 - 20000) blocked in firewall between PBX and phone VLAN",
                "NAT traversal issue with incorrect external IP configured in PBX Asterisk / Issabel"
            ),
            rootCausesFa = listOf(
                "فعال بودن قابلیت مخرب SIP ALG در روتر یا مودم که هدرهای SDP را دستکاری می‌کند",
                "بسته بودن رنج پورت‌های صوتی RTP (معمولاً 10000 تا 20000) در فایروال بین VLANها",
                "تنظیم نبودن IP استاتیک یا اکسترنال در تنظیمات NAT مرکز تلفن ایزابل / استریسک"
            ),
            triageSteps = listOf(
                TriageStep(1, "Disable SIP ALG on Router", "غیرفعال کردن SIP ALG", "Disable SIP helper/ALG in MikroTik/FortiGate firewall settings.", "غیرفعال کردن سرویس کمکی SIP ALG در روتر و فایروال."),
                TriageStep(2, "Open UDP RTP Range", "باز کردن رنج RTP", "Ensure firewall permits UDP 10000-20000 bi-directionally between PBX and phones.", "ایجاد رول مجاز برای پورت‌های UDP رنج 10000 تا 20000."),
                TriageStep(3, "Verify Asterisk NAT Setting", "بررسی تنظیمات NAT در سانترال", "Confirm externip and localnet parameters in sip.conf match subnets.", "بررسی پارامترهای Localnet و Externip در تنظیمات مرکز تلفن.")
            ),
            diagnosticCommand = "/ip firewall service-port set sip disabled",
            verificationCommand = "asterisk -rx 'sip show peers' | grep -i OK",
            escalationNote = "Always disable SIP ALG on MikroTik (/ip firewall service-port disable sip) and FortiGate (set sip-helper disable).",
            escalationNoteFa = "در تمامی تجهیزات لبه شبکه قابلیت SIP ALG را غیرفعال کنید؛ این قابلیت عامل شماره یک صدای یک‌طرفه است."
        ),

        // 14. Windows SMB File Share Error 0x80070035
        HelpdeskIssue(
            id = "issue_smb_share",
            title = "Windows File Share 'Network Path Not Found' (Error 0x80070035)",
            titleFa = "خطای عدم دسترسی به پوشه اشتراکی شبکه (Error 0x80070035)",
            category = "File & Print",
            categoryFa = "اشتراک فایل و پرینت",
            severity = IssueSeverity.MEDIUM,
            symptom = "Workstations receive 'The network path was not found' when opening \\\\srv-share or \\\\192.168.50.14.",
            symptomFa = "هنگام باز کردن پوشه اشتراکی سرور با پیام خطا 0x80070035 The network path was not found مواجه می‌شوند.",
            rootCauses = listOf(
                "TCP port 445 (SMB) blocked by endpoint security or third-party antivirus firewall",
                "NetBIOS over TCP/IP disabled on network adapter settings",
                "Workstation Network Profile set to 'Public' instead of 'Private' / DomainAuthenticated",
                "SMB 1.0 disabled while attempting connection to legacy NAS storage appliance"
            ),
            rootCausesFa = listOf(
                "مسدود شدن پورت TCP 445 توسط آنتی‌ویروس یا فایروال کلاینت",
                "غیرفعال بودن NetBIOS over TCP/IP در تنظیمات کارت شبکه ویندوز",
                "قرار گرفتن پروفایل شبکه کلاینت روی Public به جای Domain Authenticated",
                "غیرفعال بودن پروتکل SMB1 در هنگام اتصال به استوریج‌های بسیار قدیمی"
            ),
            triageSteps = listOf(
                TriageStep(1, "Check Port 445 Connectivity", "تست پورت ۴۴۵", "Execute Test-NetConnection to file server on port 445.", "بررسی برقراری سوکت TCP پورت ۴۴۵ به سمت سرور فایل."),
                TriageStep(2, "Set Network Profile to Private", "تنظیم پروفایل شبکه", "Switch Windows network category to DomainAuthenticated using PowerShell.", "تغییر پروفایل کارت شبکه از پابلیک به دامین با پاورشل."),
                TriageStep(3, "Enable NetBIOS over TCP/IP", "فعالسازی NetBIOS", "Ensure NetBIOS over TCP/IP is enabled in advanced IPv4 adapter properties.", "فعال کردن NetBIOS over TCP/IP در تنظیمات WINS کارت شبکه.")
            ),
            diagnosticCommand = "Test-NetConnection -ComputerName 192.168.50.14 -Port 445",
            verificationCommand = "Set-NetConnectionProfile -InterfaceAlias 'Ethernet' -NetworkCategory DomainAuthenticated",
            escalationNote = "Verify TCP Port 445 and ensure the 'Server' and 'Workstation' Windows services are Running.",
            escalationNoteFa = "از باز بودن پورت ۴۴۵ و فعال بودن سرویس‌های ویندوزی LanmanServer و LanmanWorkstation اطمینان حاصل کنید."
        ),

        // 15. Print Spooler Crash (Error 0x0000011b)
        HelpdeskIssue(
            id = "issue_print_spooler",
            title = "Network Printer Connection Error (0x0000011b) & Spooler Crash",
            titleFa = "خطای اتصال به پرینتر شبکه (0x0000011b) و کرش سرویس Print Spooler",
            category = "File & Print",
            categoryFa = "اشتراک فایل و پرینت",
            severity = IssueSeverity.MEDIUM,
            symptom = "Users cannot connect to shared network printer or receive error 0x0000011b after Windows updates.",
            symptomFa = "کاربران هنگام پرینت با خطای 0x0000011b مواجه می‌شوند یا پرینت در صف می‌ماند و سرویس اسپولر متوقف می‌شود.",
            rootCauses = listOf(
                "Windows security patch enforcing strict RPC authentication level for Print Spooler",
                "Corrupted third-party printer driver crashing spoolsv.exe process",
                "Locked temporary files in C:\\Windows\\System32\\spool\\PRINTERS directory"
            ),
            rootCausesFa = listOf(
                "آپدیت‌های امنیتی مایکروسافت که احراز هویت سخت‌گیرانه RPC برای پرینترها الزامی کرده است",
                "خرابی درایور پرینتر که باعث کرش کردن مکرر سرویس spoolsv.exe می‌شود",
                "قفل شدن فایل‌های موقت در پوشه صف پرینت ویندوز"
            ),
            triageSteps = listOf(
                TriageStep(1, "Purge Print Queue Folder", "پاکسازی صف پرینت", "Stop spooler, delete files in spool\\PRINTERS, and restart spooler.", "متوقف کردن اسپولر، حذف فایل‌های صف در پوشه PRINTERS و استارت مجدد."),
                TriageStep(2, "Set RPC Auth Registry Key", "تنظیم کلید رجیستری RPC", "Configure RpcAuthnLevelPrivacyEnabled = 0 on print server.", "ایجاد کلید RpcAuthnLevelPrivacyEnabled برابر 0 در پرینت سرور."),
                TriageStep(3, "Isolate Printer Drivers", "جداسازی درایور پرینتر", "Set Driver Isolation mode to 'Isolated' in Print Management.", "تنظیم ایزولاسیون درایور در Print Management تا کرش درایور کل سرویس را نخواباند.")
            ),
            diagnosticCommand = "reg add HKLM\\SYSTEM\\CurrentControlSet\\Control\\Print /v RpcAuthnLevelPrivacyEnabled /t REG_DWORD /d 0 /f",
            verificationCommand = "Restart-Service spooler -Force",
            escalationNote = "Always set Driver Isolation to 'Isolated' in print management so a single bad driver won't take down the server.",
            escalationNoteFa = "در پرینت سرور قابلیت Driver Isolation را فعال کنید تا خطای یک درایور باعث از کار افتادن کل پرینترها نشود."
        ),

        // 16. Enterprise Wi-Fi 802.1X RADIUS Authentication Failure
        HelpdeskIssue(
            id = "issue_wifi_radius",
            title = "Enterprise Wi-Fi 802.1X Authentication Failure (NPS Certificate)",
            titleFa = "عدم اتصال به وای‌فای سازمانی 802.1X و انقضای سرتیفیکیت سرور NPS",
            category = "Connectivity",
            categoryFa = "اتصال و فیزیکی",
            severity = IssueSeverity.HIGH,
            symptom = "Laptops and phones fail to authenticate to enterprise corporate Wi-Fi SSID, prompting 'Can't connect to this network'.",
            symptomFa = "لپ‌تاپ‌ها به وای‌فای سازمانی متصل نشده و مدام درخواست مجدد نام کاربری و پسورد دامین می‌کنند.",
            rootCauses = listOf(
                "Server certificate on Windows Network Policy Server (NPS) has expired",
                "Client workstation lacks the Active Directory root CA trusted certificate",
                "NPS service stopped or UDP port 1812/1813 blocked between Access Point and NPS"
            ),
            rootCausesFa = listOf(
                "منقضی شدن سرتیفیکیت دیجیتال سرور NPS رادیوس",
                "نبود سرتیفیکیت Root CA سازمانی در مخزن Trusted Root کلاینت",
                "مسدود شدن پورت UDP 1812 بین اکسس‌پوینت‌ها و سرور NPS"
            ),
            triageSteps = listOf(
                TriageStep(1, "Check NPS Event Log", "بررسی لاگ سرور NPS", "Inspect Event ID 6273 (Audit Failure) in Windows System log on NPS.", "بررسی لاگ Event 6273 در سرور NPS جهت کشف علت رد درخواست احراز هویت."),
                TriageStep(2, "Renew NPS TLS Certificate", "تمدید سرتیفیکیت سرور", "Bind active enterprise CA certificate to NPS Protected EAP policy.", "اتصال سرتیفیکیت معتبر جدید به پالیسی PEAP در کنسول NPS."),
                TriageStep(3, "Verify UDP 1812 Reachability", "تست پورت ۱۸۱۲ رادیوس", "Ensure UDP ports 1812 (Auth) and 1813 (Acct) are reachable from AP.", "تست برقراری ارتباط UDP 1812 بین کنترلر وای‌فای و سرور.")
            ),
            diagnosticCommand = "Get-WinEvent -FilterHashtable @{LogName='System';ProviderName='NPS';Id=6273} -MaxEvents 5",
            verificationCommand = "Test-NetConnection -ComputerName 192.168.1.11 -Port 1812",
            escalationNote = "Renew the Computer certificate on the NPS server using MMC Certificates snap-in and restart IAS service.",
            escalationNoteFa = "در کنسول MMC سرتیفیکیت سرور NPS را تمدید کرده و پالیسی شبکه را با سرتیفیکیت جدید به‌روزرسانی کنید."
        ),

        // 17. Suspected Ransomware Activity & Emergency Isolation
        HelpdeskIssue(
            id = "issue_ransomware_triage",
            title = "NOC Emergency Incident: Suspected Ransomware Burst Activity",
            titleFa = "تریاژ بحران امنیت NOC: شناسایی فعالیت باج‌افزار و قرنطینه فوری",
            category = "Firewall & Security",
            categoryFa = "فایروال و امنیت",
            severity = IssueSeverity.CRITICAL,
            symptom = "Unusual surge in SMB write operations on file server, files rapidly renaming with extension like .locked or .crypted.",
            symptomFa = "افزایش ناگهانی و شدید عملیات دیسک روی فایل سرور و تغییر پسوند سریع فایل‌ها به فرمت‌های ناشناس.",
            rootCauses = listOf(
                "Compromised user workstation running ransomware encrypting mapped network shares",
                "Privilege escalation via compromised local admin account"
            ),
            rootCausesFa = listOf(
                "آلوده شدن سیستم یکی از پرسنل به باج‌افزار و آغاز رمزنگاری فایل‌های اشتراکی شبکه",
                "نفوذ مهاجم و دسترسی به درایوهای شیر سازمانی با دسترسی کاربر"
            ),
            triageSteps = listOf(
                TriageStep(1, "Immediately Stop Server Service", "قطع فوری سرویس شیرینگ", "Execute 'net stop server' immediately on file server to halt SMB encryption.", "اجرای فوری دستور net stop server روی سرور برای بستن بلادرنگ درایوهای اشتراکی."),
                TriageStep(2, "Identify Infecting Client IP", "یافتن سیستم آلوده", "Inspect open SMB sessions with 'net session' to see which IP has open handles.", "بررسی نشست‌های فعال با net session و یافتن IP کلاینتی که صدها فایل باز دارد."),
                TriageStep(3, "Isolate Infected Switch Port", "ایزوله کردن پورت کلاینت", "Shut down the access switch port of the infected machine immediately.", "خاموش کردن فوری پورت سوئیچ کلاینت آلوده و کشیدن کابل شبکه فیزیکی.")
            ),
            diagnosticCommand = "net session | findstr /V \"0.0.0.0\"",
            verificationCommand = "net stop server /y",
            escalationNote = "Do NOT restart the infected workstation; preserve RAM state for digital forensics and isolate switch port instantly.",
            escalationNoteFa = "سیستم آلوده را ریستارت نکنید تا حافظه رم برای تحلیل جرم‌شناسی حفظ شود؛ پورت شبکه را بلادرنگ خاموش کنید."
        ),

        // 18. SSL/TLS Certificate Untrusted (NET::ERR_CERT_AUTHORITY_INVALID)
        HelpdeskIssue(
            id = "issue_ssl_cert",
            title = "Internal Web Portal SSL Untrusted (NET::ERR_CERT_AUTHORITY_INVALID)",
            titleFa = "خطای عدم اعتماد به سرتیفیکیت SSL سامانه‌های داخلی در مرورگر",
            category = "DNS",
            categoryFa = "DNS و نام دامنه",
            severity = IssueSeverity.MEDIUM,
            symptom = "Browsers display red security warning 'Your connection is not private' when accessing internal web portals.",
            symptomFa = "هنگام باز کردن پرتال‌های داخلی شرکت، مرورگر ارور قرمز Your connection is not private می‌دهد.",
            rootCauses = listOf(
                "Enterprise Private Root CA certificate is not installed in client Trusted Root Certification Authorities store",
                "Certificate Common Name (CN) or Subject Alternative Name (SAN) does not match the URL accessed by user",
                "Internal SSL certificate has passed its expiration date"
            ),
            rootCausesFa = listOf(
                "نبود سرتیفیکیت Root CA داخلی در مخزن Trusted Root ویندوز کلاینت",
                "عدم تطابق نام سرور (CN یا SAN) در سرتیفیکیت با آدرس وارد شده توسط کاربر",
                "منقضی شدن تاریخ اعتبار گواهی SSL سرور"
            ),
            triageSteps = listOf(
                TriageStep(1, "Inspect Certificate SAN", "بررسی هدر SAN گواهی", "Check certificate details in browser to verify SAN matches FQDN.", "بررسی تب Details گواهی در مرورگر و انطباق نام دامنه با فیلد SAN."),
                TriageStep(2, "Deploy Root CA via GPO", "توزیع سرتیفیکیت با GPO", "Distribute enterprise Root CA certificate globally through Active Directory Group Policy.", "انتشار خودکار گواهی روت به تمام کلاینت‌ها از طریق پالیسی اکتیو دایرکتوری."),
                TriageStep(3, "Verify Client Cert Store", "بررسی مخزن کلاینت", "Query certutil to verify Root CA presence in local computer store.", "استعلام با دستور certutil جهت اطمینان از قرار گرفتن گواهی در Trusted Root.")
            ),
            diagnosticCommand = "certutil -store Root | findstr /i \"EnterpriseCA\"",
            verificationCommand = "gpupdate /force",
            escalationNote = "Add the enterprise root certificate to Group Policy: Computer Config -> Windows Settings -> Security -> Public Key Policies -> Trusted Root.",
            escalationNoteFa = "گواهی Root CA را در مسیر Public Key Policies گروپ پالیسی قرار دهید تا خودکار روی تمام سیستم‌ها نصب شود."
        )
    )
}
