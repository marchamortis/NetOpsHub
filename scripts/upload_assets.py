import urllib.request
import urllib.parse
import os
import sys
import json

TOKEN = os.environ.get("GITHUB_TOKEN", "")
OWNER = "marchamortis"
REPO = "NetOpsHub"

tag_name = sys.argv[1] if len(sys.argv) > 1 else "v2.0.1"

# 1. Fetch release ID for tag
url = f"https://api.github.com/repos/{OWNER}/{REPO}/releases/tags/{tag_name}"
req = urllib.request.Request(url)
req.add_header("Authorization", f"token {TOKEN}")
req.add_header("Accept", "application/vnd.github+json")

release_id = None
existing_assets = {}

try:
    with urllib.request.urlopen(req) as resp:
        rel_data = json.loads(resp.read().decode())
        release_id = rel_data["id"]
        for a in rel_data.get("assets", []):
            existing_assets[a["name"]] = a["id"]
except urllib.error.HTTPError as e:
    # Create release if doesn't exist
    create_url = f"https://api.github.com/repos/{OWNER}/{REPO}/releases"
    release_name = f"NetOps Hub Version 2.0.1 ({tag_name})"
    release_body = f"""## 🚀 NetOps Hub Version 2.0 ({tag_name})

Major multi-platform release with high-security authentication, Cloudflare real-time synchronization, and dedicated IT modules.

### ✨ What's New in Version 2.0:
- 🛡 **Clean & Hardened Enterprise Login**: Stripped role indicators and bypass buttons. Clean enterprise credential authentication.
- 🔐 **Mandatory Combination Password Reset**: Enforces strong combination passwords (min 8 characters, uppercase, lowercase, numbers, and symbols) on first login without recurring prompts.
- 👆 **Biometric Authentication**: Fast fingerprint and face unlock powered by AndroidX BiometricPrompt.
- 👥 **Enterprise User Management**: Creator and Admin roles can create and manage user accounts with instant Cloudflare D1 synchronization.
- 🌐 **Real-Time Cloudflare Sync**: Automated online synchronization for servers, infrastructure, knowledge base, and users via Cloudflare Worker and D1 database.
- 🧭 **4 Dedicated Core Banners**:
  1. **Company Information** (80 Servers, Sites, vCenter, Rahkaran ERP)
  2. **Network Academy** (Cyber Security Visual Maps, URL Anatomy, Hashing/Encoding, Active Directory 1-4, CCNA, CCNP)
  3. **Troubleshooting Helpdesk** (Interactive NOC triage, issue diagnostics, packet loss, DNS, DHCP, ESXi troubleshooting)
  4. **Engineering Tools** (Subnetting, VLSM, Bandwidth, Port reference, Wireshark filters)
- 🌍 **Full Bilingual Support (EN & FA)**: English by default with instant toggle to Persian across all interfaces.

### 📦 Downloads for All Platforms:
1. 📱 **Android**: `NetOpsHub-Android.apk` (Install directly on any Android phone or tablet)
2. 💻 **Windows**: `NetOpsHub-Windows-WSA.zip` (Run on Windows 11 via Windows Subsystem for Android with one-click installer)
3. 🍏 **iOS & Web**: `NetOpsHub-iOS-Web-Guide.zip` (PWA / Web deployment instructions and direct web access guide)
4. 💻 **Source Code**: `NetOpsHub-Full-Source.zip` (Complete repository source bundle)
"""
    create_payload = json.dumps({
        "tag_name": tag_name,
        "name": release_name,
        "body": release_body,
        "draft": False,
        "prerelease": False
    }).encode("utf-8")
    c_req = urllib.request.Request(create_url, data=create_payload, method="POST")
    c_req.add_header("Authorization", f"token {TOKEN}")
    c_req.add_header("Content-Type", "application/json")
    with urllib.request.urlopen(c_req) as resp:
        rel_data = json.loads(resp.read().decode())
        release_id = rel_data["id"]

print(f"Targeting Release {tag_name} (ID: {release_id})")

# 2. Upload assets
ARTIFACTS_DIR = "/tmp/release_assets"
files = [
    ("NetOpsHub-Android.apk", "application/vnd.android.package-archive"),
    ("NetOpsHub-Windows-WSA.zip", "application/zip"),
    ("NetOpsHub-iOS-Web-Guide.zip", "application/zip"),
    ("NetOpsHub-Full-Source.zip", "application/zip")
]

for filename, content_type in files:
    filepath = os.path.join(ARTIFACTS_DIR, filename)
    if not os.path.exists(filepath):
        continue
    
    # Delete existing asset with same name if already present
    if filename in existing_assets:
        try:
            del_url = f"https://api.github.com/repos/{OWNER}/{REPO}/releases/assets/{existing_assets[filename]}"
            d_req = urllib.request.Request(del_url, method="DELETE")
            d_req.add_header("Authorization", f"token {TOKEN}")
            with urllib.request.urlopen(d_req) as resp:
                print(f"Deleted old asset {filename}")
        except Exception as e:
            print(f"Notice deleting {filename}: {e}")

    encoded_name = urllib.parse.quote(filename)
    upload_url = f"https://uploads.github.com/repos/{OWNER}/{REPO}/releases/{release_id}/assets?name={encoded_name}"
    
    with open(filepath, "rb") as f:
        data = f.read()
    
    u_req = urllib.request.Request(upload_url, data=data, method="POST")
    u_req.add_header("Authorization", f"token {TOKEN}")
    u_req.add_header("Content-Type", content_type)
    
    try:
        with urllib.request.urlopen(u_req) as resp:
            print(f"Uploaded {filename} successfully ({len(data)} bytes).")
    except urllib.error.HTTPError as e:
        print(f"Asset {filename} upload notice: HTTP {e.code}")
    except Exception as e:
        print(f"Asset {filename} notice: {e}")

print(f"All assets ready for {tag_name}!")
