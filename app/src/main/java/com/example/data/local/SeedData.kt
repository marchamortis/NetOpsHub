package com.example.data.local

object SeedData {

    val sites = listOf(
        SiteEntity("site_ardakan", "Ardakan", "دیتا سنتر و زیرساخت اردکان"),
        SiteEntity("site_jahan", "Jahan", "مجتمع مرکزی جهان"),
        SiteEntity("site_sardkhaneh", "Sardkhaneh", "سردخانه رباط کریم"),
        SiteEntity("site_loleh", "Loleh Mazandaran", "لوله مازندران"),
        SiteEntity("site_sangan", "SANGAN", "ذوب فولاد سنگان"),
        SiteEntity("site_kansaran", "KANSARAN", "کانساران معدن نادر"),
        SiteEntity("site_touka", "TOUKA", "توکاسیر سپاهان"),
        SiteEntity("site_felezi", "Felezi", "صنایع فلزی کوشا نوین"),
        SiteEntity("site_jahan_factory", "JAHAN FACTORI", "کارخانه جهان"),
        SiteEntity("site_jkousha", "Jkousha", "کوشا نوین")
    )

    val vcenters = listOf(
        VcenterEntity("vc_ardakan", "ardakanV", "192.168.50.20", "Ardakan", "vCenter اردکان - هاست‌های 50.15, 50.16, 50.17, 50.24, 50.14"),
        VcenterEntity("vc_jahan", "Vcenter", "192.168.1.2", "Jahan", "vCenter جهان - هاست‌های 1.16, 1.17, 1.24, 1.130, 1.200, 1.204, 1.205, 1.208")
    )

    // Extracted directly from Copy of Vcenter.pdf (Page 1 - 4)
    val servers = listOf(
        ServerEntity("s_1", 1, "ardakanV", "50.15", "ardakanV", "192.168.50.20", "Vcenter", "-", "ok", "Ardakan", "VMware ESXi", "1405-04-27"),
        ServerEntity("s_2", 2, "ardakanV", "50.15", "Remote1", "192.168.50.12", "Remote Access", "Default", "ok", "Ardakan"),
        ServerEntity("s_3", 3, "ardakanV", "50.15", "ADC", "192.168.50.23", "اکتیو جانبی (Additional Domain Controller)", "Default", "ok", "Ardakan"),
        ServerEntity("s_4", 4, "ardakanV", "50.15", "Borhan Test", "192.168.50.21", "سرور تست برهان", "-", "ok", "Ardakan"),
        ServerEntity("s_5", 5, "ardakanV", "50.15", "Nama (Golkhaneh&Ardakan)", "192.168.50.25", "سرور نما", "-", "ok", "Ardakan"),
        ServerEntity("s_6", 6, "ardakanV", "50.15", "Share2", "192.168.50.7", "اشتراک فایل (Share)", "Default", "ok", "Ardakan"),
        ServerEntity("s_7", 7, "ardakanV", "50.15", "srv-borhanclone", "Needs Verification", "کلون برهان", "-", "off", "Ardakan"),
        ServerEntity("s_8", 8, "ardakanV", "50.15", "Electronic", "192.168.50.4", "اتوماسیون الکترونیک", "Default", "ok", "Ardakan"),
        ServerEntity("s_9", 9, "ardakanV", "50.15", "Rardakan", "192.168.50.8", "راهکاران اردکان", "7312", "ok", "Ardakan"),
        ServerEntity("s_10", 10, "ardakanV", "50.16", "DC", "192.168.50.22", "دامین کنترلر اکتیو (Domain Controller)", "Default", "ok", "Ardakan", "Windows Server", "1405-04-27"),
        ServerEntity("s_11", 11, "ardakanV", "50.16", "Ardakan-DOC", "192.168.50.19", "مستندات اردکان", "Default", "ok", "Ardakan"),
        ServerEntity("s_12", 12, "ardakanV", ".50.17", "Remote2", "192.168.50.13", "ریموت ۲", "Default", "ok", "Ardakan", "Windows", "1405-04-27"),
        ServerEntity("s_13", 13, "ardakanV", ".50.17", "SRV-Borhan", "192.168.50.6", "سرور برهان", "Default", "ok", "Ardakan"),
        ServerEntity("s_14", 14, "ardakanV", ".50.24", "CC", "192.168.50.27", "سرویس VPN کلاینت", "Default", "ok", "Ardakan", "Linux/Router", "1405-04-27"),
        ServerEntity("s_15", 15, "ardakanV", ".50.24", "Bitdefender", "192.168.50.110", "آنتی ویروس سازمانی", "-", "ok", "Ardakan", "Security Appliance"),
        ServerEntity("s_16", 16, "ardakanV", ".50.14", "SRV-SHARE", "192.168.50.14", "بدون ESXI (مستقل)", "Default", "ok", "Ardakan", "Physical Server", "1405-04-27"),
        ServerEntity("s_17", 17, "Jahan", "1.16", "srv-folad", "192.168.1.202", "راهکاران فولاد کوشا", "7303", "ok", "Jahan", "Windows Server", "1405-04-27"),
        ServerEntity("s_18", 18, "Jahan", "1.16", "srv-zob", "192.168.1.203", "راهکاران ذوب امید آریان", "7306", "ok", "Jahan"),
        ServerEntity("s_19", 19, "Jahan", "1.16", "KMC", "192.168.1.252", "سرور KMC", "-", "off", "Jahan"),
        ServerEntity("s_20", 20, "Jahan", "1.17", "Vcenter", "192.168.1.2", "vCenter مرکزی جهان", "-", "ok", "Jahan", "VMware ESXi", "1405-04-27"),
        ServerEntity("s_21", 21, "Jahan", "1.17", "GhaemR", "192.168.1.22", "راهکاران قائم پروفیل", "7300", "ok", "Jahan"),
        ServerEntity("s_22", 22, "Jahan", "1.17", "JahanR", "192.168.1.8", "راهکاران جهان پروفیل", "7302", "ok", "Jahan"),
        ServerEntity("s_23", 23, "Jahan", "1.17", "SRV-DC", "192.168.1.11", "دامین کنترلر اکتیو دایرکتوری", "-", "ok", "Jahan"),
        ServerEntity("s_24", 24, "Jahan", "1.24", "PWKARAWEB", "192.168.1.7", "سیستم حضور و غیاب کارا", "Default", "ok", "Jahan", "Windows Server", "1404-10-10"),
        ServerEntity("s_25", 25, "Jahan", "1.24", "Sina Pardazesh", "192.168.1.96", "دفترچه تلفن و سانترال", "-", "ok", "Jahan"),
        ServerEntity("s_26", 26, "Jahan", "1.24", "Bitdefender", "192.168.1.209", "آنتی ویروس جهان", "-", "ok", "Jahan"),
        ServerEntity("s_27", 27, "Jahan", "1.24", "JahanD", "192.168.1.10", "سرویس دیتابیس ADD", "-", "ok", "Jahan"),
        ServerEntity("s_28", 28, "Jahan", "1.24", "Rahkaran-Test", "192.168.1.21", "تست راهکاران", "-", "ok", "Jahan"),
        ServerEntity("s_29", 29, "Jahan", "1.130", "Chargon", "192.168.1.131", "اتوماسیون اداری چارگون", "33893", "ok", "Jahan", "Windows Server", "1404-10-10"),
        ServerEntity("s_30", 30, "Jahan", "1.130", "fax-chargon", "192.168.1.132", "سرور فکس چارگون", "33894", "ok", "Jahan"),
        ServerEntity("s_31", 31, "Jahan", "1.130", "Fax-chargon-2016", "Needs Verification", "سرور فکس قدیمی ۲۰۱۶", "-", "off", "Jahan"),
        ServerEntity("s_32", 32, "Jahan", "1.200", "SRV-BA", "192.168.1.4", "بایگانی اسناد", "Default", "ok", "Jahan", "Windows", "1404-10-10"),
        ServerEntity("s_33", 33, "Jahan", "1.200", "SRV-SH", "192.168.1.12", "اشتراک فایل (share)", "Default", "ok", "Jahan"),
        ServerEntity("s_34", 34, "Jahan", "1.204", "Central", "193.168.1.6", "Desktop Central Holding", "Default", "ok", "Jahan"),
        ServerEntity("s_35", 35, "Jahan", "1.204", "Mikrotik", "Needs Verification", "روتربورد میکروتیک مجازی", "-", "off", "Jahan"),
        ServerEntity("s_36", 36, "Jahan", ".1.205", "WBS", "192.168.1.110", "سامانه کنترل و ثبت وب", "Default", "ok", "Jahan"),
        ServerEntity("s_37", 37, "Jahan", ".1.205", "SRV-FELEZI", "192.168.1.32", "راهکاران صنایع فلزی", "7311", "ok", "Jahan"),
        ServerEntity("s_38", 38, "Jahan", ".1.205", "SRV-PARAND", "192.168.1.31", "راهکاران فورج پرند", "7310", "ok", "Jahan"),
        ServerEntity("s_39", 39, "Jahan", ".1.208", "Linux", "192.168.1.198", "سرور لینوکس (lan is off)", "-", "off", "Jahan"),
        ServerEntity("s_40", 40, "Jahan", ".1.208", "SQL-TICKETING", "192.168.1.18", "HelpDesk دیتابیس تیکتینگ", "-", "ok", "Jahan"),
        ServerEntity("s_41", 41, "Jahan", ".1.1", "نما", "192.168.1.9", "سامانه نمای جهان", "Default", "ok", "Jahan"),
        ServerEntity("s_42", 42, "Sardkhaneh", ".18.10", "DC", "192.168.18.15", "دامین کنترلر سردخانه", "7300", "ok", "Sardkhaneh"),
        ServerEntity("s_43", 43, "Sardkhaneh", ".18.10", "Rahkaran", "192.168.18.19", "راهکاران سردخانه رباط کریم", "7305", "ok", "Sardkhaneh"),
        ServerEntity("s_44", 44, "Sardkhaneh", ".18.10", "Bitdefender", "192.168.18.110", "آنتی ویروس سردخانه", "-", "ok", "Sardkhaneh"),
        ServerEntity("s_45", 45, "Sardkhaneh", ".18.10", "Share", "192.168.18.16", "اشتراک فایل سردخانه", "Default", "ok", "Sardkhaneh"),
        ServerEntity("s_46", 46, "Sardkhaneh", ".18.10", "PW Gate", "192.168.18.17", "گیت و ورود و خروج", "Default", "ok", "Sardkhaneh"),
        ServerEntity("s_47", 47, "Loleh Mazandaran", ".6.150", "LRahkaran", "192.168.6.155", "راهکاران لوله مازندران", "7308", "ok", "Loleh Mazandaran"),
        ServerEntity("s_48", 48, "Loleh Mazandaran", ".6.150", "gzva", "192.168.6.110", "آنتی ویروس لوله مازندران", "-", "ok", "Loleh Mazandaran"),
        ServerEntity("s_49", 49, "Loleh Mazandaran", ".6.150", "SRV-Share", "192.168.6.152", "اشتراک فایل لوله مازندران", "-", "ok", "Loleh Mazandaran"),
        ServerEntity("s_50", 50, "Loleh Mazandaran", ".6.150", "SRV-DC", "192.168.6.151", "دامین کنترلر مازندران", "-", "ok", "Loleh Mazandaran"),
        ServerEntity("s_51", 51, "Loleh Mazandaran", ".6.150", "DHCP", "192.168.6.153", "سرور DHCP شبکه محلی", "-", "ok", "Loleh Mazandaran"),
        ServerEntity("s_52", 52, "Loleh Mazandaran", ".6.150", "VPN", "192.168.6.154", "سرویس تونل و VPN", "-", "ok", "Loleh Mazandaran"),
        ServerEntity("s_53", 53, "SANGAN", ".9.100", "PDC", "192.168.9.101", "اکتیو دایرکتوری سنگان", "52859", "ok", "SANGAN"),
        ServerEntity("s_54", 54, "SANGAN", ".9.100", "RSANGAN", "192.168.9.105", "راهکاران سنگان", "7309", "ok", "SANGAN"),
        ServerEntity("s_55", 55, "SANGAN", ".9.100", "srv-share", "192.168.9.106", "اشتراک فایل سنگان", "Default", "ok", "SANGAN"),
        ServerEntity("s_56", 56, "SANGAN", ".9.100", "SRV-2-PWKARA", "192.168.9.107", "حضور و غیاب سنگان", "Default", "ok", "SANGAN"),
        ServerEntity("s_57", 57, "SANGAN", ".9.100", "FortiVM", "Needs Verification", "فایروال فورتی‌گیت مجازی", "-", "off", "SANGAN"),
        ServerEntity("s_58", 58, "SANGAN", ".9.100", "testR", "192.168.9.40", "سرور تست روتینگ", "-", "ok", "SANGAN"),
        ServerEntity("s_59", 59, "SANGAN", ".9.100", "PDC-P", "192.168.8.101", "اکتیو پروژه سنگان", "Default", "ok", "SANGAN"),
        ServerEntity("s_60", 60, "SANGAN", ".9.100", "SHARE-P", "192.168.8.102", "اشتراک فایل پروژه سنگان", "Default", "ok", "SANGAN"),
        ServerEntity("s_61", 61, "KANSARAN", ".12.100", "DC1", "192.168.12.101", "دامین کنترلر کانساران", "Default", "ok", "KANSARAN"),
        ServerEntity("s_62", 62, "KANSARAN", ".12.100", "SHARE", "192.168.12.102", "فایل سرور کانساران", "-", "ok", "KANSARAN"),
        ServerEntity("s_63", 63, "KANSARAN", ".12.100", "PWKRA", "192.168.12.103", "حضور و غیاب کانساران", "-", "ok", "KANSARAN"),
        ServerEntity("s_64", 64, "KANSARAN", ".12.100", "SRV-DOC", "192.168.12.105", "مستندات مهندسی کانساران", "-", "ok", "KANSARAN"),
        ServerEntity("s_65", 65, "KANSARAN", ".12.100", "RAHKARAN", "192.168.12.104", "راهکاران کانساران", "7304", "ok", "KANSARAN"),
        ServerEntity("s_66", 66, "KANSARAN", ".12.100", "Bitdefender", "192.168.12.110", "آنتی ویروس کانساران", "-", "ok", "KANSARAN"),
        ServerEntity("s_67", 67, "KANSARAN", ".12.100", "SRV-VPN", "192.168.12.106", "سرور ارتباطی VPN", "-", "ok", "KANSARAN"),
        ServerEntity("s_68", 68, "KANSARAN", ".12.100", "FTP", "192.168.12.107", "سرور انتقال فایل FTP", "-", "ok", "KANSARAN"),
        ServerEntity("s_69", 69, "TOUKA", ".5.100", "SRV-DC", "192.168.5.101", "اکتیو توکا سپاهان", "6565", "ok", "TOUKA", "Windows Server", "1.16"),
        ServerEntity("s_70", 70, "TOUKA", ".5.100", "SRV-PW", "192.168.5.102", "سرویس کارا توکا", "Default", "ok", "TOUKA"),
        ServerEntity("s_71", 71, "Felezi", ".2.100", "Bitdefender", "192.168.2.110", "آنتی ویروس صنایع فلزی", "-", "ok", "Felezi"),
        ServerEntity("s_72", 72, "Felezi", ".2.100", "SRV-SINAPACK", "192.168.2.123", "سرور سیناپک فلزی", "Default", "ok", "Felezi"),
        ServerEntity("s_73", 73, "JAHAN FACTORI", ".3.100", "SRV-DC", "192.168.3.23", "دامین کنترلر کارخانه جهان", "Default", "ok", "JAHAN FACTORI"),
        ServerEntity("s_74", 74, "JAHAN FACTORI", ".3.100", "Bitdefender", "192.168.3.110", "آنتی ویروس کارخانه", "-", "ok", "JAHAN FACTORI"),
        ServerEntity("s_75", 75, "TOUKA", "88", "SRV-TOUKA-88", "192.168.88.105", "راهکاران توکاسیر", "7308", "ok", "TOUKA"),
        ServerEntity("s_76", 76, "Jkousha", ".4.100", "Jkousha", "192.168.4.1", "اکتیو کوشا نوین", "52859", "ok", "Jkousha"),
        ServerEntity("s_77", 77, "Jkousha", ".4.100", "padvish", "192.168.4.3", "پادویش آنتی ویروس (pw patrol)", "-", "ok", "Jkousha"),
        ServerEntity("s_78", 78, "Jkousha", ".4.100", "Bitdefender", "192.168.4.110", "بیت‌دیفندر کوشا", "-", "ok", "Jkousha"),
        ServerEntity("s_79", 79, "Ardakan", ".7.100", "Host-7.100", "Needs Verification", "هاست زیرساختی ۷", "-", "ok", "Ardakan")
    )

