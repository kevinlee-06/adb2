package dev.e88e89.adbkit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log

/**
 * Receives BOOT_COMPLETED / LOCKED_BOOT_COMPLETED and applies
 * the user-selected adb_enabled value from SharedPreferences.
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AdbKit"

        fun applyAdbSetting(context: Context) {
            val prefs = MainActivity.getPrefs(context)
            val value = prefs.getInt(
                MainActivity.KEY_ADB_VALUE,
                MainActivity.DEFAULT_ADB_VALUE
            )

            try {
                Settings.Global.putInt(
                    context.contentResolver,
                    Settings.Global.ADB_ENABLED,
                    value
                )
                Log.i(TAG, "Global adb_enabled → $value")
            } catch (e: SecurityException) {
                Log.e(TAG, "Global write failed: ${e.message}")
            }

            try {
                @Suppress("DEPRECATION")
                Settings.Secure.putInt(
                    context.contentResolver,
                    "adb_enabled",
                    value
                )
                Log.i(TAG, "Secure adb_enabled → $value")
            } catch (e: SecurityException) {
                Log.e(TAG, "Secure write failed: ${e.message}")
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            Log.i(TAG, "Boot received, applying setting…")
            applyAdbSetting(context)
        }
    }
}
