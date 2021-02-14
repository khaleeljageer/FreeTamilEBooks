package com.jskaleel.fte.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.*
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.fte.databinding.ActivitySplashBinding
import com.jskaleel.fte.ui.main.MainLandingActivity
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {
    private val activityScope = CoroutineScope(Dispatchers.Main)

    private val splashViewModel: SplashViewModel by viewModel()

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

        activityScope.launch {
            delay(1000)
            splashViewModel.fetchBooks(baseContext)
        }

        splashViewModel.messageData.observe(this, {
            binding.txtLoading.text = it
        })

        splashViewModel.viewState.observe(this, {
            if (!it) {
                startNextActivity()
            } else {
                binding.progressLoader.show()
            }
        })
    }

    private fun startNextActivity() {
        startActivity(Intent(this@SplashActivity, MainLandingActivity::class.java))
        this@SplashActivity.finish()
    }

    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }
}