    // Extracted directly from Link Rahkaran.pdf (Pages 1 & 2)
    val rahkaranCompanies = listOf(
        RahkaranEntity("rk_1", 1, "صنایع فولاد کوشا نوین", "192.168.1.202", "7303", "801", "http://192.168.1.202:801/folad", "راهکاران فولاد کوشا", "Port Web: 801"),
        RahkaranEntity("rk_2", 2, "ذوب امید آریان", "192.168.1.203", "7306", "806", "http://192.168.1.203:806/Zob_sg", "راهکاران ذوب امید", "Port Web: 806"),
        RahkaranEntity("rk_3", 3, "قائم پروفیل رازی", "192.168.1.22", "7300", "31033", "http://192.168.1.22:31033/razi_sg", "راهکاران قائم پروفیل", "Port Web: 31033"),
        RahkaranEntity("rk_4", 4, "جهان پروفیل پارس", "192.168.1.8", "7302", "31039", "http://jahanr:31039", "راهکاران جهان پروفیل", "Port Web: 31039"),
        RahkaranEntity("rk_5", 5, "توکاسیر سپاهان", "192.168.88.105", "7308", "31320", "http://192.168.88.105:31320", "راهکاران توکاسیر", "Port Web: 31320"),
        RahkaranEntity("rk_6", 6, "کانساران معدن نادر", "192.168.12.104", "7304", "31030", "http://192.168.12.104:31030", "راهکاران کانساران", "Port Web: 31030"),
        RahkaranEntity("rk_7", 7, "ذوب فولاد سنگان", "192.168.9.105", "7309", "31033", "http://192.168.9.105:31033", "راهکاران ذوب سنگان", "Port Web: 31033"),
        RahkaranEntity("rk_8", 8, "لوله مازندران", "192.168.6.155", "7308", "3107", "http://192.168.6.155:3107", "راهکاران لوله مازندران", "Port Web: 3107"),
        RahkaranEntity("rk_9", 9, "سردخانه رباط کریم", "192.168.18.19", "7305", "31031", "http://192.168.18.19:31031/sg", "راهکاران سردخانه", "Port Web: 31031"),
        RahkaranEntity("rk_10", 10, "فورج پرند", "192.168.1.31", "7310", "31041", "http://192.168.1.31:31041", "راهکاران فورج پرند", "Port Web: 31041"),
        RahkaranEntity("rk_11", 11, "صنایع فلزی کوشا نوین", "192.168.1.32", "7311", "31043", "http://192.168.1.32:31043/Authentication", "راهکاران صنایع فلزی", "Port Web: 31043"),
        RahkaranEntity("rk_12", 12, "فولاد اردکان", "192.168.50.8", "7312", "31030", "http://192.168.50.8:31030", "راهکاران فولاد اردکان", "Port Web: 31030")
    )

