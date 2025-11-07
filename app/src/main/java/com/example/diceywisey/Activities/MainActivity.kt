package com.example.diceywisey.Activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.diceywisey.database.DatabaseHelper
import com.example.diceywisey.utils.SessionManager
import com.example.diceywisey.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var tvUsername: TextView
    private lateinit var tvTrophies: TextView
    private lateinit var tvGamesPlayed: TextView
    private lateinit var tvAvatar: TextView
    private lateinit var btnPlay: Button
    private lateinit var btnLeaderboard: Button
    private lateinit var cardProfile: CardView
    private lateinit var btnSettings: ImageView
    private lateinit var bottomNavigation: BottomNavigationView
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
        tvAvatar = findViewById(R.id.tvAvatar)
        btnPlay = findViewById(R.id.btnPlay)
        btnLeaderboard = findViewById(R.id.btnLeaderboard)
        cardProfile = findViewById(R.id.cardProfile)
        btnSettings = findViewById(R.id.btnSettings)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Load user data
        loadUserData()

        // Setup bottom navigation
        setupBottomNavigation()

        // Animate UI elements on load
        animateUIElements()

        // Set click listeners
        btnPlay.setOnClickListener {
            animateButtonClick(it) {
                startActivity(Intent(this, GameActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        btnLeaderboard.setOnClickListener {
            animateButtonClick(it) {
                startActivity(Intent(this, LeaderboardActivity::class.java))
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        }

        cardProfile.setOnClickListener {
            animateCardClick(it) {
                startActivity(Intent(this, ProfileActivity::class.java))
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        }

        btnSettings.setOnClickListener {
            animateButtonClick(it) {
                startActivity(Intent(this, SettingsActivity::class.java))
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        }

        // Animate avatar continuously
        startAvatarAnimation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.navigation_home
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already on home
                    true
                }
                R.id.navigation_game -> {
                    startActivity(Intent(this, GameActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.navigation_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
        bottomNavigation.selectedItemId = R.id.navigation_home
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

    private fun animateUIElements() {
        // Animate card profile
        val cardAnim = ObjectAnimator.ofFloat(cardProfile, "alpha", 0f, 1f)
        val cardScale = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(cardProfile, "scaleX", 0.8f, 1f),
                ObjectAnimator.ofFloat(cardProfile, "scaleY", 0.8f, 1f)
            )
            interpolator = OvershootInterpolator()
            duration = 600
        }

        // Animate play button
        val playButtonAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(btnPlay, "translationY", 100f, 0f),
                ObjectAnimator.ofFloat(btnPlay, "alpha", 0f, 1f)
            )
            interpolator = AccelerateDecelerateInterpolator()
            duration = 500
            startDelay = 200
        }

        // Animate leaderboard button
        val leaderboardAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(btnLeaderboard, "translationY", 100f, 0f),
                ObjectAnimator.ofFloat(btnLeaderboard, "alpha", 0f, 1f)
            )
            interpolator = AccelerateDecelerateInterpolator()
            duration = 500
            startDelay = 300
        }

        val mainAnimator = AnimatorSet()
        mainAnimator.playTogether(cardAnim, cardScale, playButtonAnim, leaderboardAnim)
        mainAnimator.start()

        // Pulse animation for stats
        pulseAnimation(findViewById(R.id.layoutTrophies), 400)
        pulseAnimation(findViewById(R.id.layoutGames), 600)
    }

    private fun pulseAnimation(view: View, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            val pulse = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f)
                )
                duration = 500
                interpolator = AccelerateDecelerateInterpolator()
            }
            pulse.start()
        }, delay)
    }

    private fun startAvatarAnimation() {
        // Smooth continuous rotation
        val rotation = ObjectAnimator.ofFloat(tvAvatar, "rotation", 0f, 360f).apply {
            duration = 20000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Gentle pulsing (scaling) effect
        val scaleX = ObjectAnimator.ofFloat(tvAvatar, "scaleX", 1f, 1.05f, 1f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleY = ObjectAnimator.ofFloat(tvAvatar, "scaleY", 1f, 1.05f, 1f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Combine both scale animations
        val scaleSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY)
        }

        // Start both animations
        rotation.start()
        scaleSet.start()
    }


    private fun animateButtonClick(view: View, action: () -> Unit) {
        val scaleDown = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f)
            )
            duration = 100
        }

        val scaleUp = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f)
            )
            duration = 100
        }

        scaleDown.start()
        Handler(Looper.getMainLooper()).postDelayed({
            scaleUp.start()
            action()
        }, 100)
    }

    private fun animateCardClick(view: View, action: () -> Unit) {
        val pulse = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.98f, 1.02f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.98f, 1.02f, 1f)
            )
            duration = 300
            interpolator = BounceInterpolator()
        }
        pulse.start()
        Handler(Looper.getMainLooper()).postDelayed({
            action()
        }, 150)
    }
}