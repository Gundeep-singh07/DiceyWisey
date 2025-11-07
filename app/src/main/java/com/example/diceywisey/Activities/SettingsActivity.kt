// Location: app/src/main/java/com/diceywisey/SettingsActivity.kt
package com.example.diceywisey.Activities
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.diceywisey.utils.SessionManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.example.diceywisey.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var switchSound: SwitchMaterial
    private lateinit var switchVibration: SwitchMaterial

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize
        sessionManager = SessionManager(this)

        // Find views
        btnBack = findViewById(R.id.btnBack)
        switchSound = findViewById(R.id.switchSound)
        switchVibration = findViewById(R.id.switchVibration)

        // Load settings
        loadSettings()

        // Set click listeners
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        switchSound.setOnCheckedChangeListener { _, isChecked ->
            sessionManager.setSoundEnabled(isChecked)
        }

        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            sessionManager.setVibrationEnabled(isChecked)
        }
    }

    private fun loadSettings() {
        switchSound.isChecked = sessionManager.isSoundEnabled()
        switchVibration.isChecked = sessionManager.isVibrationEnabled()
    }
}