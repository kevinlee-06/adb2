package dev.e88e89.hdb

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.app.Activity
import android.view.View

open class MainActivity : Activity() {

    private lateinit var radioGroupImmediate: RadioGroup
    private lateinit var cardPermHint: View
    private var isUpdatingUI = false

    protected open val layoutResId: Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)

        radioGroupImmediate = findViewById(R.id.radio_group_immediate)
        cardPermHint = findViewById(R.id.card_perm_hint)

        radioGroupImmediate.setOnCheckedChangeListener { group, checkedId ->
            if (isUpdatingUI) return@setOnCheckedChangeListener
            val selected = group.findViewById<RadioButton>(checkedId)
            val value = selected.tag.toString().toIntOrNull() ?: 2
            AdbSettingsManager.applyAdbValue(this, value)
            AdbSettingsManager.getPrefs(this).edit().putInt(AdbSettingsManager.KEY_ADB_VALUE, value).apply()
            refreshStatus()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun refreshStatus() {
        isUpdatingUI = true

        // Permission hint
        val hasPermission = AdbSettingsManager.hasPermission(this)
        cardPermHint.visibility = if (hasPermission) View.GONE else View.VISIBLE

        // Immediate state
        var globalValue = AdbSettingsManager.getGlobalAdbState(this)
        if (globalValue != 0 && globalValue != 1) {
            globalValue = 2
        }
        
        val radioImmMap = mapOf(
            0 to findViewById<RadioButton>(R.id.radio_imm_0),
            1 to findViewById<RadioButton>(R.id.radio_imm_1),
            2 to findViewById<RadioButton>(R.id.radio_imm_2)
        )
        radioImmMap[globalValue]?.isChecked = true

        isUpdatingUI = false
    }
}
