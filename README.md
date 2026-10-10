# 🌐 NetOps Hub — Enterprise NOC & IT Operations Platform

[![Android Build](https://img.shields.io/badge/Platform-Android%20%7C%20Windows%20%7C%20iOS-blue.svg)](https://github.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Cloudflare D1](https://img.shields.io/badge/Backend-Cloudflare%20D1%20%26%20Workers-F38020.svg)](https://workers.cloudflare.com)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Latest Release](https://img.shields.io/badge/Release-Automated%20CI%2FCD-success.svg)](https://github.com)

**NetOps Hub** is a professional, high-performance IT Operations and Network Engineering platform designed for Network Administrators, NOC Engineers, and IT Specialists. Built natively using modern **Kotlin**, **Jetpack Compose Material 3**, local **Room Database**, and real-time cloud synchronization via **Cloudflare D1 & Workers**.

---

## 📥 دانلود و دریافت آخرین نسخه‌ها (Download & Releases)

تمامی نسخه‌های جدید با هر تغییر به صورت خودکار توسط **GitHub Actions** بیلد شده و در بخش [Releases](../../releases) قرار می‌گیرند:

| پلتفرم | فایل قابل دانلود | راهنمای اجرا |
| :--- | :--- | :--- |
| **📱 اندروید (Android)** | `NetOpsHub-Android.apk` | نصب مستقیم روی کلیه گوشی‌ها و تبلت‌های اندرویدی (Android 8.0+) |
| **💻 ویندوز (Windows 10/11)** | `NetOpsHub-Windows-WSA.zip` | اجرای بومی با Windows Subsystem for Android (WSA) یا شبیه‌سازها (شامل اسکریپت `install-wsa.bat`) |
| **🍏 آیفون و آیپد (iOS)** | `NetOpsHub-iOS-Web-Guide.zip` | نصب به عنوان وب‌اپلیکیشن مستقل (PWA) از طریق Safari با کلیک روی Add to Home Screen |
| **📦 کل سورس‌کد پروژه** | `NetOpsHub-Full-Source.zip` | سورس‌کد کامل پروژه همراه کدهای کاتلین، گریدل و اسکریپت‌های مایگریشن کلودفلر |

---

## 🛡 سطوح دسترسی کاربران (Role-Based Access Control - RBAC)

سیستم مجهز به تفکیک دسترسی سازمانی مبتنی بر ۴ سطح نقش است. به دلایل امنیتی، رمزهای عبور در مخزن عمومی قرار نمی‌گیرند و توسط سازنده یا مدیر ارشد سیستم در بخش مدیریت کاربران اختصاص داده می‌شوند:

| نقش (Role) | شناسه نقش | سطح دسترسی و اختیارات |
| :--- | :--- | :--- |
| **Creator** | `creator` | **سازنده و مالک کل سیستم:** دسترسی نامحدود به تمام بخش‌ها، ساخت کاربر با تمام نقش‌ها، ریست رمز عبور همکاران، همگام‌سازی کلودفلر |
| **Admin** | `admin` | **مدیر ارشد زیرساخت:** ساخت و مدیریت کاربران همکار (نقش‌های Admin, Editor, Viewer)، ثبت و ویرایش سرورها، مدیریت مقالات آکادمی |
| **Editor** | `editor` | **کارشناس عملیات:** ایجاد و ویرایش مقالات آکادمی شبکه، یادداشت‌برداری، استفاده از ابزارهای محاسباتی |
| **Viewer** | `viewer` | **مشاهده‌گر / مانیتورینگ:** دسترسی فقط‌خواندنی (Read-Only) به اطلاعات سرورها، پورت‌ها، ابزارها و مقالات بدون امکان حذف یا تغییر |

---

## 🌟 ویژگی‌های کلیدی پلتفرم (Key Features)

### ۱. زیرساخت شرکت و سرورها (Company Infrastructure)
- مانیتورینگ بیش از **۷۹ سرور اختصاصی** در دیتاسنترها و سایت‌های مختلف (Central, Site-A, Site-B, DR).
- ارتباط با سیستم‌های مجازی‌سازی **vCenter** و پورت‌های RDP / SSH.
- دسترسی و تفکیک ماژول‌های **ERP راهکاران سیستم** با آدرس‌های وب و پورت‌های سرویس.

### ۲. آکادمی تخصصی شبکه و ترفندهای ویندوز (Network Academy)
- بیش از **۳۵ مقاله تخصصی و راهنمای گام‌به‌گام** در حوزه‌های:
  - CCNA / CCNP Routing & Switching (OSPF, BGP, VLANs, 802.1Q, EtherChannel).
  - میکروتیک (MikroTik RouterOS v7, WireGuard VPN, Queues, Firewall).
  - ترفندهای پیشرفته هلپ‌دسک و ویندوز (PowerShell Networking, Netsh, RDP Shadowing, GPO Cache Fix, Sysinternals).
  - آنالیز بسته‌های شبکه با Wireshark و ترابل‌شوتینگ ترافیک.

### ۳. ابزارهای مهندسی شبکه (Network Engineering Tools)
- **ماشین‌حساب فوق‌پیشرفته ساب‌نتینگ (Subnet Calculator):** محاسبه آدرس شبکه، Broadcast، محدوده IPهای معتبر، Subnet Mask، تعداد هاست و نمایش باینری.
- **ماشین‌حساب VLSM (Variable Length Subnet Masking):** طراحی بهینه زیرشبکه‌ها بدون هدررفت آدرس.
- مرجع پورت‌های شبکه (Well-Known Ports) و کدهای خطای HTTP.
- ابزار فیلترنویسی وایبرشارک (Wireshark Display Filters).

### ۴. همگام‌سازی ابری لحظه‌ای با کلودفلر (Cloudflare D1 & Workers)
- متصل به **Cloudflare D1 Database** برای سینک اتوماتیک داده‌ها.
- هرگونه تغییر سرور یا مقاله توسط همکاران در بازه‌های ۲۰ ثانیه‌ای به صورت Real-Time برای تمام همکاران دیگر روی موبایل و سیستم به‌روزرسانی می‌شود.
- دارای مکانیزم کاملاً تاب‌آور در برابر قطعی اینترنت (Offline-First با Room Database).

---

## 🤖 پایپ‌لاین انتشار خودکار گیت‌هاب (GitHub Actions CI/CD)

در مسیر `.github/workflows/release.yml` یک پایپ‌لاین تمام‌اتوماتیک پیاده‌سازی شده است:
1. با هر دستور `git push` به شاخه `main` یا ایجاد Release Tag:
   - محیط ابری گابلیت اوبونتو بالا می‌آید.
   - گریدل پروژه را با جاوا ۲۱ کامپایل می‌کند.
   - فایل‌های نصبی `NetOpsHub-Android.apk` و بسته‌های کمکی ویندوز و iOS تولید می‌شوند.
   - به صورت خودکار یک **Release جدید** در گیت‌هاب ایجاد شده و فایل‌ها برای دانلود مستقیم کاربران در بخش Releases قرار می‌گیرد.

---

## 🏗 کامپایل و اجرای لوکال (Local Build)

```bash
# کلون کردن ریپازیتوری
git clone https://github.com/marchamortis/NetOpsHub.git
cd NetOpsHub

# کامپایل فایل نصبی APK
./gradlew assembleDebug

# اجرای تست‌های واحد و Robolectric
./gradlew testDebugUnitTest
```

---
*توسعه‌یافته با بالاترین استانداردهای معماری نرم‌افزار، طراحی مدرن Material 3 Glassmorphism و امنیت سازمانی.*
---

## 🆕 whats new in v2.0.1 (Security Hardening + Phone Book + In-App Updates)

### 🛡 سخت‌سازی امنیتی (Network Security Hardening)
- **حذف کامل توکن مستر (`cfat_*`)**: دورزدن احراز هویت از طریق توکن ثابت سازنده به‌کلی برداشته شد؛ هر درخواست ممتاز باید توکن سشن واقعی صادرشده توسط `/auth/login` را داشته باشد.
- **هش PBKDF2-SHA256 (۱۰۰هزار دور + Salt اختصاصی هر کاربر)**: هش‌های قدیمی SHA-256 در اولین ورود موفق به‌صورت شفاف ارتقا می‌یابند.
- **Rate Limiting ورود**: ۸ تلاش ناموفق در ۱۰ دقیقه = مسدودسازی موقت + ثبت Audit برای هر تلاش ناموفق.
- **احراز هویت روی همه Endpointها**: `/sync/all`، `/servers`، `/sites`، `/vcenters`، `/rahkaran` و مقالات آکادمی دیگر ناشناس در دسترس نیستند.
- **توکن‌های سشن فقط به‌صورت SHA-256 هش‌شده ذخیره می‌شوند**، با عمر ۲۴ ساعت و ابطال خودکار پس از تغییر رمز / تغییر نقش / غیرفعال‌سازی.
- **کلاینت**: رمزها فقط به‌صورت هش PBKDF2 در دیتابیس محلی (Room)؛ ذخیره‌سازی سشن با Android Keystore (AES-GCM)؛ حذف تمام رمزهای پشت‌بازو؛ لاگ HTTP فقط در بیلد Debug؛ `allowBackup=false`؛ R8 فعال.

### 👆 ورود بایومتریک امن (حفظ‌شده و ارتقا یافته)
- مکانیزم جدید **BiometricPrompt + CryptoObject** بر بستر Android Keystore
- فعال‌سازی بایومتریک بلافاصله پس از اولین ورود موفق با رمز؛ ابطال خودکار با تغییر بیومتریک دستگاه

### 🌐 به‌روزرسانی درون‌برنامه‌ای (OTA) — برنامه آنلاین
- با هر ورود، اپ نسخه جدید را از Worker چک می‌کند و **همان‌جا پیام نسخه جدید + دکمه به‌روزرسانی** نمایش می‌دهد (بدون نیاز به دانلود دستی از گیت‌هاب)
- محتوا (سرورها، مقالات، دفترچه تلفن) به‌صورت Real-Time از Cloudflare سینک می‌شود

### 📞 دفترچه تلفن سازمانی (ماژول جدید)
- فیلدها: **نام و نام خانوادگی | شماره تماس | نام شرکت | شماره داخلی**
- ورود گروهی از **فایل اکسل (.xlsx) یا CSV** با تشخیص خودکار ستون‌ها (فارسی/انگلیسی) توسط نقش‌های Creator و Admin + افزودن دستی مخاطب
- جستجوی سریع، فیلتر شرکت، تماس مستقیم و کپی شماره داخلی

### 🎓 آکادمی و هلپ‌دسک — نقشه‌های گرافیکی جدید (مطابق پوسترهای مرجع)
- ۶ نقشه تصویری جدید: Kerberos Deep-Dive، آرسنال کامندهای اکتیو دایرکتوری، ممیزی و دفاع AD، مقایسه OSPF و BGP، فازهای تونل IPsec، کامندهای شبکه ویندوز
- هلپ‌دسک: **حالت گرافیکی فلوشارت تصمیم‌گیری** برای ۶ سناریو (قطعی اینترنت، RDP، کندی شبکه، قفل اکانت AD، پرینتر، قطعی ESXi)

### 📥 دانلود نسخه 2.0.1
| پلتفرم | فایل |
| :--- | :--- |
| 📱 اندروید | `NetOpsHub-Android.apk` (از بخش Releases) |
| 💻 ویندوز (نصب مستقیم) | `NetOpsHub-Windows-Setup.exe` |
| 💻 ویندوز (بسته WSA) | `NetOpsHub-Windows-WSA.zip` |
| 📦 سورس کامل | `NetOpsHub-Full-Source.zip` |

> ⚠️ **پس از دیپلوی**: رمز عبور اکانت‌های Seed را از بخش مدیریت کاربران تعویض کنید و مایگریشن `0002` را روی D1 اجرا نمایید:
> `npx wrangler d1 execute netops-hub-d1 --file=./migrations/0002_security_phonebook_updates.sql --remote`

---

*توسعه‌یافته با بالاترین استانداردهای معماری نرم‌افزار، طراحی مدرن Material 3 Glassmorphism و امنیت سازمانی.*
