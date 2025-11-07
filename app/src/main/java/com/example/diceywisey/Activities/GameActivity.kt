// Location: app/src/main/java/com/example/diceywisey/GameActivity.kt
package com.example.diceywisey.Activities
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.diceywisey.database.DatabaseHelper
import com.example.diceywisey.utils.SessionManager
import kotlin.random.Random
import com.example.diceywisey.R

class GameActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvScore: TextView
    private lateinit var tvGamesCount: TextView
    private lateinit var tvDiceValue: TextView
    private lateinit var tvResult: TextView
    private lateinit var btnRoll: Button

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
        btnRoll = findViewById(R.id.btnRoll)

        // Load user data
        loadUserData()

        // Set click listeners
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnRoll.setOnClickListener {
            if (!isRolling) {
                rollDice()
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

    private fun rollDice() {
        isRolling = true
        btnRoll.isEnabled = false
        tvResult.visibility = View.INVISIBLE

        // Vibrate if enabled
        if (sessionManager.isVibrationEnabled()) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        }

        // Animate dice roll
        val rotationX = ObjectAnimator.ofFloat(tvDiceValue, "rotationX", 0f, 360f)
        val rotationY = ObjectAnimator.ofFloat(tvDiceValue, "rotationY", 0f, 360f)
        val scaleX = ObjectAnimator.ofFloat(tvDiceValue, "scaleX", 1f, 1.3f, 1f)
        val scaleY = ObjectAnimator.ofFloat(tvDiceValue, "scaleY", 1f, 1.3f, 1f)

        rotationX.duration = 500
        rotationY.duration = 500
        scaleX.duration = 500
        scaleY.duration = 500

        rotationX.interpolator = AccelerateDecelerateInterpolator()
        rotationY.interpolator = AccelerateDecelerateInterpolator()

        // Simulate rolling with random numbers
        var count = 0
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (count < 10) {
                    tvDiceValue.text = Random.nextInt(1, 7).toString()
                    count++
                    handler.postDelayed(this, 50)
                } else {
                    val finalValue = Random.nextInt(1, 7)
                    tvDiceValue.text = finalValue.toString()
                    showResult(finalValue)
                }
            }
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(rotationX, rotationY, scaleX, scaleY)
        animatorSet.start()

        handler.postDelayed(runnable, 100)
    }

    private fun showResult(diceValue: Int) {
        // Calculate trophies earned (simple: dice value = trophies)
        val trophiesEarned = diceValue

        // Update database
        val userId = sessionManager.getUserId()
        databaseHelper.updateGameStats(userId, trophiesEarned)

        // Update UI
        currentScore += trophiesEarned
        gamesPlayed++
        tvScore.text = currentScore.toString()
        tvGamesCount.text = gamesPlayed.toString()

        // Show result
        tvResult.text = getString(R.string.trophy_earned, trophiesEarned)
        tvResult.visibility = View.VISIBLE

        // Animate result text
        val fadeIn = ObjectAnimator.ofFloat(tvResult, "alpha", 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(tvResult, "scaleX", 0.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(tvResult, "scaleY", 0.5f, 1f)

        fadeIn.duration = 300
        scaleX.duration = 300
        scaleY.duration = 300

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeIn, scaleX, scaleY)
        animatorSet.start()

        // Re-enable button
        Handler(Looper.getMainLooper()).postDelayed({
            isRolling = false
            btnRoll.isEnabled = true
        }, 1000)
    }
}