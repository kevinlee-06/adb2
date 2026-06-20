# ADB Kit

[English README](README.md)

一個極小的 Android App，在開機後自動設定 `adb_enabled`，繞過金融 APP 的偵錯偵測。

## 原理

許多金融／銀行 APP 透過 `Settings.Global.ADB_ENABLED == 1` 偵測 USB 偵錯模式。將值設為 **2**，這些簡單的相等檢查就會失敗 —— APP 認為 ADB 已關閉，但 Android 系統仍將非零值視為啟用。

ADB Kit 在每次開機時自動套用此設定，無需手動操作。

## 功能

- 🔄 **開機自動套用** — `BroadcastReceiver` 監聽 `BOOT_COMPLETED`
- 🔧 **可設定值** — 可選擇 `0`、`1` 或 `2`
- 🎨 **Material 3 UI** — 原生外觀，支援動態色彩（Material You）
- 🌐 **多語言** — 英文及繁體中文
- 🔒 **Direct Boot** — 在使用者解鎖前即可套用
- 📦 **極小** — 無多餘依賴

## 安裝步驟

### 1. 建構

需要 Java 17 及 Android SDK。

```bash
JAVA_HOME=/path/to/java17 \
ANDROID_HOME=/path/to/android-sdk \
./gradlew assembleDebug
```

### 2. 安裝

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 3. 授權

`WRITE_SECURE_SETTINGS` 無法透過一般安裝取得，必須透過 ADB 授權（一次性）：

```bash
adb shell pm grant dev.e88e89.adbkit android.permission.WRITE_SECURE_SETTINGS
```

### 4. 首次啟動

至少開啟 APP 一次，Android 才會在開機時發送 `BOOT_COMPLETED` 廣播：

```bash
adb shell am start dev.e88e89.adbkit/.MainActivity
```

## 使用方式

1. 開啟 APP
2. 選擇開機後要設定的 `adb_enabled` 值：
   - **0** — 停用 ADB
   - **1** — 啟用 ADB（標準）
   - **2** — 繞過金融 APP 偵測（ADB 仍可用）
3. 點擊 **立即套用** 馬上生效，或直接關掉 —— 每次開機都會自動套用
4. 點擊 **檢查** 確認目前的權限和設定狀態

## 繞過原理

| `adb_enabled` | ADB 可用 | 金融 APP 偵測 |
|:-:|:-:|:-:|
| 0 | ❌ 否 | ✅ 通過（ADB 關閉） |
| 1 | ✅ 是 | ❌ 被偵測 |
| 2 | ✅ 是 | ✅ 通過（≠ 1） |

大多數金融 APP 使用類似這樣的簡單檢查：

```java
Settings.Global.getInt(contentResolver, "adb_enabled") == 1
```

將值設為 `2` 讓這個檢查回傳 `false`，而 Android 內部將其視為 truthy（非零 = 啟用）。

## 專案結構

```
app/src/main/
├── AndroidManifest.xml
├── java/dev/e88e89/adbkit/
│   ├── MainActivity.kt      # UI 與設定管理
│   └── BootReceiver.kt      # 開機時套用設定
├── res/layout/
│   └── activity_main.xml    # Material 3 佈局
├── res/values/
│   ├── strings.xml           # 英文字串
│   └── themes.xml            # Material 3 主題
└── res/values-zh-rTW/
    └── strings.xml           # 繁體中文字串
```

## 授權條款

本專案採用 [GNU 通用公共授權條款第 3 版](LICENSE) 授權。
