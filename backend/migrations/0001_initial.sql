-- Migration 0001: Initial schema and seed data
-- Contains authoritative company infrastructure from Vcenter.pdf and Link Rahkaran.pdf

-- Seed Users:
-- SECURITY (v2.0.1): credential values and password hashes are no longer
-- documented in this repository. Initial passwords are distributed by the
-- system owner out-of-band and must be rotated on first login
-- (must_change_password = 1). Legacy SHA-256 hashes are transparently
-- upgraded to PBKDF2-SHA256 by the Worker on next successful login.

INSERT OR IGNORE INTO users (id, username, display_name, email, password_hash, role) VALUES
('u_creator', 'creator', 'Creator (System Owner)', 'creator@netopshub.internal', 'f16d9e8a6319012b6804012dbbf2e17f90faa0a3e58aca97b26f44a6a77b5f1b', 'Creator'),
('u_admin', 'admin', 'Lead Administrator', 'admin@netopshub.internal', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Admin'),
('u_editor', 'editor', 'Network Editor', 'editor@netopshub.internal', '108b67eb01c238b1eb7dc3ba47efd90e29b10f8a846c483a99e82939b4b044d4', 'Editor'),
('u_viewer', 'viewer', 'NOC Viewer', 'viewer@netopshub.internal', 'd3a4369a4cc31cf04b77f98993f35c60fb98a96b797e8e580e66860d5696d5b9', 'Viewer');

-- Seed Sites from Vcenter.pdf
INSERT OR IGNORE INTO sites (id, name, description) VALUES
('site_ardakan', 'Ardakan', 'اردکان - دیتا سنتر و زیرساخت اردکان'),
('site_jahan', 'Jahan', 'جهان - مجتمع مرکزی جهان'),
('site_sardkhaneh', 'Sardkhaneh', 'سردخانه رباط کریم'),
('site_loleh', 'Loleh Mazandaran', 'لوله مازندران'),
('site_sangan', 'SANGAN', 'سنگان - ذوب فولاد سنگان'),
('site_kansaran', 'KANSARAN', 'کانساران معدن نادر'),
('site_touka', 'TOUKA', 'توکا - توکاسیر سپاهان'),
('site_felezi', 'Felezi', 'صنایع فلزی کوشا نوین'),
('site_jahan_factory', 'JAHAN FACTORI', 'کارخانه جهان'),
('site_jkousha', 'Jkousha', 'کوشا نوین');

-- Seed vCenters
INSERT OR IGNORE INTO vcenters (id, name, ip, site, description) VALUES
('vc_ardakan', 'ardakanV', '192.168.50.20', 'Ardakan', 'vCenter سرور اردکان - Host 50.15 / 50.16 / 50.17 / 50.24 / 50.14'),
('vc_jahan', 'Vcenter', '192.168.1.2', 'Jahan', 'vCenter سرور جهان - Host 1.16 / 1.17 / 1.24 / 1.130 / 1.200 / 1.204');

-- Seed Rahkaran Companies from Link Rahkaran.pdf
INSERT OR IGNORE INTO rahkaran_companies (id, row_no, name_fa, ip, port_app, port_web, url, service_name, notes) VALUES
('rk_1', 1, 'صنایع فولاد کوشا نوین', '192.168.1.202', '7303', '801', 'http://192.168.1.202:801/folad', 'راهکاران فولاد کوشا', 'Port Web: 801'),
('rk_2', 2, 'ذوب امید آریان', '192.168.1.203', '7306', '806', 'http://192.168.1.203:806/Zob_sg', 'راهکاران ذوب امید', 'Port Web: 806'),
('rk_3', 3, 'قائم پروفیل رازی', '192.168.1.22', '7300', '31033', 'http://192.168.1.22:31033/razi_sg', 'راهکاران قائم پروفیل', 'Port Web: 31033'),
('rk_4', 4, 'جهان پروفیل پارس', '192.168.1.8', '7302', '31039', 'http://jahanr:31039', 'راهکاران جهان پروفیل', 'Port Web: 31039'),
('rk_5', 5, 'توکاسیر سپاهان', '192.168.88.105', '7308', '31320', 'http://192.168.88.105:31320', 'راهکاران توکاسیر', 'Port Web: 31320'),
('rk_6', 6, 'کانساران معدن نادر', '192.168.12.104', '7304', '31030', 'http://192.168.12.104:31030', 'راهکاران کانساران', 'Port Web: 31030'),
('rk_7', 7, 'ذوب فولاد سنگان', '192.168.9.105', '7309', '31033', 'http://192.168.9.105:31033', 'راهکاران ذوب سنگان', 'Port Web: 31033'),
('rk_8', 8, 'لوله مازندران', '192.168.6.155', '7308', '3107', 'http://192.168.6.155:3107', 'راهکاران لوله مازندران', 'Port Web: 3107'),
('rk_9', 9, 'سردخانه رباط کریم', '192.168.18.19', '7305', '31031', 'http://192.168.18.19:31031/sg', 'راهکاران سردخانه', 'Port Web: 31031'),
('rk_10', 10, 'فورج پرند', '192.168.1.31', '7310', '31041', 'http://192.168.1.31:31041', 'راهکاران فورج پرند', 'Port Web: 31041'),
('rk_11', 11, 'صنایع فلزی کوشا نوین', '192.168.1.32', '7311', '31043', 'http://192.168.1.32:31043/Authentication', 'راهکاران صنایع فلزی', 'Port Web: 31043'),
('rk_12', 12, 'فولاد اردکان', '192.168.50.8', '7312', '31030', 'http://192.168.50.8:31030', 'راهکاران فولاد اردکان', 'Port Web: 31030');

-- Seed Academy Categories
INSERT OR IGNORE INTO academy_categories (id, slug, name, name_fa, icon, description, order_index) VALUES
('cat_helpdesk', 'help-desk', 'Help Desk', 'پشتیبانی فنی', 'SupportAgent', 'عیب‌یابی سیستمی، ویندوز، سرویس‌ها، پرینتر و شبکه کلاینت', 1),
('cat_netplus', 'network-plus', 'Network+', 'مفاهیم شبکه (Network+)', 'Lan', 'اصول بنیادی شبکه، مدل OSI، ساب‌نتینگ، کابل‌کشی و سوئیچینگ', 2),
('cat_ccna', 'ccna', 'CCNA', 'سیسکو CCNA', 'Router', 'پیکربندی سوئیچ‌ها و روترهای سیسکو، VLAN، OSPF، NAT و ACL', 3),
('cat_ccnp', 'ccnp', 'CCNP', 'سیسکو CCNP', 'Hub', 'مسیریابی پیشرفته، BGP، بازتوزیع روت، مهندسی ترافیک سازمانی', 4),
('cat_mikrotik', 'mikrotik', 'MikroTik RouterOS', 'میکروتیک', 'SettingsEthernet', 'تنظیمات روتربورد، فایروال، منگل، روتینگ، وایرگارد و پهنای باند', 5),
('cat_fortigate', 'fortigate', 'FortiGate', 'فورتی‌گیت', 'Security', 'پالیسی‌های فایروال، IPsec VPN، SSL VPN، SD-WAN و عیب‌یابی پکت', 6),
('cat_windows', 'windows-server', 'Windows Server & AD', 'ویندوز سرور و اکتیو دایرکتوری', 'Dns', 'مدیریت دامین کنترلر، DNS، DHCP، Group Policy و اشتراک فایل', 7),
('cat_fundamentals', 'fundamentals', 'Networking Fundamentals', 'پروتکل‌ها و پایه‌ها', 'CloudSync', 'مرجع پروتکل‌های اینترنت TCP, UDP, ARP, ICMP, DNS, RDP, SMB', 8),
('cat_tools', 'network-tools', 'Network Tools', 'ابزارهای تخصصی شبکه', 'Calculate', 'ماشین‌حساب‌های ساب‌نت، VLSM، وایلدکارد، IPv6 و مانیتورینگ', 9);
