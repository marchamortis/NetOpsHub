#!/usr/bin/env bash
set -e

MSG="${1:-Update NetOps Hub and trigger automated multi-platform release}"

# 1. Stage and commit changes
git add -A
if ! git diff-index --quiet HEAD --; then
    git commit -m "$MSG"
fi

# 2. Determine release tag version (v2.0.0 per user request)
NEXT_TAG="${2:-v2.0.1}"

echo "Creating release tag $NEXT_TAG..."
git tag -d "$NEXT_TAG" 2>/dev/null || true
git tag -a "$NEXT_TAG" -m "Release $NEXT_TAG - NetOps Hub Version 2.0.1"
git push origin main
git push origin "$NEXT_TAG" --force

# 3. Ensure fresh build and prepare release artifacts
echo "Preparing release binaries..."
gradle assembleDebug --no-daemon

mkdir -p /tmp/release_assets
cp app/build/outputs/apk/debug/app-debug.apk /tmp/release_assets/NetOpsHub-Android.apk

# Windows WSA bundle
mkdir -p /tmp/win
cp windows/* /tmp/win/ 2>/dev/null || true
cp /tmp/release_assets/NetOpsHub-Android.apk /tmp/win/NetOpsHub.apk
cd /tmp/win && zip -r /tmp/release_assets/NetOpsHub-Windows-WSA.zip . && cd /app/applet

# iOS guide bundle
mkdir -p /tmp/ios_b
cp ios/* /tmp/ios_b/ 2>/dev/null || true
cd /tmp/ios_b && zip -r /tmp/release_assets/NetOpsHub-iOS-Web-Guide.zip . && cd /app/applet

# Full project source code
zip -r /tmp/release_assets/NetOpsHub-Full-Source.zip . -x ".git/*" "app/build/*" ".gradle/*" "release-artifacts/*" "/tmp/*" "NetOpsHub.apk" "NetOpsHub-SourceCode.zip" ".build-outputs/*"

# 4. Upload binaries directly to GitHub Release via API
python3 /app/applet/scripts/upload_assets.py "$NEXT_TAG"

echo "Release $NEXT_TAG published successfully with all download binaries!"
