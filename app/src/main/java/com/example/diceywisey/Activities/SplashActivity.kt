// Location: app/src/main/java/com/diceywisey/SplashActivity.kt
package com.example.diceywisey.Activities
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.BounceInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.diceywisey.utils.SessionManager
import com.example.diceywisey.R

class SplashActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sessionManager = SessionManager(this)

        // Animate app icon and name
        val appIcon = findViewById<View>(R.id.appIcon)
        val appName = findViewById<View>(R.id.appName)

        // Scale up animation for icon
        val scaleXIcon = ObjectAnimator.ofFloat(appIcon, "scaleX", 0f, 1.2f, 1f)
        val scaleYIcon = ObjectAnimator.ofFloat(appIcon, "scaleY", 0f, 1.2f, 1f)
        scaleXIcon.duration = 1000
        scaleYIcon.duration = 1000
        scaleXIcon.interpolator = BounceInterpolator()
        scaleYIcon.interpolator = BounceInterpolator()

        // Fade in animation for app name
        val fadeInName = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f)
        fadeInName.duration = 800
        fadeInName.startDelay = 500

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXIcon, scaleYIcon, fadeInName)
        animatorSet.start()

        // Navigate after delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2500)
    }

    private fun navigateToNextScreen() {
        val intent = if (sessionManager.isLoggedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}