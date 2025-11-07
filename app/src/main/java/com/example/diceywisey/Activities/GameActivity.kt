package com.example.diceywisey.Activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.diceywisey.database.DatabaseHelper
import com.example.diceywisey.utils.SessionManager
import kotlin.random.Random
import com.example.diceywisey.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class GameActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var tvScore: TextView
    private lateinit var tvGamesCount: TextView
    private lateinit var tvDiceValue: TextView
    private lateinit var tvResult: TextView
    private lateinit var cardResult: CardView
    private lateinit var btnRoll: Button
    private lateinit var viewGlow: View
    private lateinit var layoutScore: LinearLayout
    private lateinit var layoutGames: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var vibrator: Vibrator

    private var isRolling = false
    private var currentScore = 0
    private var gamesPlayed = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initialize
        databaseHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Find views
        btnBack = findViewById(R.id.btnBack)
        tvScore = findViewById(R.id.tvScore)
        tvGamesCount = findViewById(R.id.tvGamesCount)
        tvDiceValue = findViewById(R.id.tvDiceValue)
        tvResult = findViewById(R.id.tvResult)
        cardResult = findViewById(R.id.cardResult)
        btnRoll = findViewById(R.id.btnRoll)
        viewGlow = findViewById(R.id.viewGlow)
        layoutScore = findViewById(R.id.layoutScore)
        layoutGames = findViewById(R.id.layoutGames)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Load user data
        loadUserData()

        // Setup bottom navigation
        setupBottomNavigation()

        // Animate UI on load
        animateUIOnLoad()

        // Continuous idle animations
        startIdleAnimations()

        // Set click listeners
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnRoll.setOnClickListener {
            if (!isRolling) {
                animateButtonPress(btnRoll) {
                    rollDice()
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.navigation_game
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.navigation_game -> true
                R.id.navigation_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    finish()
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    true
                }
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

    private fun loadUserData() {
        val userId = sessionManager.getUserId()
        val user = databaseHelper.getUserById(userId)
        user?.let {
            currentScore = it.trophies
            gamesPlayed = it.gamesPlayed
            tvScore.text = currentScore.toString()
            tvGamesCount.text = gamesPlayed.toString()
        }
    }

    private fun animateUIOnLoad() {
        // Animate score cards
        val scoreAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(layoutScore, "translationX", -200f, 0f),
                ObjectAnimator.ofFloat(layoutScore, "alpha", 0f, 1f)
            )
            duration = 500
            interpolator = OvershootInterpolator()
        }

        val gamesAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(layoutGames, "translationX", 200f, 0f),
                ObjectAnimator.ofFloat(layoutGames, "alpha", 0f, 1f)
            )
            duration = 500
            interpolator = OvershootInterpolator()
            startDelay = 100
        }

        // Animate dice
        val diceAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(tvDiceValue, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(tvDiceValue, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(tvDiceValue, "alpha", 0f, 1f)
            )
            duration = 600
            interpolator = BounceInterpolator()
            startDelay = 300
        }

        // Animate button
        val buttonAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(btnRoll, "translationY", 100f, 0f),
                ObjectAnimator.ofFloat(btnRoll, "alpha", 0f, 1f)
            )
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 400
        }

        scoreAnim.start()
        gamesAnim.start()
        diceAnim.start()
        buttonAnim.start()
    }

    private fun startIdleAnimations() {
        // Gentle pulse on dice
        val dicePulse = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(tvDiceValue, "scaleX", 1f, 1.05f, 1f),
                ObjectAnimator.ofFloat(tvDiceValue, "scaleY", 1f, 1.05f, 1f)
            )
            duration = 2000
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 1000
        }
        dicePulse.start()

        // Gentle rotation
        val rotation = ObjectAnimator.ofFloat(tvDiceValue, "rotation", 0f, 5f, 0f, -5f, 0f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        rotation.start()
    }

    private fun rollDice() {
        isRolling = true
        btnRoll.isEnabled = false
        cardResult.visibility = View.INVISIBLE

        // Show glow effect
        viewGlow.visibility = View.VISIBLE
        val glowAnim = ObjectAnimator.ofFloat(viewGlow, "alpha", 0f, 0.5f).apply {
            duration = 200
        }
        glowAnim.start()

        // Vibrate if enabled
        if (sessionManager.isVibrationEnabled()) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        }

        // Intense rolling animation
        val rotationX = ObjectAnimator.ofFloat(tvDiceValue, "rotationX", 0f, 720f)
        val rotationY = ObjectAnimator.ofFloat(tvDiceValue, "rotationY", 0f, 720f)
        val rotation = ObjectAnimator.ofFloat(tvDiceValue, "rotation", 0f, 360f)
        val scaleX = ObjectAnimator.ofFloat(tvDiceValue, "scaleX", 1f, 1.4f, 1f)
        val scaleY = ObjectAnimator.ofFloat(tvDiceValue, "scaleY", 1f, 1.4f, 1f)

        rotationX.duration = 600
        rotationY.duration = 600
        rotation.duration = 600
        scaleX.duration = 600
        scaleY.duration = 600

        rotationX.interpolator = AccelerateDecelerateInterpolator()
        rotationY.interpolator = AccelerateDecelerateInterpolator()

        // Rapid number changes
        var count = 0
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (count < 15) {
                    tvDiceValue.text = Random.nextInt(1, 7).toString()
                    count++
                    handler.postDelayed(this, 40)
                } else {
                    val finalValue = Random.nextInt(1, 7)
                    tvDiceValue.text = finalValue.toString()
                    showResult(finalValue)
                }
            }
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(rotationX, rotationY, rotation, scaleX, scaleY)
        animatorSet.start()

        handler.postDelayed(runnable, 100)
    }

    private fun showResult(diceValue: Int) {
        // Hide glow
        val glowFade = ObjectAnimator.ofFloat(viewGlow, "alpha", 0.5f, 0f).apply {
            duration = 300
        }
        glowFade.start()
        Handler(Looper.getMainLooper()).postDelayed({
            viewGlow.visibility = View.INVISIBLE
        }, 300)

        // Calculate trophies
        val trophiesEarned = diceValue

        // Update database
        val userId = sessionManager.getUserId()
        databaseHelper.updateGameStats(userId, trophiesEarned)

        // Animate score update
        val oldScore = currentScore
        currentScore += trophiesEarned
        gamesPlayed++

        animateNumberChange(tvScore, oldScore, currentScore)
        animateNumberChange(tvGamesCount, gamesPlayed - 1, gamesPlayed)

        // Pulse score cards
        pulseView(layoutScore)
        Handler(Looper.getMainLooper()).postDelayed({
            pulseView(layoutGames)
        }, 100)

        // Show result with animation
        tvResult.text = getString(R.string.trophy_earned, trophiesEarned)
        cardResult.visibility = View.VISIBLE

        val resultAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(cardResult, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(cardResult, "scaleX", 0.5f, 1.2f, 1f),
                ObjectAnimator.ofFloat(cardResult, "scaleY", 0.5f, 1.2f, 1f),
                ObjectAnimator.ofFloat(cardResult, "translationY", -50f, 0f)
            )
            duration = 500
            interpolator = BounceInterpolator()
        }
        resultAnim.start()

        // Confetti effect on dice
        val celebrate = AnimatorSet().apply {
            playSequentially(
                ObjectAnimator.ofFloat(tvDiceValue, "rotation", 0f, -15f, 15f, -10f, 10f, 0f).apply {
                    duration = 500
                    interpolator = BounceInterpolator()
                }
            )
        }
        celebrate.start()

        // Vibrate for success
        if (sessionManager.isVibrationEnabled()) {
            Handler(Looper.getMainLooper()).postDelayed({
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            }, 200)
        }

        // Re-enable button
        Handler(Looper.getMainLooper()).postDelayed({
            isRolling = false
            btnRoll.isEnabled = true

            // Hide result after delay
            Handler(Looper.getMainLooper()).postDelayed({
                val fadeOut = ObjectAnimator.ofFloat(cardResult, "alpha", 1f, 0f).apply {
                    duration = 300
                }
                fadeOut.start()
                Handler(Looper.getMainLooper()).postDelayed({
                    cardResult.visibility = View.INVISIBLE
                }, 300)
            }, 2000)
        }, 1000)
    }

    private fun animateNumberChange(textView: TextView, from: Int, to: Int) {
        val animator = ValueAnimator.ofInt(from, to)
        animator.duration = 500
        animator.addUpdateListener { animation ->
            textView.text = animation.animatedValue.toString()
        }
        animator.start()
    }

    private fun pulseView(view: View) {
        val pulse = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.15f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.15f, 1f)
            )
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
        }
        pulse.start()
    }

    private fun animateButtonPress(button: Button, action: () -> Unit) {
        val press = AnimatorSet().apply {
            playSequentially(
                ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f).apply { duration = 100 },
                ObjectAnimator.ofFloat(button, "scaleX", 0.95f, 1f).apply { duration = 100 }
            )
        }

        val pressY = AnimatorSet().apply {
            playSequentially(
                ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f).apply { duration = 100 },
                ObjectAnimator.ofFloat(button, "scaleY", 0.95f, 1f).apply { duration = 100 }
            )
        }

        press.start()
        pressY.start()

        Handler(Looper.getMainLooper()).postDelayed({
            action()
        }, 100)
    }
}