    // Network Academy Extensive Knowledge Articles (Sections 9 - 17)
    val articles = listOf(
        // Help Desk
        ArticleEntity(
            id = "art_hd_01",
            title = "Resolving Windows Network Connectivity & DHCP Failures",
            titleFa = "عیب‌یابی مشکلات اتصال شبکه و عدم دریافت IP از DHCP در ویندوز",
            summary = "Step-by-step diagnostic workflow for IP lease renewal, socket resets, and DNS resolution.",
            summaryFa = "مراحل گام‌به‌گام دریافت مجدد آی‌پی، ریست سوکت‌های شبکه و رفع خطاهای اتصال کلاینت.",
            content = "When client computers display 'No Internet Access' or 169.254.x.x (APIPA) addresses, follow this systematic procedure to isolate physical link issues, driver stack corruption, or DHCP exhaustion.",
            categorySlug = "help-desk",
            subcategory = "Network Connectivity",
            vendor = "Microsoft Windows",
            difficulty = "Fundamental",
            tags = "DHCP, IPConfig, Winsock, DNS, Network Adapter",
            commandsJson = """[
                {"label": "Check IP Configuration", "cmd": "ipconfig /all"},
                {"label": "Release Current Lease", "cmd": "ipconfig /release"},
                {"label": "Request New DHCP Lease", "cmd": "ipconfig /renew"},
                {"label": "Flush DNS Resolver Cache", "cmd": "ipconfig /flushdns"},
                {"label": "Reset TCP/IP and Winsock Stack", "cmd": "netsh winsock reset && netsh int ip reset"}
            ]""",
            warnings = "Resetting the TCP/IP stack requires an immediate system reboot to take full effect.",
            expectedResults = "A valid IPv4 lease matching the local subnet scope (e.g. 192.168.x.x) is assigned, and Default Gateway responds to ICMP echo requests.",
            verification = "ping <Default Gateway IP>\nping 8.8.8.8\nnslookup google.com",
            troubleshooting = "If APIPA (169.254.x.x) persists, inspect physical cable with a cable tester, check switch port VLAN membership, and check DHCP server scope pool status."
        ),
        ArticleEntity(
            id = "art_hd_02",
            title = "Troubleshooting Print Spooler Crashes and Network Printers",
            titleFa = "عیب‌یابی توقف سرویس پرینت اسپولر و پرینترهای تحت شبکه",
            summary = "Clearing corrupted print queues, restarting spooler services, and checking port 9100 Raw/LPR communication.",
            summaryFa = "پاک‌سازی صف پرینت خراب، ریستارت سرویس Spooler و تست ارتباط پورت ۹۱۰۰ پرینتر.",
            content = "Network printers frequently encounter hung jobs in the Windows print spooler queue (C:\\Windows\\System32\\spool\\PRINTERS). This guide provides automated PowerShell one-liners to clear stuck spool files safely.",
            categorySlug = "help-desk",
            subcategory = "Printing",
            vendor = "Windows / HP / Canon",
            difficulty = "Fundamental",
            tags = "Print Spooler, Port 9100, RAW, LPR",
            commandsJson = """[
                {"label": "Stop Spooler Service", "cmd": "Stop-Service -Name Spooler -Force"},
                {"label": "Clear Stuck Print Spool Files", "cmd": "Remove-Item -Path C:\\Windows\\System32\\spool\\PRINTERS\\* -Force"},
                {"label": "Start Spooler Service", "cmd": "Start-Service -Name Spooler"},
                {"label": "Test RAW 9100 Port Connectivity", "cmd": "Test-NetConnection -ComputerName 192.168.1.50 -Port 9100"}
            ]""",
            warnings = "Ensure no pending mission-critical jobs before clearing the PRINTERS directory.",
            expectedResults = "Spooler starts clean and printer status changes from 'Error' or 'Offline' to 'Ready'.",
            verification = "Get-Printer | Select-Object Name, PrinterStatus, PortName"
        ),
        ArticleEntity(
            id = "art_hd_03",
            title = "Resolving Remote Desktop (RDP) Protocol Failures & Port 3389",
            titleFa = "رفع خطاهای اتصال ریموت دسکتاپ RDP و پورت ۳۳۸۹",
            summary = "Diagnosing NLA (Network Level Authentication) mismatches, Windows Firewall blocks, and registry port changes.",
            summaryFa = "بررسی خطاهای NLA، فایروال ویندوز و تغییر پورت پیش‌فرض RDP.",
            content = "Remote Desktop connection failures usually stem from: disabled Remote Desktop setting, Windows Firewall blocking port 3389, CredSSP encryption oracle remediation mismatch, or NLA requirements.",
            categorySlug = "help-desk",
            subcategory = "Remote Support",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "RDP, 3389, NLA, CredSSP, Firewall",
            commandsJson = """[
                {"label": "Verify RDP Port Listening (Local)", "cmd": "netstat -ano | findstr :3389"},
                {"label": "Test Remote RDP Port from PowerShell", "cmd": "Test-NetConnection -ComputerName 192.168.1.10 -Port 3389"},
                {"label": "Enable RDP via PowerShell (Remote Admin)", "cmd": "Set-ItemProperty -Path 'HKLM:\\System\\CurrentControlSet\\Control\\Terminal Server' -Name 'fDenyTSConnections' -Value 0"},
                {"label": "Allow RDP Rule in Windows Firewall", "cmd": "Enable-NetFirewallRule -DisplayGroup 'Remote Desktop'"}
            ]""",
            expectedResults = "TCP connection to port 3389 succeeds with TcpTestSucceeded: True.",
            verification = "mstsc /v:192.168.1.10:3389 /admin"
        ),

        // Network+
        ArticleEntity(
            id = "art_net_01",
            title = "OSI vs TCP/IP Models & Protocol Data Units (PDU)",
            titleFa = "مقایسه مدل‌های هفت‌لایه OSI و TCP/IP و واحدهای داده (PDU)",
            summary = "Deep dive into encapsulation, Layer 2 frames, Layer 3 packets, Layer 4 segments, and Layer 7 data.",
            summaryFa = "بررسی کپسوله‌سازی دیتا، فریم لایه ۲، پکت لایه ۳ و سگمنت لایه ۴.",
            content = "Understanding network protocol encapsulation is the cornerstone of all network engineering and packet analysis. Each layer prepends its header:\n\n1. Application (Data)\n2. Transport (Segment with TCP/UDP ports)\n3. Network (Packet with Source/Destination IP)\n4. Data Link (Frame with MAC addresses and FCS)\n5. Physical (Bits on copper/fiber/radio).",
            categorySlug = "network-plus",
            subcategory = "Fundamentals",
            vendor = "CompTIA / IEEE",
            difficulty = "Fundamental",
            tags = "OSI, TCP/IP, PDU, Encapsulation, MAC, IP",
            commandsJson = """[
                {"label": "Inspect Local ARP Table", "cmd": "arp -a"},
                {"label": "View Route Table and Gateways", "cmd": "route print -4"}
            ]"""
        ),
        ArticleEntity(
            id = "art_net_02",
            title = "IPv4 Subnetting, CIDR Notation & VLSM Allocation",
            titleFa = "محاسبات ساب‌نتینگ IPv4، نماد CIDR و تخصیص VLSM",
            summary = "Mastering powers of two, subnet boundary math, host formula (2^h - 2), and prefix lengths.",
            summaryFa = "فرمول تعداد هاست، تشخیص آدرس شبکه و برودکست و مرزهای ساب‌نت.",
            content = "A subnet mask separates the 32-bit IPv4 address into Network Bits and Host Bits. Formula for usable hosts: 2^h - 2 (subtracting network and broadcast addresses). A /24 provides 254 hosts; /26 provides 62 hosts; /30 provides 2 hosts for point-to-point router links.",
            categorySlug = "network-plus",
            subcategory = "Addressing",
            vendor = "IETF",
            difficulty = "Intermediate",
            tags = "Subnetting, CIDR, VLSM, IPv4, Addressing",
            commandsJson = """[
                {"label": "Determine Default Interface Prefix", "cmd": "ip route show"}
            ]"""
        ),

        // CCNA
        ArticleEntity(
            id = "art_ccna_01",
            title = "Cisco Switch VLAN Configuration & 802.1Q Trunking",
            titleFa = "پیکربندی VLAN و پورت‌های ترانک 802.1Q در سوئیچ سیسکو",
            summary = "Configuring access ports, creating VLANs, and establishing IEEE 802.1Q trunk links between switches.",
            summaryFa = "تنظیم پورت‌های اکسس، تعریف وی‌لن و فعال‌سازی ترانک 802.1Q بین سوئیچ‌ها.",
            content = "Virtual LANs (VLANs) segment broadcast domains on Layer 2 switches. Trunk links carry multiple VLANs across switches using 4-byte 802.1Q tags containing the 12-bit VLAN ID (1 to 4094).",
            categorySlug = "ccna",
            subcategory = "Switching",
            vendor = "Cisco Systems",
            difficulty = "Intermediate",
            tags = "VLAN, Trunk, 802.1Q, Cisco IOS, Switchport",
            commandsJson = """[
                {"label": "Enter Config & Create VLANs", "cmd": "enable\nconfigure terminal\nvlan 10\n name SALES\nvlan 20\n name ENGINEERING\nexit"},
                {"label": "Configure Access Port", "cmd": "interface GigabitEthernet0/1\n switchport mode access\n switchport access vlan 10\n spanning-tree portfast\n no shutdown"},
                {"label": "Configure 802.1Q Trunk Port", "cmd": "interface GigabitEthernet0/24\n switchport trunk encapsulation dot1q\n switchport mode trunk\n switchport trunk allowed vlan 10,20\n no shutdown"},
                {"label": "Verify VLAN and Trunk Status", "cmd": "show vlan brief\nshow interfaces trunk"}
            ]""",
            expectedResults = "Trunk port shows status 'trunking' with encapsulation '802.1q' and allowed VLANs active.",
            verification = "show interfaces trunk\nshow mac address-table dynamic"
        ),
        ArticleEntity(
            id = "art_ccna_02",
            title = "Single-Area OSPFv2 Routing Configuration and Neighbors",
            titleFa = "پیکربندی پروتکل مسیریابی OSPFv2 تک‌ناحیه‌ای در سیسکو",
            summary = "Enabling Open Shortest Path First, router-ids, network statements, and adjacency verification.",
            summaryFa = "راه‌اندازی OSPFv2، تنظیم Router ID، دستور network و بررسی وضعیت Neighbor.",
            content = "OSPF is a link-state interior gateway routing protocol (IGP) using the Dijkstra SPF algorithm. Routers form neighbor adjacencies through Hello packets (multicast 224.0.0.5) and exchange Link State Advertisements (LSAs).",
            categorySlug = "ccna",
            subcategory = "Routing",
            vendor = "Cisco Systems",
            difficulty = "Intermediate",
            tags = "OSPF, Routing, Area 0, Dijkstra, Router-ID",
            commandsJson = """[
                {"label": "Configure OSPF Process and Area 0", "cmd": "configure terminal\nrouter ospf 1\n router-id 1.1.1.1\n network 192.168.10.0 0.0.0.255 area 0\n network 10.0.0.0 0.0.0.3 area 0\n passive-interface GigabitEthernet0/0"},
                {"label": "Verify OSPF Neighbor Adjacency", "cmd": "show ip ospf neighbor"},
                {"label": "Inspect OSPF Route Table Entries", "cmd": "show ip route ospf"}
            ]""",
            expectedResults = "Neighbor state advances to 'FULL/DR' or 'FULL/BDR' and OSPF routes appear marked with 'O' in routing table.",
            verification = "show ip ospf neighbor\nshow ip route ospf"
        ),

        // CCNP
        ArticleEntity(
            id = "art_ccnp_01",
            title = "BGP (Border Gateway Protocol) eBGP and iBGP Peering",
            titleFa = "پیکربندی همسایگی BGP (eBGP و iBGP) و فیلترینگ مسیر",
            summary = "Autonomous System peering, next-hop-self, route-maps, and prefix-lists.",
            summaryFa = "اتصال ASهای مختلف، دستور next-hop-self، روت‌مپ و پالیسی‌های BGP.",
            content = "BGP is the path-vector protocol that powers the global Internet. eBGP operates between different Autonomous Systems (AS) with default TTL of 1, while iBGP operates within the same AS and requires a full mesh or Route Reflectors due to split-horizon rules.",
            categorySlug = "ccnp",
            subcategory = "Advanced Routing",
            vendor = "Cisco Systems",
            difficulty = "Advanced",
            tags = "BGP, eBGP, iBGP, AS, Route-Map, Prefix-List",
            commandsJson = """[
                {"label": "Configure eBGP Neighbor", "cmd": "router bgp 65001\n bgp log-neighbor-changes\n neighbor 203.0.113.2 remote-as 65002\n neighbor 203.0.113.2 description ISP-PRIMARY\n network 198.51.100.0 mask 255.255.255.0"},
                {"label": "Configure iBGP with Loopback & Next-Hop-Self", "cmd": "router bgp 65001\n neighbor 10.255.255.2 remote-as 65001\n neighbor 10.255.255.2 update-source Loopback0\n neighbor 10.255.255.2 next-hop-self"},
                {"label": "Verify BGP Summary & Routes", "cmd": "show ip bgp summary\nshow ip bgp"}
            ]""",
            expectedResults = "State/PfxRcd column displays a numeric value (count of received prefixes) rather than Active/Idle.",
            verification = "show ip bgp summary\nshow ip route bgp"
        ),

        // MikroTik
        ArticleEntity(
            id = "art_mt_01",
            title = "MikroTik RouterOS NAT Masquerade & Port Forwarding (Dst-NAT)",
            titleFa = "پیکربندی NAT سورس (Masquerade) و پورت فورواردینگ در میکروتیک",
            summary = "Configuring Internet NAT masquerading and exposing internal servers safely via dstnat.",
            summaryFa = "اشتراک اینترنت با مسکورید و انتقال پورت به سرورهای داخلی با dstnat.",
            content = "MikroTik RouterOS handles network address translation in `/ip firewall nat`. Masquerade rewrites source IP to outgoing WAN IP. Dst-NAT redirects incoming WAN traffic to internal servers (such as RDP or Web portals).",
            categorySlug = "mikrotik",
            subcategory = "Firewall & NAT",
            vendor = "MikroTik",
            difficulty = "Intermediate",
            tags = "MikroTik, RouterOS, NAT, Masquerade, Port Forward, Dst-NAT",
            commandsJson = """[
                {"label": "Source NAT (Internet Sharing)", "cmd": "/ip firewall nat add chain=srcnat out-interface=ether1-WAN action=masquerade comment=\"WAN Masquerade\""},
                {"label": "Port Forwarding to Internal Server (Dst-NAT)", "cmd": "/ip firewall nat add chain=dstnat in-interface=ether1-WAN protocol=tcp dst-port=7300 action=dst-nat to-addresses=192.168.1.22 to-ports=7300 comment=\"Forward to Ghaem Profile\""},
                {"label": "View Active NAT Rules", "cmd": "/ip firewall nat print"}
            ]""",
            expectedResults = "Counters on NAT rule increment and external connections establish successfully to internal server.",
            verification = "/ip firewall nat print stats"
        ),
        ArticleEntity(
            id = "art_mt_02",
            title = "MikroTik RouterOS WireGuard Site-to-Site & Client VPN",
            titleFa = "راه‌اندازی تونل سریع و امن WireGuard در روتربورد میکروتیک",
            summary = "Creating modern lightweight WireGuard interfaces, peers, and static routes.",
            summaryFa = "ساخت اینترفیس وایرگارد، تعریف Peerها و مسیریابی بین شعب.",
            content = "WireGuard in RouterOS v7 provides high-speed encrypted UDP tunneling using modern cryptography (Curve25519, ChaCha20, Poly1305).",
            categorySlug = "mikrotik",
            subcategory = "VPN",
            vendor = "MikroTik",
            difficulty = "Intermediate",
            tags = "WireGuard, VPN, RouterOS v7, Cryptography",
            commandsJson = """[
                {"label": "Create WireGuard Interface", "cmd": "/interface wireguard add name=wg-branch listen-port=13231 mtu=1420"},
                {"label": "Assign IP to WireGuard Interface", "cmd": "/ip address add address=10.10.10.1/24 interface=wg-branch"},
                {"label": "Add Remote Peer", "cmd": "/interface wireguard peers add interface=wg-branch public-key=\"<REMOTE_PUB_KEY>\" endpoint-address=\"branch.company.com\" endpoint-port=13231 allowed-address=10.10.10.2/32,192.168.50.0/24 persistent-keepalive=25s"},
                {"label": "Add Route for Remote Subnet", "cmd": "/ip route add dst-address=192.168.50.0/24 gateway=wg-branch"}
            ]"""
        ),

        // FortiGate
        ArticleEntity(
            id = "art_fg_01",
            title = "FortiGate IPsec VPN Site-to-Site & CLI Debug Flow",
            titleFa = "تونل IPsec بین دیتاسنترها و عیب‌یابی با Debug Flow در فورتی‌گیت",
            summary = "IKE Phase 1, Phase 2, Firewall Policies, and live packet inspection via CLI.",
            summaryFa = "تنظیم فاز ۱ و ۲، نوشتن پالیسی‌های فایروال و ردگیری زنده پکت‌ها با Debug Flow.",
            content = "FortiGate firewalls require phase1-interface, phase2-interface, address objects, and bidirectional firewall policies. When packets drop, `diagnose debug flow` provides real-time state engine insights.",
            categorySlug = "fortigate",
            subcategory = "IPsec & Debugging",
            vendor = "Fortinet",
            difficulty = "Advanced",
            tags = "FortiGate, IPsec, VPN, Debug Flow, Packet Capture",
            commandsJson = """[
                {"label": "Check IPsec Tunnel Phase 1 Status", "cmd": "diagnose vpn ike gateway list"},
                {"label": "Check IPsec Tunnel Phase 2 SAs", "cmd": "diagnose vpn tunnel list"},
                {"label": "Run Live CLI Packet Debug Flow", "cmd": "diagnose debug reset\ndiagnose debug flow filter saddr 192.168.1.100\ndiagnose debug flow filter daddr 192.168.50.20\ndiagnose debug flow show function-name enable\ndiagnose debug flow trace start 10\ndiagnose debug enable"},
                {"label": "Stop Debug Output", "cmd": "diagnose debug disable\ndiagnose debug reset"}
            ]"""
        ),

        // Windows Server & Active Directory
        ArticleEntity(
            id = "art_win_01",
            title = "Active Directory Domain Controller Health & Replication Check",
            titleFa = "بررسی سلامت دامین کنترلر، ریپلیکیشن و FSMO رول‌ها در اکتیو دایرکتوری",
            summary = "Running DCDiag, Repadmin, and FSMO queries across domain controllers.",
            summaryFa = "اجرای دستورات dcdiag و repadmin برای اطمینان از سلامت اکتیو دایرکتوری و رفع خطاهای ریپلیکیشن.",
            content = "In multi-DC environments (like Ardakan and Jahan), maintaining synchronized AD databases (NTDS.dit) is critical for Kerberos ticket issuance, user authentication, and group policy application.",
            categorySlug = "windows-server",
            subcategory = "Active Directory",
            vendor = "Microsoft Windows Server",
            difficulty = "Advanced",
            tags = "Active Directory, Domain Controller, Repadmin, DCDiag, FSMO",
            commandsJson = """[
                {"label": "Inspect AD Replication Partners & Failures", "cmd": "repadmin /replsummary"},
                {"label": "Force Immediate Replication Across All Partners", "cmd": "repadmin /syncall /AdeP"},
                {"label": "Run Full Domain Controller Diagnostics", "cmd": "dcdiag /v /c /d /e"},
                {"label": "Query 5 FSMO Role Holders", "cmd": "netdom query fsmo"}
            ]""",
            expectedResults = "Largest Delta shows low minutes, '0 fails', and all tests in dcdiag show passed.",
            verification = "repadmin /showrepl"
        ),

        // ==========================================
        // WINDOWS PRO TIPS & SYSADMIN HACKS (ترفندهای ویندوزی)
        // ==========================================
        ArticleEntity(
            id = "art_win_godmode",
            title = "God Mode & Master Control Panel GUID",
            titleFa = "ترفند فعال‌سازی گاد مود (God Mode) و کنترل‌پنل جامع مخفی ویندوز",
            summary = "Unlocking the hidden master control panel containing over 200+ administrative applets in a single folder.",
            summaryFa = "دسترسی به بیش از ۲۰۰ تنظیم و ابزار مدیریتی ویندوز در یک پوشه واحد بدون نیاز به گشتن در کنترل‌پنل.",
            content = "Windows contains an undocumented GUID-based namespace junction that aggregates every Administrative Tool, Security setting, Device Manager, and Network Config into a searchable folder. Create a new folder anywhere and name it with the exact canonical GUID string.",
            categorySlug = "windows-tricks",
            subcategory = "Productivity & Tweaks",
            vendor = "Microsoft Windows",
            difficulty = "Fundamental",
            tags = "GodMode, GUID, Shell, Control Panel, Shortcut",
            commandsJson = """[
                {"label": "Create God Mode Folder via CMD", "cmd": "mkdir \"%USERPROFILE%\\Desktop\\GodMode.{ED7BA470-8E54-465E-825C-99712043E01C}\""},
                {"label": "Open Directly via Shell Run", "cmd": "explorer.exe shell:::{ED7BA470-8E54-465E-825C-99712043E01C}"}
            ]""",
            expectedResults = "A folder with the Control Panel icon appears displaying all 200+ settings grouped by category.",
            verification = "dir \"%USERPROFILE%\\Desktop\\GodMode*\""
        ),
        ArticleEntity(
            id = "art_win_tcptune",
            title = "Turbocharge Windows Network Stack (TCP Window Auto-Tuning & Offloads)",
            titleFa = "بهینه‌سازی و افزایش سرعت پهنای باند شبکه ویندوز با تنظیمات پیشرفته TCP",
            summary = "Tuning Receive Window Auto-Tuning, RSS (Receive Side Scaling), and Chimney Offload for maximum gigabit throughput.",
            summaryFa = "رفع افت سرعت دانلود و انتقال فایل در شبکه گیگابیت با فعال‌سازی TCP Auto-Tuning و تنظیمات netsh.",
            content = "By default, Windows may restrict TCP receive windows on certain network adapters or legacy profiles, artificially bottlenecking high-speed 1Gbps/10Gbps transfers. Using netsh commands elevates the TCP stack to optimal performance.",
            categorySlug = "windows-tricks",
            subcategory = "Network Tuning",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "TCP, Netsh, Auto-Tuning, RSS, Bandwidth, Speed",
            commandsJson = """[
                {"label": "Check Current TCP Global Settings", "cmd": "netsh int tcp show global"},
                {"label": "Enable Normal Auto-Tuning", "cmd": "netsh int tcp set global autotuninglevel=normal"},
                {"label": "Enable Receive-Side Scaling (Multi-core CPU)", "cmd": "netsh int tcp set global rss=enabled"},
                {"label": "Enable TCP Chimney / Checksum Offload", "cmd": "netsh int ip set global taskoffload=enabled"}
            ]""",
            expectedResults = "Receive Window Auto-Tuning Level changes to 'normal' and multi-stream transfers utilize full interface bandwidth.",
            verification = "netsh int tcp show global"
        ),
        ArticleEntity(
            id = "art_win_fixupdate",
            title = "Rescue Stuck Windows Update Engine & Reset SoftwareDistribution Store",
            titleFa = "حل مشکل هنگ و خطاهای آپدیت ویندوز با پاک‌سازی کامل پوشه SoftwareDistribution",
            summary = "Terminating update services, purging corrupted update caches, and re-registering BITS/WUAUSERV DLLs.",
            summaryFa = "تعمیر خطاهای 0x80070002 و 0x80240034 هنگام نصب آپدیت با متوقف‌سازی سرویس‌ها و تخلیه کش دانلود.",
            content = "Corrupted download chunks in C:\\Windows\\SoftwareDistribution frequently cause Windows Update to freeze at 0% or loop indefinitely. This proven sequence cleanly drains the queue.",
            categorySlug = "windows-tricks",
            subcategory = "Maintenance",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "Windows Update, SoftwareDistribution, BITS, WUAUSERV, Fix",
            commandsJson = """[
                {"label": "Stop Windows Update & BITS Services", "cmd": "net stop wuauserv\nnet stop cryptSvc\nnet stop bits\nnet stop msiserver"},
                {"label": "Rename Corrupted Update Folders", "cmd": "ren C:\\Windows\\SoftwareDistribution SoftwareDistribution.old\nren C:\\Windows\\System32\\catroot2 catroot2.old"},
                {"label": "Restart Windows Update Services", "cmd": "net start wuauserv\nnet start cryptSvc\nnet start bits\nnet start msiserver"},
                {"label": "Trigger Immediate Update Check", "cmd": "UsoClient.exe StartScan"}
            ]""",
            expectedResults = "Windows Update restarts with a pristine queue and downloads fresh update binaries without hanging.",
            verification = "Get-Service wuauserv, bits"
        ),
        ArticleEntity(
            id = "art_win_winsxs",
            title = "Reclaim 20GB+ Disk Space: Deep Clean WinSxS Component Store via DISM",
            titleFa = "آزادسازی فضای درایو C با پاک‌سازی عمیق انبار مؤلفه‌های WinSxS از طریق DISM",
            summary = "Safely purging superseded Windows updates, obsolete service packs, and backup packages.",
            summaryFa = "حذف فایل‌های آپدیت قدیمی و جایگزین‌شده در پوشه حجیم WinSxS بدون صدمه به سیستم‌عامل.",
            content = "The WinSxS (Windows Side-by-Side) folder accumulates superseded version binaries after every monthly cumulative patch. Standard Disk Cleanup misses these, but DISM commands with /ResetBase permanently prune superseded revisions.",
            categorySlug = "windows-tricks",
            subcategory = "Disk Optimization",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "DISM, WinSxS, Disk Cleanup, Storage, ResetBase",
            commandsJson = """[
                {"label": "Analyze WinSxS Actual Component Size", "cmd": "Dism.exe /Online /Cleanup-Image /AnalyzeComponentStore"},
                {"label": "Run Deep Component Cleanup", "cmd": "Dism.exe /Online /Cleanup-Image /StartComponentCleanup"},
                {"label": "Permanently Remove Superseded Updates (/ResetBase)", "cmd": "Dism.exe /Online /Cleanup-Image /StartComponentCleanup /ResetBase"}
            ]""",
            warnings = "Once /ResetBase is executed, you will not be able to uninstall previous cumulative updates, but dozens of gigabytes of disk space are reclaimed.",
            expectedResults = "Component store cleanup completes 100% and disk free space on drive C: increases noticeably.",
            verification = "Dism.exe /Online /Cleanup-Image /AnalyzeComponentStore"
        ),
        ArticleEntity(
            id = "art_win_taskkill",
            title = "Force Kill Frozen Processes Holding Open Ports & File Locks",
            titleFa = "بستن اجباری پروسه‌های هنگ کرده و آزادکردن فوری پورت‌های اشغال‌شده",
            summary = "Finding the exact PID holding a network port (like 80, 443, 3389, 7300) and terminating its process tree immediately.",
            summaryFa = "تشخیص شناسه پروسه (PID) مسدودکننده پورت با netstat و بستن اجباری درخت پروسه با taskkill /f /t.",
            content = "When an application fails to start because 'Port is already in use', standard Task Manager might hide the child process. Use netstat to map port to PID, then taskkill /f /t to obliterate it.",
            categorySlug = "windows-tricks",
            subcategory = "Troubleshooting",
            vendor = "Microsoft Windows",
            difficulty = "Fundamental",
            tags = "Taskkill, Netstat, PID, Ports, Freeze, Kill",
            commandsJson = """[
                {"label": "Find PID Listening on Target Port (e.g. 7300)", "cmd": "netstat -ano | findstr :7300"},
                {"label": "Identify Process Name by PID", "cmd": "tasklist /FI \"PID eq <PID_NUMBER>\""},
                {"label": "Forcefully Terminate Process and Children", "cmd": "taskkill /F /T /PID <PID_NUMBER>"},
                {"label": "Force Kill by Image Name (e.g. hung EXCEL.EXE)", "cmd": "taskkill /F /IM EXCEL.EXE /T"}
            ]""",
            expectedResults = "SUCCESS: The process with PID has been terminated. Port becomes immediately available for binding.",
            verification = "netstat -ano | findstr :7300"
        ),
        ArticleEntity(
            id = "art_win_powerplan",
            title = "Unlock Hidden 'Ultimate Performance' Power Plan in Windows",
            titleFa = "فعال‌سازی پلن مصرف انرژی مخفی Ultimate Performance برای حداکثر بازدهی",
            summary = "Unlocking the workstation-grade power plan that eliminates micro-latencies and CPU core parking.",
            summaryFa = "حذف تاخیرهای فرکانسی و جلوگیری از به خواب رفتن هسته‌های پردازنده در سرورها و سیستم‌های تحت فشار.",
            content = "Microsoft developed the 'Ultimate Performance' power scheme for high-end workstations and servers. It bypasses conventional power-saving heuristics to maximize real-time hardware response.",
            categorySlug = "windows-tricks",
            subcategory = "Performance",
            vendor = "Microsoft Windows",
            difficulty = "Fundamental",
            tags = "Powercfg, Ultimate Performance, CPU, Speed, Latency",
            commandsJson = """[
                {"label": "Unlock Ultimate Performance Power Plan", "cmd": "powercfg -duplicatescheme e9a42b02-d5df-448d-aa00-03f14749eb61"},
                {"label": "List All Available Power Schemes", "cmd": "powercfg /L"},
                {"label": "Set Ultimate Performance as Active", "cmd": "powercfg /setactive e9a42b02-d5df-448d-aa00-03f14749eb61"}
            ]""",
            expectedResults = "Power Scheme GUID: e9a42b02-d5df-448d-aa00-03f14749eb61 (Ultimate Performance) is created and set active.",
            verification = "powercfg /getactivescheme"
        ),
        ArticleEntity(
            id = "art_win_winget",
            title = "Automated Fleet Software Deployment & Bulk Updates via WinGet",
            titleFa = "نصب و بروزرسانی خودکار و سایلنت نرم‌افزارهای سازمانی با اسکریپت WinGet",
            summary = "Installing Google Chrome, 7-Zip, AnyDesk, Notepad++, Putty, Wireshark silently with a single PowerShell line.",
            summaryFa = "راه‌اندازی فوری کلاینت جدید بدون نیاز به دانلود دستی نرم‌افزارها و کلیک بر روی گزینه‌های Next.",
            content = "Windows Package Manager (winget) allows IT administrators to deploy official vendor packages silently in seconds, bypassing bundled adware and manual installer clicks.",
            categorySlug = "windows-tricks",
            subcategory = "Automation",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "WinGet, Automation, Deployment, Silent Install, Fleet",
            commandsJson = """[
                {"label": "One-Line IT Toolchain Deployment", "cmd": "winget install --id 7zip.7zip --silent --accept-package-agreements\nwinget install --id Google.Chrome --silent --accept-package-agreements\nwinget install --id Notepad++.Notepad++ --silent --accept-package-agreements\nwinget install --id WiresharkFoundation.Wireshark --silent --accept-package-agreements\nwinget install --id PuTTY.PuTTY --silent --accept-package-agreements"},
                {"label": "Upgrade All Installed Applications Silently", "cmd": "winget upgrade --all --silent --accept-package-agreements"}
            ]""",
            expectedResults = "All designated tools install silently into Program Files with desktop and start menu shortcuts.",
            verification = "winget list"
        ),

        // ==========================================
        // ADVANCED HELP DESK & TROUBLESHOOTING
        // ==========================================
        ArticleEntity(
            id = "art_hd_bsod",
            title = "Blue Screen of Death (BSOD) Minidump Triage & Crash Dump Analysis",
            titleFa = "عیب‌یابی تخصصی صفحه آبی مرگ (BSOD) و تحلیل فایل‌های دامپ با WinDbg",
            summary = "Locating memory dumps, reading bugcheck codes (CRITICAL_PROCESS_DIED, KERNEL_SECURITY_CHECK), and isolating faulty driver sys files.",
            summaryFa = "بررسی خطاهای Stop Code و مشخص‌کردن درایور یا سخت‌افزار مقصر از روی فایل‌های Minidump.",
            content = "When Windows crashes with a BSOD, a crash dump is recorded in C:\\Windows\\Minidump. Using PowerShell and dump utilities pinpoints the exact driver filename (e.g., nvlddmkm.sys, netwtw08.sys) causing the kernel panic.",
            categorySlug = "help-desk",
            subcategory = "Crash Analysis",
            vendor = "Microsoft Windows",
            difficulty = "Advanced",
            tags = "BSOD, Minidump, WinDbg, Bugcheck, Driver, Crash",
            commandsJson = """[
                {"label": "Check Recent Crash Events in Event Log", "cmd": "Get-WinEvent -FilterHashtable @{LogName='System'; ProviderName='Microsoft-Windows-WER-SystemErrorReporting'} -MaxEvents 5"},
                {"label": "Configure System to Produce Small Minidumps", "cmd": "wmic recoveros set DebugInfoType = 3"},
                {"label": "Scan for System File Integrity Corruption", "cmd": "sfc /scannow && DISM /Online /Cleanup-Image /RestoreHealth"}
            ]""",
            expectedResults = "Faulting driver and memory address identified, allowing precise driver rollback or hardware testing.",
            verification = "sfc /scannow"
        ),
        ArticleEntity(
            id = "art_hd_profile",
            title = "Fixing Temporary Profile Logins & Corrupted Windows User Profiles",
            titleFa = "حل مشکل ورود کاربر با پروفایل موقت (We can't sign in to your account) در رجیستری",
            summary = "Fixing the ProfileList registry key .bak extension to restore access to Desktop and Documents.",
            summaryFa = "حذف پسوند .bak در شاخه ProfileList رجیستری و بازگردانی پروفایل اصلی کاربر بدون از دست رفتن فایل‌ها.",
            content = "If a user signs in and sees 'You've been signed in with a temporary profile', Windows failed to lock NTUSER.DAT. The solution involves renaming the .bak registry key in HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\ProfileList.",
            categorySlug = "help-desk",
            subcategory = "User Accounts",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "Profile, Temp Profile, Registry, ProfileList, SID, Fix",
            commandsJson = """[
                {"label": "Find User SID via PowerShell", "cmd": "wmic useraccount where name='<USERNAME>' get sid"},
                {"label": "Query ProfileList Registry Key", "cmd": "reg query \"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\ProfileList\""},
                {"label": "Fix Procedure in Regedit", "cmd": "# 1. Open regedit\n# 2. Navigate to HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\ProfileList\n# 3. Locate folder ending with .bak\n# 4. Remove .bak extension from the correct profile folder\n# 5. Set State value to 0"}
            ]""",
            expectedResults = "User logs in and Windows loads their original Desktop, Documents, and AppData directories properly."
        ),

        // ==========================================
        // ADVANCED CCNA & CCNP GUIDES
        // ==========================================
        ArticleEntity(
            id = "art_ccna_roas",
            title = "Cisco Router-on-a-Stick Inter-VLAN Routing with 802.1Q Subinterfaces",
            titleFa = "مسیریابی بین وی‌لن‌ها با متد Router-on-a-Stick در روتر و سوئیچ سیسکو",
            summary = "Configuring physical router subinterfaces (e.g. g0/0.10, g0/0.20) with 802.1Q encapsulation for inter-VLAN forwarding.",
            summaryFa = "اتصال یک پورت ترانک به روتر و تعریف ساب‌اینترفیس‌ها به همراه آی‌پی‌های گیت‌وی هر VLAN.",
            content = "Router-on-a-Stick (ROAS) routes traffic between different VLANs using a single physical interface connected via an 802.1Q trunk link to a Layer 2 switch.",
            categorySlug = "ccna",
            subcategory = "Routing",
            vendor = "Cisco Systems",
            difficulty = "Intermediate",
            tags = "ROAS, Inter-VLAN, Subinterface, 802.1Q, Cisco",
            commandsJson = """[
                {"label": "Configure Router Subinterfaces for VLAN 10 & 20", "cmd": "enable\nconfigure terminal\ninterface GigabitEthernet0/0\n no shutdown\ninterface GigabitEthernet0/0.10\n encapsulation dot1Q 10\n ip address 192.168.10.1 255.255.255.0\ninterface GigabitEthernet0/0.20\n encapsulation dot1Q 20\n ip address 192.168.20.1 255.255.255.0\nexit"},
                {"label": "Configure Switch Trunk Link to Router", "cmd": "interface GigabitEthernet0/1\n switchport mode trunk\n switchport trunk allowed vlan 10,20\n no shutdown"}
            ]""",
            expectedResults = "Hosts in VLAN 10 can successfully ping hosts in VLAN 20 through their respective subinterface default gateways.",
            verification = "show ip route\nshow ip interface brief"
        ),
        ArticleEntity(
            id = "art_ccnp_redist",
            title = "OSPF and BGP Mutual Route Redistribution with Route-Maps",
            titleFa = "بازتوزیع دوطرفه مسیرها (Redistribution) بین OSPF و BGP با Route-Map",
            summary = "Safely redistributing prefixes, preventing routing loops with tags, and setting metric types.",
            summaryFa = "انتقال روت‌ها بین شبکه‌های OSPF داخلی و BGP خارجی با جلوگیری از ایجاد لوپ روتینگ با تگ‌گذاری.",
            content = "Redistribution introduces routing loops unless strict route filtering and tagging are enforced. Tagging incoming routes allows mutual redistribution without route re-injection.",
            categorySlug = "ccnp",
            subcategory = "Advanced Routing",
            vendor = "Cisco Systems",
            difficulty = "Advanced",
            tags = "Redistribution, OSPF, BGP, Route-Map, Metric, Tags",
            commandsJson = """[
                {"label": "Define Prefix-List and Route-Map with Tag", "cmd": "ip prefix-list INTERNAL_NETS permit 192.168.0.0/16 ge 24\nroute-map OSPF_TO_BGP permit 10\n match ip address prefix-list INTERNAL_NETS\n set tag 100\nexit"},
                {"label": "Redistribute OSPF into BGP Process", "cmd": "router bgp 65001\n redistribute ospf 1 route-map OSPF_TO_BGP"},
                {"label": "Verify Redistributed Prefixes in BGP Table", "cmd": "show ip bgp 192.168.0.0"}
            ]""",
            expectedResults = "BGP table reflects internal OSPF prefixes flagged with origin code '?' or tagged properly.",
            verification = "show ip route bgp\nshow ip bgp"
        ),

        // ==========================================
        // ADVANCED MIKROTIK & FORTIGATE
        // ==========================================
        ArticleEntity(
            id = "art_mt_pcc",
            title = "MikroTik PCC Dual-WAN Load Balancing with Failover",
            titleFa = "لودبالانس و ترکیب پهنای باند دو خط اینترنت (PCC) در میکروتیک به همراه Failover",
            summary = "Configuring Per Connection Classifier (PCC) in firewall mangle for seamless dual-ISP bandwidth distribution.",
            summaryFa = "تقسیم هوشمند ترافیک کاربران بین دو اینترنت مختلف بر اساس سورس و مقصد با بازگشت خودکار در صورت قطعی.",
            content = "Per Connection Classifier (PCC) groups connections based on hashing source/destination IP addresses, ensuring HTTPS sessions remain on the same WAN while utilizing full bandwidth of both links.",
            categorySlug = "mikrotik",
            subcategory = "Bandwidth & Routing",
            vendor = "MikroTik",
            difficulty = "Advanced",
            tags = "PCC, Load Balance, Dual-WAN, Mangle, Failover, MikroTik",
            commandsJson = """[
                {"label": "Mark Connections via PCC in Mangle", "cmd": "/ip firewall mangle add chain=prerouting dst-address-type=!local in-interface=bridge per-connection-classifier=both-addresses:2/0 action=mark-connection new-connection-mark=WAN1_conn passthrough=yes\n/ip firewall mangle add chain=prerouting dst-address-type=!local in-interface=bridge per-connection-classifier=both-addresses:2/1 action=mark-connection new-connection-mark=WAN2_conn passthrough=yes"},
                {"label": "Mark Routing Tables based on Connection Marks", "cmd": "/ip firewall mangle add chain=prerouting in-interface=bridge connection-mark=WAN1_conn action=mark-routing new-routing-mark=to_WAN1\n/ip firewall mangle add chain=prerouting in-interface=bridge connection-mark=WAN2_conn action=mark-routing new-routing-mark=to_WAN2"},
                {"label": "Add Default Gateways for Marked Routing Tables", "cmd": "/ip route add dst-address=0.0.0.0/0 gateway=192.168.10.1 routing-table=to_WAN1 check-gateway=ping\n/ip route add dst-address=0.0.0.0/0 gateway=192.168.20.1 routing-table=to_WAN2 check-gateway=ping"}
            ]""",
            expectedResults = "Client connections split equally between WAN1 and WAN2, with immediate automatic switchover if one link fails.",
            verification = "/ip firewall mangle print stats"
        ),
        ArticleEntity(
            id = "art_fg_sdwan",
            title = "FortiGate SD-WAN SLA Performance Probes & Automated Path Steering",
            titleFa = "تنظیم SD-WAN هوشمند و تغییر خودکار لینک اینترنت بر اساس کیفیت پکت در فورتی‌گیت",
            summary = "Configuring Performance SLAs (Packet Loss, Latency, Jitter) and SD-WAN rules for priority traffic.",
            summaryFa = "ارزیابی دائم کیفیت خطوط اینترنت و هدایت ترافیک VoIP و دیتاسنتر به بهترین لینک با کمترین پکت‌لاس.",
            content = "FortiGate SD-WAN tests link health using continuous DNS/Ping probes against cloud targets (8.8.8.8, 1.1.1.1). When latency or loss exceeds defined thresholds, traffic dynamically steers to alternate WAN members without dropping active TCP connections.",
            categorySlug = "fortigate",
            subcategory = "SD-WAN",
            vendor = "Fortinet",
            difficulty = "Advanced",
            tags = "SD-WAN, SLA, Jitter, Packet Loss, Failover, FortiGate",
            commandsJson = """[
                {"label": "Configure SD-WAN Performance SLA", "cmd": "config system sdwan\n config health-check\n  edit \"Google_DNS_SLA\"\n   set server \"8.8.8.8\"\n   set members 1 2\n   config sla\n    edit 1\n     set latency-threshold 50\n     set jitter-threshold 10\n     set packetloss-threshold 2\n    next\n   end\n  next\n end\nend"},
                {"label": "Check Live SD-WAN Health Status via CLI", "cmd": "diagnose sys sdwan health-check"}
            ]""",
            expectedResults = "All active WAN members show status 'Alive' with packet loss %, latency, and jitter calculated in real time.",
            verification = "diagnose sys sdwan health-check"
        ),

        // ==========================================
        // WINDOWS TRICKS & PRO HACKS (EXPANDED TO PEAK)
        // ==========================================
        ArticleEntity(
            id = "art_win_sfc_dism",
            title = "Repair Corrupted Windows System Files & Component Store (SFC + DISM)",
            titleFa = "تعمیر کامل فایل‌های سیستمی و کرنل آسیب‌دیده با ترکیب SFC و DISM آنلاین",
            summary = "Dual-stage repair workflow utilizing DISM online image repair followed by SFC protected system file replacement.",
            summaryFa = "رفع ارورهای سیستمی، کرش برنامه‌ها و فایل‌های dll دست‌کاری شده با بازگردانی سورس سالم از سرور مایکروسافت.",
            content = "Running sfc /scannow alone often fails when the local component store (WinSxS) is itself corrupted. The enterprise gold standard is to repair the component store first with DISM /RestoreHealth, then let SFC regenerate system binaries.",
            categorySlug = "windows-tricks",
            subcategory = "System Integrity",
            vendor = "Microsoft Windows",
            difficulty = "Fundamental",
            tags = "SFC, DISM, RestoreHealth, Corruption, System32, DLL",
            commandsJson = """[
                {"label": "Check Component Store Health", "cmd": "DISM /Online /Cleanup-Image /CheckHealth"},
                {"label": "Repair Component Store from Windows Update", "cmd": "DISM /Online /Cleanup-Image /RestoreHealth"},
                {"label": "Scan and Replace Corrupted System Files", "cmd": "sfc /scannow"}
            ]""",
            expectedResults = "Windows Resource Protection found corrupt files and successfully repaired them.",
            verification = "findstr /c:\"[SR]\" %windir%\\logs\\cbs\\cbs.log"
        ),
        ArticleEntity(
            id = "art_win_mtu",
            title = "Optimize Network MTU & Subinterface Packet Sizes (Zero Fragmentation)",
            titleFa = "بهینه‌سازی MTU کارت شبکه و رفع پکت‌لاس ناشی از قطعه‌قطعه شدن (Fragmentation)",
            summary = "Measuring the exact Maximum Transmission Unit with ping sweep and setting persistent subinterface MTU values.",
            summaryFa = "حل مشکل لود نشدن برخی وب‌سایت‌ها و کندی شدید تونل‌های VPN با تنظیم دقیق مقدار MTU کارت شبکه.",
            content = "When network packets exceed the Path MTU (especially over PPPoE or IPsec tunnels with encryption headers), packets fragment or drop silently. Tuning the Windows interface MTU guarantees 100% wire-speed transmission.",
            categorySlug = "windows-tricks",
            subcategory = "Network Tuning",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "MTU, Ping, Fragmentation, Netsh, Subinterface, VPN",
            commandsJson = """[
                {"label": "Query Subinterface Index and Current MTU", "cmd": "netsh interface ipv4 show subinterfaces"},
                {"label": "Test MTU Boundary with Don't Fragment Flag", "cmd": "ping 8.8.8.8 -f -l 1472"},
                {"label": "Set Persistent Interface MTU (e.g. 1492 for PPPoE)", "cmd": "netsh interface ipv4 set subinterface \"Ethernet\" mtu=1492 store=persistent"}
            ]""",
            expectedResults = "Ping packets pass without 'Packet needs to be fragmented but DF set'. Throughput stabilizes.",
            verification = "netsh interface ipv4 show subinterfaces"
        ),
        ArticleEntity(
            id = "art_win_robocopy",
            title = "High-Speed Enterprise Multi-Threaded File Replication with Robocopy",
            titleFa = "انتقال فوق‌سریع اطلاعات و بکاپ‌گیری مولتی‌ترد با کامند پیشرفته Robocopy",
            summary = "Mirroring terabytes of enterprise file shares with ACL permissions, NTFS timestamps, and 16 concurrent worker threads.",
            summaryFa = "کپی فایل‌های حجیم و اشتراک‌های سازمانی بدون افت سرعت و با حفظ کامل سطوح دسترسی امنیتی (NTFS Permissions).",
            content = "Standard Windows Explorer file copy is single-threaded and locks on long filenames or open locks. Robocopy (Robust File Copy) with /MT:16 and /MIR replicates files at maximum hardware I/O saturation.",
            categorySlug = "windows-tricks",
            subcategory = "Storage & Backup",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "Robocopy, Multi-Thread, Mirror, NTFS, ACL, Backup",
            commandsJson = """[
                {"label": "High-Speed Multi-Thread Mirror with NTFS Security", "cmd": "robocopy \"D:\\Shares\\Data\" \"\\\\192.168.1.10\\Backup\" /MIR /MT:16 /COPY:DATS /R:2 /W:3 /NP /LOG:\"C:\\robocopy.log\""},
                {"label": "Monitor Active Backup Log in Real-time", "cmd": "Get-Content \"C:\\robocopy.log\" -Wait -Tail 20"}
            ]""",
            expectedResults = "Total files copied and mirrored with zero permission errors and detailed performance summary.",
            verification = "type C:\\robocopy.log | findstr /C:\"Total\" /C:\"Dirs\" /C:\"Files\""
        ),
        ArticleEntity(
            id = "art_win_battery",
            title = "Generate Deep Battery Diagnostics & PowerCfg Health Telemetry",
            titleFa = "تولید گزارش جامع سلامت باتری، ظرفیت و چرخه‌های شارژ لپ‌تاپ‌های سازمانی",
            summary = "Exporting interactive HTML telemetry detailing factory design capacity vs current full charge capacity.",
            summaryFa = "تشخیص فرسودگی باتری، بررسی سیکل‌های شارژ و یافتن نرم‌افزارهای عامل تخلیه باتری در کمتر از چند ثانیه.",
            content = "Enterprise laptops often suffer battery degradation that is invisible in standard battery meters. Windows contains a built-in diagnostic engine that creates a micro-auditing report of battery chemistry.",
            categorySlug = "windows-tricks",
            subcategory = "Hardware Telemetry",
            vendor = "Microsoft Windows",
            difficulty = "Fundamental",
            tags = "Battery, Powercfg, Capacity, Laptop, Health, Energy",
            commandsJson = """[
                {"label": "Generate Interactive Battery Health HTML Report", "cmd": "powercfg /batteryreport /output \"%USERPROFILE%\\Desktop\\BatteryReport.html\""},
                {"label": "Run 60-Second System Energy Efficiency Audit", "cmd": "powercfg /energy /output \"%USERPROFILE%\\Desktop\\EnergyReport.html\""},
                {"label": "Open Report in Default Browser", "cmd": "start \"\" \"%USERPROFILE%\\Desktop\\BatteryReport.html\""}
            ]""",
            expectedResults = "HTML report opens showing Design Capacity, Full Charge Capacity, and Cycle Count.",
            verification = "dir \"%USERPROFILE%\\Desktop\\*Report.html\""
        ),
        ArticleEntity(
            id = "art_win_symlinks",
            title = "Create Directory Junctions & NTFS Symbolic Links (mklink)",
            titleFa = "هدایت فضا و انتقال پوشه‌های سنگین به درایو دیگر با دایرکتوری جانکشن (mklink /J)",
            summary = "Redirecting space-heavy application directories (AppData, iTunes, Docker, VMs) to secondary drives without breaking software paths.",
            summaryFa = "آزادکردن ده‌ها گیگابایت از درایو C با انتقال پوشه‌ها به درایو D و ایجاد لینک سخت شفاف برای نرم‌افزارها.",
            content = "NTFS Directory Junctions allow creating a transparent pointer at the filesystem driver level. Programs continue reading from C:\\... while the actual bytes reside on D:\\, completely invisible to the operating software.",
            categorySlug = "windows-tricks",
            subcategory = "Storage Optimization",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "mklink, Junction, Symlink, NTFS, AppData, Storage",
            commandsJson = """[
                {"label": "Move Data Directory to Secondary Drive", "cmd": "robocopy \"C:\\HeavyFolder\" \"D:\\HeavyFolder\" /MOVE /E"},
                {"label": "Create Transparent NTFS Directory Junction", "cmd": "mklink /J \"C:\\HeavyFolder\" \"D:\\HeavyFolder\""},
                {"label": "Verify Junction Target Path", "cmd": "dir /AL \"C:\\\""}
            ]""",
            expectedResults = "Directory Junction created. Files written to C:\\HeavyFolder instantly store on D:\\HeavyFolder.",
            verification = "dir /AL \"C:\\HeavyFolder\""
        ),
        ArticleEntity(
            id = "art_win_sandbox",
            title = "Windows Sandbox: Instant Disposable VM for Safe Malware & Script Triage",
            titleFa = "راه‌اندازی سندباکس ویندوز برای تست امن فایل‌های مشکوک، بدافزار و اسکریپت‌ها",
            summary = "Enabling hardware-isolated disposable lightweight Windows desktop that discards all state upon closure.",
            summaryFa = "محیط مجازی موقت و ایزوله که با بستن پنجره آن، تمام فایل‌ها، تغییرات و بدافزارها برای همیشه پاک می‌شوند.",
            content = "Windows Sandbox provides a pristine, isolated Hyper-V container utilizing the host kernel with zero persistent disk storage. Perfect for testing suspicious email attachments, untrusted software, or PowerShell scripts safely.",
            categorySlug = "windows-tricks",
            subcategory = "Security & Isolation",
            vendor = "Microsoft Windows",
            difficulty = "Fundamental",
            tags = "Sandbox, Isolation, Security, Malware, Disposable, Hyper-V",
            commandsJson = """[
                {"label": "Enable Windows Sandbox Feature via PowerShell", "cmd": "Enable-WindowsOptionalFeature -Online -FeatureName \"Containers-DisposableClientVM\" -All"},
                {"label": "Launch Windows Sandbox Instantly", "cmd": "WindowsSandbox.exe"}
            ]""",
            expectedResults = "Windows Sandbox opens in 10 seconds. All state purges automatically on window close.",
            verification = "Get-WindowsOptionalFeature -Online -FeatureName \"Containers-DisposableClientVM\""
        ),
        ArticleEntity(
            id = "art_win_dnsflush",
            title = "Purge DNS Resolver & NetBIOS Name Cache (Instant Domain Refresh)",
            titleFa = "تخلیه کامل کش دی‌ان‌اس و نت‌بایوس جهت مشاهده تغییرات فوری رکوردهای سرور",
            summary = "Clearing local resolver sockets, flushing cached NXDOMAIN responses, and re-registering Active Directory DNS names.",
            summaryFa = "رفع ارور خطای باز شدن سایت یا سرور بعد از تغییر IP رکورد با تخلیه کش DNS کلاینت.",
            content = "Windows caches negative (NXDOMAIN) and outdated DNS records locally. When a server IP changes, the client continues connecting to old IPs until the TTL expires unless cleared with this pipeline.",
            categorySlug = "windows-tricks",
            subcategory = "Name Resolution",
            vendor = "Microsoft Windows",
            difficulty = "Fundamental",
            tags = "DNS, FlushDNS, NetBIOS, Nbtstat, RegisterDNS, IPConfig",
            commandsJson = """[
                {"label": "Flush DNS Resolver Cache", "cmd": "ipconfig /flushdns"},
                {"label": "Purge and Reload NetBIOS Remote Name Cache", "cmd": "nbtstat -R"},
                {"label": "Release and Refresh NetBIOS Names with WINS", "cmd": "nbtstat -RR"},
                {"label": "Initiate Dynamic Registration of DNS Names & IP", "cmd": "ipconfig /registerdns"}
            ]""",
            expectedResults = "Successfully flushed the DNS Resolver Cache. Registration of the DNS resource records initiated.",
            verification = "ipconfig /displaydns"
        ),
        ArticleEntity(
            id = "art_win_securechannel",
            title = "Repair Broken Active Directory Domain Trust (Secure Channel Reset)",
            titleFa = "تعمیر قطعی ارتباط اعتماد کلاینت با دامین کنترلر (Trust Relationship Failure)",
            summary = "Repairing 'The trust relationship between this workstation and the primary domain failed' without unjoining domain.",
            summaryFa = "رفع خطای عدم پذیرش پسورد دامین و قطعی ارتباط با اکتیو دایرکتوری بدون نیاز به خروج و ورود مجدد به دامین.",
            content = "When a workstation password machine account becomes out-of-sync with Active Directory Domain Controllers, users cannot sign in. Using PowerShell Reset-ComputerMachinePassword or Test-ComputerSecureChannel repairs the channel in place.",
            categorySlug = "windows-tricks",
            subcategory = "Active Directory",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "Domain, Trust, Secure Channel, Active Directory, PowerShell",
            commandsJson = """[
                {"label": "Test Domain Controller Secure Channel Status", "cmd": "Test-ComputerSecureChannel -Verbose"},
                {"label": "Repair Machine Account Trust with Domain Admin Auth", "cmd": "Test-ComputerSecureChannel -Repair -Credential (Get-Credential)"},
                {"label": "Force Local Kerberos Machine Password Reset", "cmd": "Reset-ComputerMachinePassword -Server <DC_HOSTNAME> -Credential (Get-Credential)"}
            ]""",
            expectedResults = "Test-ComputerSecureChannel returns True. Domain authentication restored immediately without reboot.",
            verification = "Test-ComputerSecureChannel"
        ),

        // ==========================================
        // HELP DESK COMPREHENSIVE GUIDES
        // ==========================================
        ArticleEntity(
            id = "art_hd_outlook_ost",
            title = "Repair Corrupted Outlook Mail Profiles & Rebuild OST Data Files",
            titleFa = "حل خطای خرابی دیتابیس Outlook و ساخت مجدد فایل داده آفلاین OST",
            summary = "Resolving 'Cannot start Microsoft Outlook', hung profile loading, and synchronizing fresh Exchange/IMAP mailbox cache.",
            summaryFa = "رفع فریز شدن اوت‌لوک هنگام باز شدن با حذف فایل خراب OST و دانلود مجدد دیتابیس ایمیل از سرور.",
            content = "Exchange and IMAP store cached mailbox items in %LOCALAPPDATA%\\Microsoft\\Outlook\\<email>.ost. Hard reboots or large mailboxes corrupt this file. Safely renaming the .ost forces Outlook to recreate a pristine database without data loss.",
            categorySlug = "help-desk",
            subcategory = "Email & Messaging",
            vendor = "Microsoft Outlook",
            difficulty = "Fundamental",
            tags = "Outlook, OST, Mail Profile, Exchange, Corrupt, Fix",
            commandsJson = """[
                {"label": "Terminate Stuck Outlook Processes", "cmd": "taskkill /F /IM OUTLOOK.EXE"},
                {"label": "Launch Outlook in Isolated Safe Mode", "cmd": "outlook.exe /safe"},
                {"label": "Locate and Rename Corrupted OST Database", "cmd": "ren \"%LOCALAPPDATA%\\Microsoft\\Outlook\\*.ost\" \"corrupt.ost.old\""},
                {"label": "Reset Outlook Navigation Pane and Views", "cmd": "outlook.exe /resetnavpane"}
            ]""",
            expectedResults = "Outlook starts smoothly, generates a fresh OST file, and begins downloading mail folders from Exchange.",
            verification = "dir \"%LOCALAPPDATA%\\Microsoft\\Outlook\\*.ost\""
        ),
        ArticleEntity(
            id = "art_hd_time_skew",
            title = "Fix Active Directory Kerberos Time Skew & Time Sync Failure",
            titleFa = "حل خطای ساعت کلاینت با سرور و عدم پذیرش تیکت Kerberos در شبکه دامین",
            summary = "Resynchronizing workstation clocks with the PDC Emulator to resolve the 5-minute Kerberos ticket validity window.",
            summaryFa = "تنظیم ساعت سیستم با سرور دامین برای رفع خطای Time Skew که مانع ورود کاربران و لاگین به شیرینگ‌ها می‌شود.",
            content = "Kerberos protocol strictly limits clock drift to 5 minutes (default). If a client CMOS battery dies or time drifts, all domain logins and shared folder access fail with authentication errors.",
            categorySlug = "help-desk",
            subcategory = "Domain Services",
            vendor = "Microsoft Windows",
            difficulty = "Fundamental",
            tags = "NTP, Time Skew, Kerberos, W32tm, Domain Controller, Clock",
            commandsJson = """[
                {"label": "Check Current Time Source and Offset", "cmd": "w32tm /query /status"},
                {"label": "Configure Client to Sync from Domain Hierarchy", "cmd": "w32tm /config /syncfromflags:domhier /update"},
                {"label": "Force Immediate Re-synchronization with Domain Controller", "cmd": "w32tm /resync /rediscover"},
                {"label": "Restart Windows Time Service", "cmd": "net stop w32time && net start w32time"}
            ]""",
            expectedResults = "Source points to Domain Controller, precision and stratum reflect healthy sync, clock drift drops to <0.05s.",
            verification = "w32tm /query /status"
        ),
        ArticleEntity(
            id = "art_hd_bitlocker",
            title = "BitLocker Drive Encryption Recovery Key Retrieval & TPM Verification",
            titleFa = "استخراج کلید ریکاوری ۴۸ رقمی بیت‌لاکر و تست سلامت ماژول TPM مادربورد",
            summary = "Querying numerical password protectors, testing TPM hardware availability, and backing up keys to Active Directory.",
            summaryFa = "دریافت کلید ریکاوری درایو C جهت باز کردن سیستم قفل‌شده و جلوگیری از فرمت شدن اطلاعات مهم کاربر.",
            content = "When BIOS updates or secure boot changes trigger the BitLocker Recovery screen, IT support must supply the 48-digit numerical password. Using manage-bde commands extracts keys before system changes.",
            categorySlug = "help-desk",
            subcategory = "Security & Encryption",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "BitLocker, Recovery Key, TPM, Encryption, manage-bde",
            commandsJson = """[
                {"label": "Extract 48-Digit Numerical BitLocker Recovery Key", "cmd": "manage-bde -protectors -get C:"},
                {"label": "Verify TPM Hardware Security Status", "cmd": "tpm.msc"},
                {"label": "Backup Recovery Key to Active Directory Domain", "cmd": "manage-bde -protectors -adbackup C: -id {PROTECTOR-GUID}"}
            ]""",
            expectedResults = "Numerical Password displayed with 48 digits divided in 8 groups of 6 numbers.",
            verification = "manage-bde -status C:"
        ),
        ArticleEntity(
            id = "art_hd_proxy_reset",
            title = "Reset WinHTTP Proxy & System Proxy PAC Autodiscovery",
            titleFa = "ریست پروکسی سیستم و رفع خطای قطعی اینترنت در اثر برنامه‌های فیلترشکن",
            summary = "Eliminating leftover proxy bindings in system sockets and resetting WinINet / WinHTTP to direct access.",
            summaryFa = "رفع خطای 'Proxy server is not responding' بعد از قطع شدن ناگهانی فیلترشکن‌ها و نرم‌افزارهای واسط.",
            content = "VPN and proxy clients frequently leave 127.0.0.1:8080 proxy configurations in registry and WinHTTP settings after crashes. Standard browser resets do not clear system-level WinHTTP sockets.",
            categorySlug = "help-desk",
            subcategory = "Internet Connectivity",
            vendor = "Microsoft Windows",
            difficulty = "Fundamental",
            tags = "Proxy, WinHTTP, PAC, 127.0.0.1, Reset, Network",
            commandsJson = """[
                {"label": "Check Current System WinHTTP Proxy Setting", "cmd": "netsh winhttp show proxy"},
                {"label": "Reset WinHTTP to Direct Connection (No Proxy)", "cmd": "netsh winhttp reset proxy"},
                {"label": "Disable User-Level Internet Proxy in Registry", "cmd": "reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 0 /f"}
            ]""",
            expectedResults = "Direct access (no proxy server). Client web connectivity restores instantly.",
            verification = "netsh winhttp show proxy"
        ),

        // ==========================================
        // IT TRAINING: CISCO, MIKROTIK, FORTINET, VMWARE, WINDOWS SERVER
        // ==========================================
        ArticleEntity(
            id = "art_train_wireguard",
            title = "MikroTik WireGuard High-Performance Site-to-Site VPN Tunnel",
            titleFa = "راه‌اندازی تونل پرسرعت وایرگارد (WireGuard) بین دو شعبه در میکروتیک RouterOS v7",
            summary = "Configuring UDP-based modern WireGuard crypto interfaces, public/private keys, peers, and static routes.",
            summaryFa = "اتصال پایدار و با کمترین تاخیر بین دفاتر و کارخانه‌ها با پروتکل مدرن وایرگارد در RouterOS v7.",
            content = "WireGuard replaces resource-heavy IPsec and OpenVPN with state-of-the-art cryptography (ChaCha20, Curve25519) in the Linux kernel space. It delivers up to 4x higher throughput on budget MikroTik hardware.",
            categorySlug = "mikrotik",
            subcategory = "VPN & Tunnels",
            vendor = "MikroTik",
            difficulty = "Advanced",
            tags = "WireGuard, VPN, MikroTik, RouterOS v7, Cryptography, Tunnel",
            commandsJson = """[
                {"label": "Create WireGuard Interface on Branch Router", "cmd": "/interface wireguard add name=wg-headquarters listen-port=13231"},
                {"label": "Assign Private Tunnel IP Address", "cmd": "/ip address add address=10.200.0.2/30 interface=wg-headquarters"},
                {"label": "Add Central Site as WireGuard Peer", "cmd": "/interface wireguard peers add interface=wg-headquarters public-key=\"<HEADQUARTERS_PUBLIC_KEY>\" endpoint-address=\"hq.company.com\" endpoint-port=13231 allowed-address=10.200.0.0/30,192.168.1.0/24 persistent-keepalive=25s"},
                {"label": "Add Static Route to Remote LAN via Tunnel", "cmd": "/ip route add dst-address=192.168.1.0/24 gateway=10.200.0.1"}
            ]""",
            expectedResults = "Interface status shows RX/TX bytes incrementing. Ping to remote 192.168.1.x hosts succeeds with minimal latency.",
            verification = "/interface wireguard print\n/interface wireguard peers print stats"
        ),
        ArticleEntity(
            id = "art_train_forti_ipsec",
            title = "FortiGate Site-to-Site IPsec VPN with IKEv2 & Dead Peer Detection (DPD)",
            titleFa = "پیکربندی تونل امن IPsec VPN بین فایروال فورتی‌گیت و شعبه با IKEv2 و DPD",
            summary = "Phase 1 / Phase 2 crypto proposals, interface binding, firewall policies, and static routing over VPN.",
            summaryFa = "اتصال امن دیتاسنتر و شعب با رمزنگاری AES-256 و فعال‌سازی مکانیزم تشخیص سریع قطعی لینک (DPD).",
            content = "IPsec VPN tunnels establish end-to-end encrypted transport across the public internet. Utilizing IKEv2 accelerates rekeying and handles asymmetric routing effectively.",
            categorySlug = "fortigate",
            subcategory = "IPsec VPN",
            vendor = "Fortinet",
            difficulty = "Advanced",
            tags = "IPsec, IKEv2, FortiGate, VPN, Phase 1, Phase 2, Firewall",
            commandsJson = """[
                {"label": "Configure Phase 1 Interface via CLI", "cmd": "config vpn ipsec phase1-interface\n edit \"VPN_Branch\"\n  set interface \"wan1\"\n  set ike-version 2\n  set peertype any\n  set net-device disable\n  set proposal aes256-sha256\n  set remote-gw 198.51.100.20\n  set psksecret MySecretKey2026\n  set dpd on-idle\n next\nend"},
                {"label": "Configure Phase 2 Proposal", "cmd": "config vpn ipsec phase2-interface\n edit \"VPN_Branch_P2\"\n  set phase1name \"VPN_Branch\"\n  set proposal aes256-sha256\n  set src-subnet 192.168.1.0 255.255.255.0\n  set dst-subnet 192.168.50.0 255.255.255.0\n next\nend"},
                {"label": "Check Live IPsec Tunnel State in Real-Time", "cmd": "diagnose vpn ike gateway list"}
            ]""",
            expectedResults = "Phase 1 and Phase 2 status show 'ESTABLISHED' with SA established flags.",
            verification = "diagnose vpn tunnel list"
        ),
        ArticleEntity(
            id = "art_train_cisco_ospf",
            title = "Cisco OSPF Multi-Area Design & Dead Interval Optimization",
            titleFa = "طراحی OSPF چندمنطقه‌ای (Multi-Area) و تنظیم تایمرهای Dead برای همگرایی سریع",
            summary = "Configuring Area Border Routers (ABRs), Area 0 backbone, route summarization, and sub-second convergence.",
            summaryFa = "تقسیم شبکه به چند Area در OSPF برای کاهش لود پردازشی روترها و کاهش زمان Down شدن به زیر ۲ ثانیه.",
            content = "In large campus networks, running all routers in a single OSPF Area 0 leads to excessive SPF recalculations on link flaps. Designing hierarchical areas with ABRs isolates flooding domains.",
            categorySlug = "ccna",
            subcategory = "Dynamic Routing",
            vendor = "Cisco Systems",
            difficulty = "Intermediate",
            tags = "OSPF, Area 0, ABR, Convergence, Dead Interval, Cisco",
            commandsJson = """[
                {"label": "Configure OSPF Process and Area Membership", "cmd": "router ospf 1\n router-id 1.1.1.1\n network 192.168.10.0 0.0.0.255 area 0\n network 192.168.20.0 0.0.0.255 area 1\n passive-interface default\n no passive-interface GigabitEthernet0/0"},
                {"label": "Tune Hello and Dead Timers for Fast Failover", "cmd": "interface GigabitEthernet0/0\n ip ospf hello-interval 1\n ip ospf dead-interval 4"},
                {"label": "Verify OSPF Neighbor Adjacencies", "cmd": "show ip ospf neighbor"}
            ]""",
            expectedResults = "Neighbor state transitions to 'FULL/DR' or 'FULL/BDR' with immediate sub-second link failure detection.",
            verification = "show ip ospf neighbor\nshow ip route ospf"
        ),
        ArticleEntity(
            id = "art_train_vmware_vmotion",
            title = "VMware vSphere ESXi vMotion & Distributed Switch VLAN Portgroups",
            titleFa = "پیکربندی vMotion و پورت‌گروه‌های سوئیچ توزیع‌شده (vDS) برای مهاجرت زنده ماشین‌های مجازی",
            summary = "Configuring VMkernel interfaces with vMotion TCP/IP stacks, 802.1Q VLAN trunking, and zero-downtime VM migration.",
            summaryFa = "مهاجرت زنده سرورهای ERP بدون حتی یک پکت قطعی با راه‌اندازی شبکه مجزای vMotion در کلاستر vCenter.",
            content = "Live migration (vMotion) transfers active VM memory state over the network while the virtual CPU continues executing. Setting up dedicated VMkernel adapters on 10Gbps uplinks with jumbo frames prevents migration timeouts.",
            categorySlug = "windows-server",
            subcategory = "Virtualization",
            vendor = "VMware Broadcom",
            difficulty = "Advanced",
            tags = "vSphere, ESXi, vMotion, vCenter, VLAN, Distributed Switch",
            commandsJson = """[
                {"label": "Check VMkernel Interface Connectivity over vMotion Stack", "cmd": "vmkping -I vmk1 -s 1472 -d 192.168.50.25"},
                {"label": "Verify ESXi Physical NIC Uplink Speeds", "cmd": "esxcli network nic list"},
                {"label": "Check Active vSwitch VLAN Portgroup ID", "cmd": "esxcli network vswitch standard portgroup list"}
            ]""",
            expectedResults = "vmkping returns 0% packet loss and vCenter vMotion migration wizard completes at 100%.",
            verification = "esxcli network vswitch standard list"
        ),
        ArticleEntity(
            id = "art_train_windows_ad",
            title = "Windows Server 2022/2025 AD DS Disaster Recovery & FSMO Role Transfer",
            titleFa = "بازیابی بحران اکتیو دایرکتوری و انتقال فوری رول‌های FSMO با کامند ntdsutil",
            summary = "Seizing Flexible Single Master Operation (FSMO) roles when the Primary Domain Controller crashes permanently.",
            summaryFa = "مدیریت بحران سوختن سرور دامین کنترلر اصلی و ارتقای سرور بکاپ به عنوان سرور جامع دامین بدون از دست رفتن کاربران.",
            content = "Active Directory relies on 5 FSMO roles: Schema Master, Domain Naming Master, PDC Emulator, RID Master, and Infrastructure Master. If the PDC dies, seizing roles via ntdsutil restores domain functionality.",
            categorySlug = "windows-server",
            subcategory = "Active Directory Disaster Recovery",
            vendor = "Microsoft Windows Server",
            difficulty = "Advanced",
            tags = "FSMO, ntdsutil, AD DS, Disaster Recovery, Active Directory, PDC",
            commandsJson = """[
                {"label": "Query Current FSMO Role Holders across Forest", "cmd": "netdom query fsmo"},
                {"label": "Seize PDC Emulator Role via PowerShell", "cmd": "Move-ADDirectoryServerOperationMasterRole -Identity <BACKUP_DC> -OperationMasterRole PDCEmulator,RIDMaster -Force"},
                {"label": "Clean Up Dead Domain Controller Metadata via ntdsutil", "cmd": "ntdsutil\nmetadata cleanup\nconnections\nconnect to server localhost\nquit\nselect operation target\nlist sites\nquit\nquit"}
            ]""",
            expectedResults = "netdom query fsmo reports all 5 roles held by the operational survivor Domain Controller.",
            verification = "netdom query fsmo"
        ),
        ArticleEntity(
            id = "art_win_portproxy",
            title = "Native Windows Port Forwarding & NAT with netsh interface portproxy",
            titleFa = "هدایت پورت و فورواردینگ ترافیک در ویندوز بدون نرم‌افزار اضافی با netsh portproxy",
            summary = "Redirecting incoming traffic on any port to another internal IP or WSL container transparently.",
            summaryFa = "انتقال ترافیک پورت‌های ورودی به آی‌پی یا پورت دیگر در شبکه محلی یا کانتینرهای لینوکسی در سطح ویندوز.",
            content = "Windows includes a native Layer-4 TCP portproxy within the netsh networking subsystem. It allows administrators to route incoming requests on port 8080 to an internal database or web server on 192.168.1.50:80 without installing third-party proxies.",
            categorySlug = "windows-tricks",
            subcategory = "Network Redirection",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "Netsh, Portproxy, Port Forwarding, NAT, TCP, Firewall",
            commandsJson = """[
                {"label": "Add Port Forwarding Rule (Listen on 8080 -> Forward to 192.168.1.50:80)", "cmd": "netsh interface portproxy add v4tov4 listenport=8080 listenaddress=0.0.0.0 connectport=80 connectaddress=192.168.1.50"},
                {"label": "Display All Active Port Proxy Forwarding Rules", "cmd": "netsh interface portproxy show all"},
                {"label": "Allow Port in Windows Firewall", "cmd": "netsh advfirewall firewall add rule name=\"PortProxy 8080\" dir=in action=allow protocol=TCP localport=8080"},
                {"label": "Delete Port Proxy Rule", "cmd": "netsh interface portproxy delete v4tov4 listenport=8080 listenaddress=0.0.0.0"}
            ]""",
            warnings = "Ensure the Windows 'IP Helper' service (iphlpsvc) is running, as netsh portproxy relies on it.",
            expectedResults = "Incoming TCP connections on port 8080 seamlessly reach target destination 192.168.1.50:80.",
            verification = "netstat -ano | findstr :8080"
        ),
        ArticleEntity(
            id = "art_win_godmode",
            title = "Activate Windows God Mode & All-in-One Master Administrative Panel",
            titleFa = "فعال‌سازی پنل مدیریت کل (God Mode) و دسترسی به تمام ابزارهای ادمین ویندوز در یک پوشه",
            summary = "Creating a unified management console with all 200+ Control Panel tools, MMC snap-ins, and security settings.",
            summaryFa = "ایجاد پوشه جادویی God Mode با دسترسی آنی به بیش از ۲۰۰ ابزار عیب‌یابی، مدیریت سرویس‌ها و تنظیمات مخفی ویندوز.",
            content = "Windows contains a hidden master GUID namespace {ED7BA470-8E54-465E-825C-99712043E01C} that compiles all configuration applets, device manager tools, security policies, and power settings into a single searchable interface.",
            categorySlug = "windows-tricks",
            subcategory = "Administration",
            vendor = "Microsoft Windows",
            difficulty = "Fundamental",
            tags = "God Mode, Master Control, Admin Tools, Shortcuts, System Administration",
            commandsJson = """[
                {"label": "Create God Mode Folder via CMD", "cmd": "mkdir \"%USERPROFILE%\\Desktop\\GodMode.{ED7BA470-8E54-465E-825C-99712043E01C}\""},
                {"label": "Create God Mode Folder via PowerShell", "cmd": "New-Item -ItemType Directory -Path \"$([Environment]::GetFolderPath('Desktop'))\\GodMode.{ED7BA470-8E54-465E-825C-99712043E01C}\""}
            ]""",
            expectedResults = "A special Control Panel icon appears on Desktop containing over 200 sorted administration tools.",
            verification = "dir \"%USERPROFILE%\\Desktop\\GodMode.*\""
        ),
        ArticleEntity(
            id = "art_win_psremoting",
            title = "Multi-Host Remote Management via WinRM & PowerShell Remoting",
            titleFa = "مدیریت ریموت و همزمان کلاینت‌های تحت شبکه با WinRM و دستورات Invoke-Command",
            summary = "Executing commands, querying hardware, and pushing software configurations across 50 computers concurrently.",
            summaryFa = "اجرای همزمان اسکریپت روی صدها کامپیوتر تحت شبکه دامین یا ورک‌گروپ در کمتر از چند ثانیه از طریق پروتکل WinRM.",
            content = "PowerShell Remoting uses Windows Remote Management (WinRM) over WS-Management protocol (ports 5985/5986). It allows system administrators to manage remote computers headless without opening GUI Remote Desktop.",
            categorySlug = "windows-tricks",
            subcategory = "Remote Management",
            vendor = "Microsoft Windows",
            difficulty = "Intermediate",
            tags = "PowerShell, WinRM, Enter-PSSession, Invoke-Command, Remote Administration",
            commandsJson = """[
                {"label": "Enable PS Remoting on Target Machine (Run as Admin)", "cmd": "Enable-PSRemoting -Force -SkipNetworkProfileCheck"},
                {"label": "Interactive Remote Shell into Client Computer", "cmd": "Enter-PSSession -ComputerName \"192.168.1.100\" -Credential (Get-Credential)"},
                {"label": "Execute Command on Multiple Remote Hosts in Parallel", "cmd": "Invoke-Command -ComputerName (Get-Content hosts.txt) -ScriptBlock { Get-Service Spooler | Restart-Service }"}
            ]""",
            warnings = "In workgroup environments without Active Directory, configure TrustedHosts using: Set-Item WSMan:\\localhost\\Client\\TrustedHosts -Value '*' -Force.",
            expectedResults = "Commands run concurrently on all target nodes and return structured objects with ComputerName property.",
            verification = "Test-WSMan -ComputerName \"192.168.1.100\""
        ),
        ArticleEntity(
            id = "art_net_subnet_mastery",
            title = "IPv4 Subnetting Math, Magic Number & High-Speed Network Planning",
            titleFa = "راهنمای جامع محاسبات ذهنی ساب‌نتینگ، فرمول عدد جادویی و طراحی رنج آی‌پی",
            summary = "Mastering subnet mask calculations, broadcast boundaries, usable host formulas, and CIDR prefix math.",
            summaryFa = "آموزش گام‌به‌گام و فرمول‌های سریع محاسبه آی‌پی شبکه، برودکست، هاست‌های قابل استفاده و طراحی ساب‌نت در سناریوهای واقعی.",
            content = "Subnetting allows dividing a contiguous IP address block into smaller network segments for security, performance, and broadcast domain isolation. The Magic Number method (256 minus the interesting octet value) provides the interval step for every subnet block.",
            categorySlug = "ccna",
            subcategory = "IP Addressing & Subnetting",
            vendor = "Cisco / IETF",
            difficulty = "Fundamental",
            tags = "Subnetting, CIDR, Magic Number, IPv4, Broadcast, Network ID, VLSM",
            commandsJson = """[
                {"label": "Magic Number Formula", "cmd": "Step = 256 - SubnetMaskOctet\nExample: /26 mask 255.255.255.192 -> Step = 256 - 192 = 64\nSubnets: 0, 64, 128, 192"},
                {"label": "Usable Host Formula", "cmd": "Usable Hosts = (2 ^ HostBits) - 2\nExample: /27 has 5 host bits -> (2 ^ 5) - 2 = 30 usable hosts"},
                {"label": "Cisco CLI Subnet Verification", "cmd": "show ip route\nshow ip interface brief"}
            ]""",
            expectedResults = "Engineers can quickly determine Network Address, Broadcast Address, First Usable, and Last Usable IP in seconds.",
            verification = "Use the built-in NetOps Hub Subnet Calculator in Tools section."
        ),
        ArticleEntity(
            id = "art_net_vlsm_design",
            title = "Variable Length Subnet Masking (VLSM) Architectural Hierarchy",
            titleFa = "طراحی معماری آدرس‌دهی سلسله‌مراتبی با ماسک با طول متغیر (VLSM)",
            summary = "Optimizing IPv4 allocations for disparate department sizes without wasting address space.",
            summaryFa = "تخصیص هوشمندانه بلاک‌های ساب‌نت از بزرگ‌ترین نیاز تا کوچک‌ترین لینک‌های نظیربه‌نظیر Point-to-Point (/30 و /31).",
            content = "Variable Length Subnet Masking (VLSM) allows multiple subnet masks to exist within the same classful address space. The golden rule of VLSM design is to always order requirement sizes from largest to smallest, avoiding overlapping addresses.",
            categorySlug = "ccna",
            subcategory = "Subnet Engineering",
            vendor = "Cisco CCNA",
            difficulty = "Intermediate",
            tags = "VLSM, Subnetting, IPv4, Optimization, Point-to-Point, CIDR",
            commandsJson = """[
                {"label": "Order Requirements Largest to Smallest", "cmd": "1. HQ Users: 100 hosts -> /25 (126 usable)\n2. Branch 1: 50 hosts -> /26 (62 usable)\n3. Branch 2: 25 hosts -> /27 (30 usable)\n4. WAN Link: 2 hosts -> /30 (2 usable)"},
                {"label": "Configure Subnet on Cisco Router", "cmd": "interface GigabitEthernet0/0/0\n ip address 172.16.0.1 255.255.255.128\n no shutdown"}
            ]""",
            expectedResults = "Zero IP address wastage with clean summarized routing tables.",
            verification = "show ip route connected"
        )
    )

