# ADB Kit

[中文版 README](README.zh_TW.md)

A minimal Android app that automatically sets `adb_enabled` on boot to bypass financial app debug detection.

## How It Works

Many financial/banking apps check `Settings.Global.ADB_ENABLED == 1` to detect USB debugging. By setting the value to **2**, these simple equality checks fail — the app thinks ADB is off, while Android still treats any non-zero value as enabled.

ADB Kit applies this setting automatically every time the device boots, so you never have to worry about it resetting.

## Features

- 🔄 **Auto-apply on boot** — `BroadcastReceiver` listens for `BOOT_COMPLETED`
- 🔧 **Configurable value** — Choose between `0`, `1`, or `2`
- 🎨 **Material 3 UI** — Native look with Dynamic Colors (Material You)
- 🌐 **i18n** — English & Traditional Chinese
- 🔒 **Direct Boot aware** — Applies before user unlock
- 📦 **Tiny** — No unnecessary dependencies

## Setup

### 1. Build

Requires Java 17 and Android SDK.

```bash
JAVA_HOME=/path/to/java17 \
ANDROID_HOME=/path/to/android-sdk \
./gradlew assembleDebug
```

### 2. Install

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 3. Grant Permission

`WRITE_SECURE_SETTINGS` cannot be granted through normal installation. You must grant it via ADB (one-time):

```bash
adb shell pm grant dev.e88e89.adbkit android.permission.WRITE_SECURE_SETTINGS
```

### 4. Launch Once

Open the app at least once so Android will deliver `BOOT_COMPLETED` broadcasts to it:

```bash
adb shell am start dev.e88e89.adbkit/.MainActivity
```

## Usage

1. Open the app
2. Select the desired `adb_enabled` value:
   - **0** — Disable ADB
   - **1** — Enable ADB (standard)
   - **2** — Bypass financial app detection (ADB still works)
3. Tap **Apply Now** to apply immediately, or just leave it — the value will be applied on every boot
4. Tap **Check** to verify the current permission and setting status

## How the Bypass Works

| `adb_enabled` | ADB Functional | Financial App Detection |
|:-:|:-:|:-:|
| 0 | ❌ No | ✅ Passes (ADB off) |
| 1 | ✅ Yes | ❌ Detected |
| 2 | ✅ Yes | ✅ Passes (≠ 1) |

Most financial apps use a simple check like:

```java
Settings.Global.getInt(contentResolver, "adb_enabled") == 1
```

Setting the value to `2` makes this check return `false`, while Android internally treats it as truthy (non-zero = enabled).

## Project Structure

```
app/src/main/
├── AndroidManifest.xml
├── java/dev/e88e89/adbkit/
│   ├── MainActivity.kt      # UI + settings management
│   └── BootReceiver.kt      # Applies setting on boot
├── res/layout/
│   └── activity_main.xml    # Material 3 layout
├── res/values/
│   ├── strings.xml           # English strings
│   └── themes.xml            # Material 3 theme
└── res/values-zh-rTW/
    └── strings.xml           # Traditional Chinese strings
```

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).
