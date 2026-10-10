; ============================================================
; NetOps Hub v2.0.1 - Windows Desktop Installer (NSIS)
; Bundles: Android APK + WSA install helper + native launcher
; Produces: NetOpsHub-Windows-Setup.exe
; ============================================================

!define APPNAME "NetOps Hub"
!define COMPANYNAME "NetOps"
!define VERSION "2.0.1"

Name "${APPNAME} ${VERSION}"
OutFile "NetOpsHub-Windows-Setup.exe"
InstallDir "$LOCALAPPDATA\NetOpsHub"
RequestExecutionLevel user
SetCompressor /SOLID lzma

; Modern UI
!include "MUI2.nsh"
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"

!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "Persian"

Section "Install"
  SetOutPath "$INSTDIR"

  ; Payload (files are placed next to this .nsi at build time)
  File "NetOpsHub.apk"
  File "install-wsa.bat"
  File "README-WINDOWS.md"

  ; Desktop + Start Menu shortcuts pointing to the WSA install helper
  CreateShortCut "$DESKTOP\NetOps Hub.lnk" "$INSTDIR\install-wsa.bat" "" "$INSTDIR\install-wsa.bat" 0
  CreateDirectory "$SMPROGRAMS\NetOps Hub"
  CreateShortCut "$SMPROGRAMS\NetOps Hub\NetOps Hub.lnk" "$INSTDIR\install-wsa.bat" "" "$INSTDIR\install-wsa.bat" 0
  CreateShortCut "$SMPROGRAMS\NetOps Hub\Uninstall NetOps Hub.lnk" "$INSTDIR\uninstall.exe"

  ; Uninstaller
  WriteUninstaller "$INSTDIR\uninstall.exe"

  ; Registry for Add/Remove Programs
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\NetOpsHub" \
    "DisplayName" "${APPNAME} ${VERSION}"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\NetOpsHub" \
    "UninstallString" "$INSTDIR\uninstall.exe"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\NetOpsHub" \
    "DisplayVersion" "${VERSION}"
SectionEnd

Section "Uninstall"
  Delete "$INSTDIR\NetOpsHub.apk"
  Delete "$INSTDIR\install-wsa.bat"
  Delete "$INSTDIR\README-WINDOWS.md"
  Delete "$INSTDIR\uninstall.exe"
  RMDir "$INSTDIR"
  Delete "$DESKTOP\NetOps Hub.lnk"
  RMDir /r "$SMPROGRAMS\NetOps Hub"
  DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\NetOpsHub"
SectionEnd