    // Initial Default Users for Local & Offline Operation
    // v2.0.1 SECURITY: no plaintext/default passwords are shipped in the APK anymore.
    // Seeded accounts have an empty local hash and MUST complete their first
    // online login (server-issued password) before offline unlock is possible.
    // The old backdoor passwords ("Creator@NetOps2026", "Admin@NetOps2026",
    // "123456", ...) are fully removed.
    val defaultUsers = listOf(
        UserEntity("u_creator", "creator", "Creator (System Owner)", "creator@netopshub.internal", "Creator", true, "", mustChangePassword = false),
        UserEntity("u_admin", "admin", "Lead Infrastructure Admin", "admin@netopshub.internal", "Admin", true, "", mustChangePassword = true),
        UserEntity("u_editor", "editor", "Network Operations Editor", "editor@netopshub.internal", "Editor", true, "", mustChangePassword = true),
        UserEntity("u_viewer", "viewer", "NOC Telemetry Viewer", "viewer@netopshub.internal", "Viewer", true, "", mustChangePassword = true)
    )

    // Sample contacts - replace with real data via Excel import (Creator/Admin)
    val phonebookEntries = listOf(
        PhoneEntryEntity("ph_sample_1", "Network", "Operations Center", "", "Central NOC", "1001", "Sample entry - 24/7 NOC hotline"),
        PhoneEntryEntity("ph_sample_2", "IT", "Helpdesk", "", "Jahan HQ", "1002", "Sample entry - internal helpdesk"),
        PhoneEntryEntity("ph_sample_3", "Security", "Team", "", "Ardakan DC", "1003", "Sample entry - security team"),
        PhoneEntryEntity("ph_sample_4", "Datacenter", "Reception", "", "Sangan", "1004", "Sample entry - site reception")
    )

