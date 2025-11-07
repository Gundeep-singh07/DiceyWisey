// Location: app/src/main/java/com/diceywisey/ProfileActivity.kt
package com.example.diceywisey.Activities
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.diceywisey.database.DatabaseHelper
import com.example.diceywisey.utils.SessionManager
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.diceywisey.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvTrophies: TextView
    private lateinit var tvGamesPlayed: TextView
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize
        databaseHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        // Find views
        btnBack = findViewById(R.id.btnBack)
        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        tvTrophies = findViewById(R.id.tvTrophies)
        tvGamesPlayed = findViewById(R.id.tvGamesPlayed)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnSave)
        btnLogout = findViewById(R.id.btnLogout)

        // Load user data
        loadUserData()

        // Set click listeners
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        btnSave.setOnClickListener {
            updateProfile()
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadUserData() {
        val userId = sessionManager.getUserId()
        val user = databaseHelper.getUserById(userId)

        user?.let {
            tvUsername.text = it.username
            tvEmail.text = it.email
            tvTrophies.text = it.trophies.toString()
            tvGamesPlayed.text = it.gamesPlayed.toString()
            etEmail.setText(it.email)
        }
    }

    private fun updateProfile() {
        val newEmail = etEmail.text.toString().trim()

        if (newEmail.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = sessionManager.getUserId()
        val success = databaseHelper.updateUserProfile(userId, newEmail)

        if (success) {
            Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
            tvEmail.text = newEmail
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_confirm))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                logout()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun logout() {
        sessionManager.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}