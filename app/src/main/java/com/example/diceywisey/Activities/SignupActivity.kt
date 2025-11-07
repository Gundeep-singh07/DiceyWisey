// Location: app/src/main/java/com/diceywisey/SignupActivity.kt
package com.example.diceywisey.Activities
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diceywisey.R

import com.example.diceywisey.database.DatabaseHelper
import com.example.diceywisey.utils.SessionManager



import com.google.android.material.textfield.TextInputEditText

class SignupActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSignup: Button
    private lateinit var tvLogin: TextView

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize
        databaseHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        // Find views
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignup = findViewById(R.id.btnSignup)
        tvLogin = findViewById(R.id.tvLogin)

        // Set click listeners
        btnSignup.setOnClickListener {
            signupUser()
        }

        tvLogin.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun signupUser() {
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if username exists
        if (databaseHelper.isUsernameExists(username)) {
            Toast.makeText(this, getString(R.string.error_username_exists), Toast.LENGTH_SHORT).show()
            return
        }

        // Register user
        val success = databaseHelper.registerUser(username, email, password)
        if (success) {
            // Login the user automatically
            val user = databaseHelper.loginUser(username, password)
            if (user != null) {
                sessionManager.createLoginSession(user.id, user.username)
                Toast.makeText(this, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        } else {
            Toast.makeText(this, "Signup failed. Try again.", Toast.LENGTH_SHORT).show()
        }
    }
}