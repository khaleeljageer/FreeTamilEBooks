package com.jskaleel.fte.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.*
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.fte.databinding.ActivitySplashBinding
import com.jskaleel.fte.ui.main.MainLandingActivity
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {
    private val activityScope = CoroutineScope(Dispatchers.Main)

    private val splashViewModel: SplashViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.llSplashLogo.startAnimation(animSet)

        splashViewModel.fetchBooks()
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