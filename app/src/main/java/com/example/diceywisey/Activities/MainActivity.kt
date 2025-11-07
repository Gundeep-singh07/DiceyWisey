// Location: app/src/main/java/com/diceywisey/MainActivity.kt
package com.example.diceywisey.Activities
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.diceywisey.database.DatabaseHelper
import com.example.diceywisey.utils.SessionManager
import com.example.diceywisey.R


class MainActivity : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvTrophies: TextView
    private lateinit var tvGamesPlayed: TextView
    private lateinit var btnPlay: Button
    private lateinit var btnLeaderboard: Button
    private lateinit var cardProfile: CardView
    private lateinit var btnSettings: ImageView

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize
        databaseHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        // Find views
        tvUsername = findViewById(R.id.tvUsername)
        tvTrophies = findViewById(R.id.tvTrophies)
        tvGamesPlayed = findViewById(R.id.tvGamesPlayed)
        btnPlay = findViewById(R.id.btnPlay)
        btnLeaderboard = findViewById(R.id.btnLeaderboard)
        cardProfile = findViewById(R.id.cardProfile)
        btnSettings = findViewById(R.id.btnSettings)

        // Load user data
        loadUserData()

        // Set click listeners
        btnPlay.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnLeaderboard.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        cardProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData() {
        val userId = sessionManager.getUserId()
        val user = databaseHelper.getUserById(userId)

        user?.let {
            tvUsername.text = it.username
            tvTrophies.text = it.trophies.toString()
            tvGamesPlayed.text = it.gamesPlayed.toString()
        }
    }
}