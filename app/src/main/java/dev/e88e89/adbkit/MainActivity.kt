package dev.e88e89.adbkit

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors

class MainActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "adbkit_prefs"
        const val KEY_ADB_VALUE = "adb_value"
        const val DEFAULT_ADB_VALUE = 2

        fun getPrefs(context: Context) =
            context.createDeviceProtectedStorageContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private lateinit var textPermStatus: TextView
    private lateinit var textGlobalValue: TextView
    private lateinit var textSecureValue: TextView
    private lateinit var textTargetValue: TextView
    private lateinit var cardPermHint: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getPrefs(this)
        val savedValue = prefs.getInt(KEY_ADB_VALUE, DEFAULT_ADB_VALUE)

        // ── Radio group ──
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val radioMap = mapOf(
            0 to findViewById<RadioButton>(R.id.radio_0),
            1 to findViewById<RadioButton>(R.id.radio_1),
            2 to findViewById<RadioButton>(R.id.radio_2),
        )
        radioMap[savedValue]?.isChecked = true

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selected = group.findViewById<RadioButton>(checkedId)
            val value = selected.tag.toString().toInt()
            prefs.edit().putInt(KEY_ADB_VALUE, value).apply()
        }

        // ── Status views ──
        textPermStatus = findViewById(R.id.text_perm_status)
        textGlobalValue = findViewById(R.id.text_global_value)
        textSecureValue = findViewById(R.id.text_secure_value)
        textTargetValue = findViewById(R.id.text_target_value)
        cardPermHint = findViewById(R.id.card_perm_hint)

        // ── Buttons ──
        findViewById<MaterialButton>(R.id.btn_check).setOnClickListener {
            refreshStatus()
        }

        findViewById<MaterialButton>(R.id.btn_apply).setOnClickListener {
            BootReceiver.applyAdbSetting(this)
            refreshStatus()
            Toast.makeText(this, R.string.applied_toast, Toast.LENGTH_SHORT).show()
        }

        refreshStatus()
    }

    private fun refreshStatus() {
        val prefs = getPrefs(this)
        val targetValue = prefs.getInt(KEY_ADB_VALUE, DEFAULT_ADB_VALUE)

        val globalValue = try {
            Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED)
        } catch (_: Exception) { -1 }

        val secureValue = try {
            @Suppress("DEPRECATION")
            Settings.Secure.getInt(contentResolver, "adb_enabled")
        } catch (_: Exception) { -1 }

        val hasPermission = try {
            Settings.Global.putInt(contentResolver, Settings.Global.ADB_ENABLED, globalValue)
            true
        } catch (_: SecurityException) {
            false
        }

        // Permission — use Material theme-resolved colors
        if (hasPermission) {
            textPermStatus.text = getString(R.string.perm_granted)
            textPermStatus.setTextColor(
                MaterialColors.getColor(textPermStatus, com.google.android.material.R.attr.colorPrimary)
            )
            cardPermHint.visibility = android.view.View.GONE
        } else {
            textPermStatus.text = getString(R.string.perm_not_granted)
            textPermStatus.setTextColor(
                MaterialColors.getColor(textPermStatus, com.google.android.material.R.attr.colorError)
            )
            cardPermHint.visibility = android.view.View.VISIBLE
        }

        // Values
        textGlobalValue.text = globalValue.toString()
        textSecureValue.text = secureValue.toString()
        textTargetValue.text = targetValue.toString()
    }
}
