# HDB

[中文版 README](README.zh_TW.md)

A minimal, ultra-lightweight (~61 KB) Android app that easily manages adb_enabled to bypass financial app debug detection.

## How It Works

Many financial/banking apps check Settings.Global.ADB_ENABLED == 1 to detect USB debugging. By setting the value to 2, these simple equality checks fail — the app thinks ADB is off, while Android still treats any non-zero value as enabled.

HDB provides a quick and native way to manage this setting.

## Features

- Quick Settings Tile: Toggle ADB directly from the quick settings panel
- Configurable value: Choose between 0, 1, or 2
- Pure Native UI: No heavy Material or AppCompat dependencies, reducing APK size to ~61 KB
- i18n: English and Traditional Chinese

## Setup

### 1. Build

Requires Java 17 and Android SDK.

```bash
./gradlew assembleRelease
```

### 2. Install

```bash
adb install app/build/outputs/apk/release/app-release.apk
```

### 3. Grant Permission

WRITE_SECURE_SETTINGS cannot be granted through normal installation. You must grant it via ADB (one-time):

```bash
adb shell pm grant dev.e88e89.hdb android.permission.WRITE_SECURE_SETTINGS
```


## How the Bypass Works

| adb_enabled | ADB Functional | Financial App Detection |
|:-:|:-:|:-:|
| 0 | No | Passes (ADB off) |
| 1 | Yes | Detected |
| 2 | Yes | Passes (≠ 1) |

## Compatibility

Tested with adb_enabled = 2:

| App | Status | Notes |
|---|:-:|---|
| iPASS MONEY | Pass | |
| PX Pay (全支付) | Pass | |
| Easy Wallet (悠遊付) | Pass | |
| Taipei Fubon Bank (台北富邦銀行) | Pass | |
| Fubon AI Pro (富邦 AI Pro) | Pass | |
| e-Post Office (行動郵局) | Pass | |
| JKOPAY (街口支付) | Pass | |
| Cathay United Bank (國泰世華) | Pass | |
| CTBC Bank (中國信託) | Pass | |
| OPEN POINT | Pass | |
| Cathay Securities (國泰證券) | Warn | Warning shown, but does not affect functionality |
| Next Bank (將來銀行) | Warn | Transfer feature restricted |
| FamilyMart (全家便利商店) | Fail | Refuses to launch |

Note: Apps that use more sophisticated detection methods (e.g. checking USB connection state, ro.debuggable, or using attestation APIs) may still detect debugging regardless of the adb_enabled value.

## License

This project is licensed under the GNU General Public License v3.0.