    // Standard & Specialized TCP/UDP Ports Reference (Section 24)
    val ports = listOf(
        PortEntity("p_20", 20, "TCP", "FTP-Data", "File Transfer Protocol (Data channel)", "Transferring files during active FTP", "Plaintext protocol"),
        PortEntity("p_21", 21, "TCP", "FTP-Control", "File Transfer Protocol (Control channel)", "FTP login and command session", "Credentials sent unencrypted"),
        PortEntity("p_22", 22, "TCP", "SSH / SFTP", "Secure Shell & SFTP", "Secure remote administration and encrypted transfer", "Industry standard for Linux / Network CLI"),
        PortEntity("p_23", 23, "TCP", "Telnet", "Telnet protocol", "Legacy unencrypted CLI terminal", "Insecure: replace with SSH"),
        PortEntity("p_25", 25, "TCP", "SMTP", "Simple Mail Transfer Protocol", "Server-to-server email relay", "Unauthenticated relay vulnerability if misconfigured"),
        PortEntity("p_53", 53, "UDP/TCP", "DNS", "Domain Name System", "Translating domain names to IP addresses", "UDP for queries <512B; TCP for zone transfers and large responses"),
        PortEntity("p_67", 67, "UDP", "DHCP Server", "Dynamic Host Configuration Protocol (Server)", "Listening for DHCP Discover/Request from clients", "Critical infrastructure service"),
        PortEntity("p_68", 68, "UDP", "DHCP Client", "Dynamic Host Configuration Protocol (Client)", "Receiving DHCP Offer/ACK on clients", "Local network broadcast/unicast"),
        PortEntity("p_80", 80, "TCP", "HTTP", "Hypertext Transfer Protocol", "Unencrypted World Wide Web traffic", "Cleartext; should redirect to HTTPS 443"),
        PortEntity("p_88", 88, "TCP/UDP", "Kerberos", "Active Directory Kerberos Authentication", "Ticket granting and authentication in Windows Domain", "Core AD dependency"),
        PortEntity("p_110", 110, "TCP", "POP3", "Post Office Protocol v3", "Email retrieval by clients", "Use POP3S port 995 for encryption"),
        PortEntity("p_123", 123, "UDP", "NTP", "Network Time Protocol", "Time synchronization for servers and switches", "Accurate time critical for Kerberos and TLS certificates"),
        PortEntity("p_137", 137, "UDP", "NetBIOS-NS", "NetBIOS Name Service", "Windows legacy local computer name resolution", "Do not expose to WAN"),
        PortEntity("p_138", 138, "UDP", "NetBIOS-DGM", "NetBIOS Datagram Service", "Windows legacy file browsing broadcast", "Internal LAN only"),
        PortEntity("p_139", 139, "TCP", "NetBIOS-SSN", "NetBIOS Session Service", "Windows legacy NetBIOS file sharing", "Internal LAN only"),
        PortEntity("p_143", 143, "TCP", "IMAP", "Internet Message Access Protocol", "Email client inbox sync", "Use IMAPS port 993"),
        PortEntity("p_161", 161, "UDP", "SNMP", "Simple Network Management Protocol", "Network monitoring (PRTG, Zabbix, LibreNMS)", "Use SNMPv3 with auth and privacy encryption"),
        PortEntity("p_162", 162, "UDP", "SNMP-Trap", "SNMP Trap Receiver", "Alert messages sent from switches/routers to NMS", "UDP traps"),
        PortEntity("p_389", 389, "TCP/UDP", "LDAP", "Lightweight Directory Access Protocol", "Querying Active Directory objects and users", "Cleartext; prefer LDAPS port 636"),
        PortEntity("p_443", 443, "TCP", "HTTPS", "HTTP over TLS/SSL", "Encrypted secure web communication", "Standard for secure web portals and APIs"),
        PortEntity("p_445", 445, "TCP", "SMB over IP", "Server Message Block (Windows File Sharing)", "Direct host Windows file shares, IPC$, printers", "Block at perimeter firewall (WannaCry vector)"),
        PortEntity("p_500", 500, "UDP", "ISAKMP / IKE", "Internet Key Exchange (IPsec VPN)", "Phase 1 negotiation for IPsec VPN tunnels", "Essential for IPsec VPN"),
        PortEntity("p_636", 636, "TCP", "LDAPS", "LDAP over SSL/TLS", "Encrypted Active Directory directory queries", "Requires trusted CA certificate"),
        PortEntity("p_993", 993, "TCP", "IMAPS", "IMAP over SSL/TLS", "Secure email retrieval", "Encrypted IMAP"),
        PortEntity("p_995", 995, "TCP", "POP3S", "POP3 over SSL/TLS", "Secure email retrieval", "Encrypted POP3"),
        PortEntity("p_1433", 1433, "TCP", "MS-SQL", "Microsoft SQL Server", "Database queries for ERP systems (Rahkaran, Borhan)", "Protect with network ACLs"),
        PortEntity("p_1521", 1521, "TCP", "Oracle DB", "Oracle Database Listener", "Enterprise Oracle DB communication", "Internal secure segment"),
        PortEntity("p_3306", 3306, "TCP", "MySQL", "MySQL / MariaDB Database", "Database engine for web apps and monitoring", "Bind to localhost/private IP"),
        PortEntity("p_3389", 3389, "TCP/UDP", "RDP", "Remote Desktop Protocol", "Remote Windows Server / PC desktop control", "Never expose directly to public internet without VPN"),
        PortEntity("p_5432", 5432, "TCP", "PostgreSQL", "PostgreSQL Database Server", "Open-source relational database", "Production data storage"),
        PortEntity("p_5900", 5900, "TCP", "VNC", "Virtual Network Computing", "Remote GUI desktop screen sharing", "Unencrypted in legacy implementations"),
        PortEntity("p_7300", 7300, "TCP", "Rahkaran Ghaem", "Rahkaran ERP (Ghaem Profile)", "Internal company ERP service port", "Application port"),
        PortEntity("p_7302", 7302, "TCP", "Rahkaran Jahan", "Rahkaran ERP (Jahan Profile)", "Internal company ERP service port", "Application port"),
        PortEntity("p_7303", 7303, "TCP", "Rahkaran Folad", "Rahkaran ERP (Folad Kousha)", "Internal company ERP service port", "Application port"),
        PortEntity("p_7304", 7304, "TCP", "Rahkaran Kansaran", "Rahkaran ERP (Kansaran)", "Internal company ERP service port", "Application port"),
        PortEntity("p_7305", 7305, "TCP", "Rahkaran Sardkhaneh", "Rahkaran ERP (Sardkhaneh)", "Internal company ERP service port", "Application port"),
        PortEntity("p_7306", 7306, "TCP", "Rahkaran Zob", "Rahkaran ERP (Zob Omid)", "Internal company ERP service port", "Application port"),
        PortEntity("p_7308", 7308, "TCP", "Rahkaran Touka/Loleh", "Rahkaran ERP (Toukasir & Loleh Mazandaran)", "Internal company ERP service port", "Application port"),
        PortEntity("p_7309", 7309, "TCP", "Rahkaran Sangan", "Rahkaran ERP (Zob Sangan)", "Internal company ERP service port", "Application port"),
        PortEntity("p_7310", 7310, "TCP", "Rahkaran Parand", "Rahkaran ERP (Forge Parand)", "Internal company ERP service port", "Application port"),
        PortEntity("p_7311", 7311, "TCP", "Rahkaran Felezi", "Rahkaran ERP (Sanaye Felezi)", "Internal company ERP service port", "Application port"),
        PortEntity("p_7312", 7312, "TCP", "Rahkaran Ardakan", "Rahkaran ERP (Folad Ardakan)", "Internal company ERP service port", "Application port"),
        PortEntity("p_8080", 8080, "TCP", "HTTP-Alt", "Alternative HTTP / Proxy", "Web proxy, Apache Tomcat, dev web servers", "Common secondary web port"),
        PortEntity("p_8443", 8443, "TCP", "HTTPS-Alt", "Alternative HTTPS", "Secure admin consoles (vCenter, Tomcat)", "Encrypted alternative")
    )

