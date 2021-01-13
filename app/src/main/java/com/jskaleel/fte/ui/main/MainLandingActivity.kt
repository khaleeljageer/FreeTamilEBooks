package com.jskaleel.fte.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.jskaleel.fte.R
import com.jskaleel.fte.databinding.ActivityMainLandingBinding
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem

class MainLandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainLandingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)

        val menuItems = arrayOf(
            CbnMenuItem(
                R.drawable.ic_dashboard,
                R.drawable.avd_dashboard,
                R.id.navigation_dashboard
            ),
            CbnMenuItem(R.drawable.ic_home, R.drawable.avd_home, R.id.navigation_home),
            CbnMenuItem(
                R.drawable.ic_download,
                R.drawable.avd_download,
                R.id.navigation_downloads
            ),
            CbnMenuItem(
                R.drawable.ic_settings,
                R.drawable.avd_settings,
                R.id.navigation_settings
            )
        )
        binding.navView.setMenuItems(menuItems, 1)
        binding.navView.setupWithNavController(navController)
    }
}