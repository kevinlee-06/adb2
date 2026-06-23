package dev.e88e89.adbkit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Receives BOOT_COMPLETED / LOCKED_BOOT_COMPLETED and applies
 * the user-selected adb_enabled value from SharedPreferences.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            Log.i("AdbKit", "Boot received (${intent.action}), applying setting…")
            AdbSettingsManager.applyAdbSetting(context)
        }
    }
}