    // HTTP Status Codes Database (Section 28)
    val httpErrors = listOf(
        HttpErrorEntity("http_200", 200, "2xx", "OK", "Standard response for successful HTTP requests.", "Normal healthy response.", "No action needed.", "GET /api/v1/health -> 200 OK"),
        HttpErrorEntity("http_201", 201, "2xx", "Created", "The request succeeded and a new resource was created.", "Resource successfully generated on server.", "Use Location header or response body.", "POST /api/v1/notes -> 201 Created"),
        HttpErrorEntity("http_301", 301, "3xx", "Moved Permanently", "The requested resource has been assigned a new permanent URI.", "URL restructuring or HTTP to HTTPS redirect.", "Update bookmark or client URL.", "http://example.com -> 301 -> https://example.com"),
        HttpErrorEntity("http_400", 400, "4xx", "Bad Request", "The server cannot process the request due to client error (malformed syntax).", "Invalid JSON, missing required headers, illegal characters.", "Inspect request payload and schema.", "Invalid JSON in POST body"),
        HttpErrorEntity("http_401", 401, "4xx", "Unauthorized", "The request lacks valid authentication credentials.", "Missing Bearer token, expired session, wrong password.", "Re-authenticate or refresh session token.", "Authorization: Bearer <expired_token>"),
        HttpErrorEntity("http_403", 403, "4xx", "Forbidden", "The server understands the request but refuses to authorize it.", "Authenticated user lacks permission (Viewer trying to delete server).", "Verify user role and RBAC policy.", "Viewer accessing Admin endpoint"),
        HttpErrorEntity("http_404", 404, "4xx", "Not Found", "The requested resource could not be found on the server.", "Typo in endpoint URL, deleted server record, broken link.", "Verify URL path and resource ID.", "GET /api/v1/servers/nonexistent_id"),
        HttpErrorEntity("http_408", 408, "4xx", "Request Timeout", "The server timed out waiting for the client request.", "Slow client connection, high network latency.", "Check client uplink and retry.", "Client took >60s to send body"),
        HttpErrorEntity("http_409", 409, "4xx", "Conflict", "The request conflicts with current server state.", "Optimistic lock version mismatch, duplicate unique field.", "Fetch latest state and merge changes.", "Editing server modified by another user"),
        HttpErrorEntity("http_429", 429, "4xx", "Too Many Requests", "The user has sent too many requests in a given amount of time (Rate Limited).", "Exceeded rate limit (brute force protection, API quota).", "Back off and retry after the Retry-After header period.", "100 login attempts in 1 minute"),
        HttpErrorEntity("http_500", 500, "5xx", "Internal Server Error", "An unhandled exception occurred on the server.", "Server application crash, unhandled runtime error.", "Check server worker/service logs.", "Uncaught exception in backend code"),
        HttpErrorEntity("http_502", 502, "5xx", "Bad Gateway", "The server received an invalid response from upstream gateway/backend.", "Upstream server down, reverse proxy configuration issue.", "Verify backend microservice status.", "Nginx reverse proxy cannot reach upstream"),
        HttpErrorEntity("http_503", 503, "5xx", "Service Unavailable", "The server is currently unable to handle the request due to maintenance or overload.", "Server maintenance, CPU exhaustion, D1 connection limit.", "Retry after temporary load subsides.", "Database undergoing maintenance"),
        HttpErrorEntity("http_504", 504, "5xx", "Gateway Timeout", "The gateway did not receive a timely response from upstream.", "Upstream database or service hung or slow query.", "Optimize queries and check database indexes.", "Query execution timed out")
    )

