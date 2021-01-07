package com.jskaleel.fte.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    private val activityScope = CoroutineScope(Dispatchers.Main)

    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        appDatabase = AppDatabase.getAppDatabase(applicationContext)
        val llSplashLogo = findViewById<LinearLayout>(R.id.llSplashLogo)


        val scaleAnim = ScaleAnimation(
            0f, 1f,
            0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        val alphaAnim = AlphaAnimation(0f, 1f)
        val animSet = AnimationSet(true)
        animSet.addAnimation(scaleAnim)
        animSet.addAnimation(alphaAnim)

        animSet.interpolator = OvershootInterpolator()
        animSet.duration = 600
        llSplashLogo.startAnimation(animSet)

        activityScope.launch {
            delay(2000)
            startNextActivity()
        }
    }

    private fun startNextActivity() {
        startActivity(Intent(this@SplashActivity, MainLandingActivity::class.java))
//        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        this@SplashActivity.finish()
    }

    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }
}