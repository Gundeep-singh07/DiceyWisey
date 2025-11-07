package com.example.diceywisey.Activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diceywisey.R
import com.example.diceywisey.Adapter.LeaderboardAdapter
import com.example.diceywisey.database.DatabaseHelper
import com.example.diceywisey.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var rvLeaderboard: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Initialize helpers
        databaseHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        // Find views
        btnBack = findViewById(R.id.btnBack)
        rvLeaderboard = findViewById(R.id.rvLeaderboard)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Setup RecyclerView
        rvLeaderboard.layoutManager = LinearLayoutManager(this)
        rvLeaderboard.alpha = 0f

        // Load leaderboard
        loadLeaderboard()

        // Setup bottom navigation
        setupBottomNavigation()

        // Animate entrance
        animateEntrance()

        // Back button
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.navigation_leaderboard
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
                R.id.navigation_leaderboard -> true
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadLeaderboard() {
        val userId = sessionManager.getUserId()
        val leaderboard = databaseHelper.getLeaderboard(userId)
        adapter = LeaderboardAdapter(leaderboard)
        rvLeaderboard.adapter = adapter
    }

    private fun animateEntrance() {
        // Fade in recycler view
        val fadeIn = ObjectAnimator.ofFloat(rvLeaderboard, "alpha", 0f, 1f).apply {
            duration = 500
            startDelay = 200
        }
        fadeIn.start()

        // Animate items one by one
        Handler(Looper.getMainLooper()).postDelayed({
            animateListItems()
        }, 300)
    }

    private fun animateListItems() {
        val itemCount = rvLeaderboard.adapter?.itemCount ?: 0
        for (i in 0 until minOf(itemCount, 10)) {
            Handler(Looper.getMainLooper()).postDelayed({
                val viewHolder = rvLeaderboard.findViewHolderForAdapterPosition(i)
                viewHolder?.itemView?.let { view ->
                    view.alpha = 0f
                    view.translationX = -100f

                    val anim = AnimatorSet().apply {
                        playTogether(
                            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f),
                            ObjectAnimator.ofFloat(view, "translationX", -100f, 0f)
                        )
                        duration = 400
                        interpolator = OvershootInterpolator()
                    }
                    anim.start()
                }
            }, i * 80L)
        }
    }
}