    // Wireshark Filters Catalog (Section 30)
    val wiresharkFilters = listOf(
        WiresharkEntity("ws_01", "ip.addr == 192.168.1.10", "Filter by Specific IP", "Matches any packet where either source or destination IP equals 192.168.1.10.", "Isolate all traffic involving a specific server or workstation.", "ip.addr == 192.168.1.10", "Tracking server conversation during connectivity issues."),
        WiresharkEntity("ws_02", "ip.src == 192.168.1.10", "Filter by Source IP Only", "Matches packets originating from 192.168.1.10.", "Analyze traffic generated by an individual host.", "ip.src == 192.168.50.8", "Detecting unauthorized outbound traffic from an ERP server."),
        WiresharkEntity("ws_03", "ip.dst == 192.168.1.10", "Filter by Destination IP Only", "Matches packets destined for 192.168.1.10.", "Analyze incoming requests to a specific host.", "ip.dst == 192.168.1.2", "Auditing requests arriving at vCenter."),
        WiresharkEntity("ws_04", "tcp.port == 443", "Filter HTTPS Traffic", "Displays all TCP segments using port 443.", "Analyze encrypted web and API transactions.", "tcp.port == 443", "Inspecting TLS handshakes and certificate exchanges."),
        WiresharkEntity("ws_05", "udp.port == 53", "Filter DNS Queries and Responses", "Isolates DNS name resolution traffic over UDP.", "Debug DNS resolution delays and NXDOMAIN responses.", "udp.port == 53", "Investigating slow name resolution or failed lookups."),
        WiresharkEntity("ws_06", "tcp.port == 3389", "Filter RDP Session Traffic", "Displays Remote Desktop connections.", "Diagnose remote desktop connection failures.", "tcp.port == 3389", "Verifying whether RDP TCP SYN packets reach the server."),
        WiresharkEntity("ws_07", "icmp", "Filter ICMP Ping & Error Messages", "Displays echo requests, echo replies, and destination unreachable messages.", "Verify ping reachability and MTU path discovery.", "icmp", "Checking for 'Destination Unreachable (Fragmentation Needed)'."),
        WiresharkEntity("ws_08", "arp", "Filter Address Resolution Protocol", "Displays ARP requests (Who has?) and ARP replies (is at).", "Troubleshoot duplicate IPs and Layer 2 broadcast storms.", "arp.duplicate-address-detected", "Detecting ARP spoofing or IP address collisions on LAN."),
        WiresharkEntity("ws_09", "tcp.flags.syn == 1 && tcp.flags.ack == 0", "Capture New TCP Connection Attempts", "Isolates the initial SYN packet initiating a 3-way handshake.", "Detect port scans and monitor connection initiation rates.", "tcp.flags.syn == 1 && tcp.flags.ack == 0", "Detecting SYN flood attacks or slow port scan attempts."),
        WiresharkEntity("ws_10", "tcp.flags.reset == 1", "Filter TCP RST (Reset) Packets", "Displays aborted or abruptly terminated TCP connections.", "Identify connections rejected by firewalls or closed ports.", "tcp.flags.reset == 1", "Finding why an ERP connection was forcefully aborted."),
        WiresharkEntity("ws_11", "tcp.analysis.retransmission", "Detect TCP Retransmissions", "Highlights packets re-sent due to unacknowledged delivery.", "Identify packet loss, faulty cabling, or severe buffer congestion.", "tcp.analysis.retransmission", "Proving packet loss on inter-site VPN tunnel."),
        WiresharkEntity("ws_12", "tcp.analysis.duplicate_ack", "Detect Duplicate ACKs", "Highlights duplicate ACKs that trigger fast retransmission.", "Pinpoint network congestion and out-of-order delivery.", "tcp.analysis.duplicate_ack", "Analyzing TCP throughput degradation across WAN links.")
    )
}
