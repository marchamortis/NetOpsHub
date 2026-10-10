@echo off
title NetOps Hub - Windows Installation Helper
echo ======================================================================
echo             NetOps Hub - Windows Installation Helper (WSA / ADB)
echo ======================================================================
echo.

REM Check if ADB is installed in PATH or WSA default location
where adb >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [!] ADB not found in system PATH.
    echo [*] Checking common Android/WSA locations...
    if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
        set ADB="%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
    ) else if exist "C:\platform-tools\adb.exe" (
        set ADB="C:\platform-tools\adb.exe"
    ) else (
        echo.
        echo [ERROR] ADB is required to install APK on Windows Subsystem for Android (WSA) or BlueStacks.
        echo Please install Windows Subsystem for Android (WSA) with developer mode enabled,
        echo or install WSA-PacMan (https://github.com/Lana-Ns/nexus-pacman) to install NetOpsHub.apk with a single click.
        echo.
        pause
        exit /b 1
    )
) else (
    set ADB=adb
)

echo [*] Connecting to local Android runtime (127.0.0.1:58526)...
%ADB% connect 127.0.0.1:58526 >nul 2>nul

echo [*] Installing NetOpsHub.apk (version 2.0.1)...
REM Try running from the script's own folder so the bundled APK is found
cd /d "%~dp0"
%ADB% install -r NetOpsHub.apk
if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] NetOps Hub has been installed successfully on your Windows system!
    echo You can now search for 'NetOps Hub' in your Windows Start Menu.
) else (
    echo.
    echo [!] Installation failed or device not connected.
    echo Please make sure Windows Subsystem for Android (WSA) is running and Developer Mode is ON.
)
echo.
pause
