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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.diceywisey.R

class ProfileActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvTrophies: TextView
    private lateinit var tvGamesPlayed: TextView
    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button
    private lateinit var bottomNavigation: BottomNavigationView
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
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSave = findViewById(R.id.btnSave)
        btnLogout = findViewById(R.id.btnLogout)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Load user data
        loadUserData()

        // Setup bottom navigation
        setupBottomNavigation()

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

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.navigation_profile
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.navigation_game -> {
                    startActivity(Intent(this, GameActivity::class.java))
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.navigation_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    finish()
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    true
                }
                R.id.navigation_profile -> true
                else -> false
            }
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
            etUsername.setText(it.username)
            etEmail.setText(it.email)
        }
    }

    private fun updateProfile() {
        val newUsername = etUsername.text.toString().trim()
        val newEmail = etEmail.text.toString().trim()
        val newPassword = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validation
        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Username and Email cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if password fields are filled
        if (newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) {
            if (newPassword != confirmPassword) {
                Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
                return
            }
            if (newPassword.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val userId = sessionManager.getUserId()
        val success = databaseHelper.updateUserProfile(
            userId,
            newUsername,
            newEmail,
            if (newPassword.isNotEmpty()) newPassword else null
        )

        if (success) {
            Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
            tvUsername.text = newUsername
            tvEmail.text = newEmail
            etPassword.setText("")
            etConfirmPassword.setText("")

            // Update session if username changed
            sessionManager.createLoginSession(userId, newUsername)
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("🚪 Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("YES") { dialog, _ ->
            logout()
            dialog.dismiss()
        }
        builder.setNegativeButton("NO") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        // Style the dialog buttons
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            getColor(R.color.minecraft_green)
        )
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
            getColor(R.color.error)
        )
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