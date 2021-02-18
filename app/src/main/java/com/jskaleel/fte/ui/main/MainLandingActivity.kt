package com.jskaleel.fte.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.jskaleel.fte.R
import com.jskaleel.fte.databinding.ActivityMainLandingBinding
import com.jskaleel.fte.ui.fragments.DashboardFragment
import com.jskaleel.fte.ui.fragments.SettingsFragment
import com.jskaleel.fte.ui.main.download.DownloadsFragment
import com.jskaleel.fte.ui.main.home.HomeFragment
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem


class MainLandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainLandingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val menuItems = arrayOf(
            CbnMenuItem(
                R.drawable.ic_dashboard,
                R.drawable.avd_dashboard
            ),
            CbnMenuItem(R.drawable.ic_home, R.drawable.avd_home),
            CbnMenuItem(
                R.drawable.ic_download,
                R.drawable.avd_download
            ),
            CbnMenuItem(
                R.drawable.ic_settings,
                R.drawable.avd_settings
            )
        )
        binding.navView.setMenuItems(menuItems, 1)

        loadFragment(HomeFragment(), HOME_TAG)

        binding.navView.setOnMenuItemClickListener { _, i ->
            when (i) {
                0 -> {
                    loadFragment(DashboardFragment(), DASHBOARD_TAG)
                }
                1 -> {
                    loadFragment(HomeFragment(), HOME_TAG)
                }
                2 -> {
                    loadFragment(DownloadsFragment(), DOWNLOADS_TAG)
                }
                3 -> {
                    loadFragment(SettingsFragment(), SETTINGS_TAG)
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        val existingFrag = supportFragmentManager.findFragmentByTag(tag)
        if (existingFrag == null) {
            transaction.add(binding.navContainer.id, fragment, tag)
            showViewInBackStack(transaction, fragment, tag)
        } else {
            showViewInBackStack(transaction, fragment, tag)
        }
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    private fun showViewInBackStack(
        transaction: FragmentTransaction,
        fragment: Fragment,
        tag: String
    ) {
        val fragList = supportFragmentManager.fragments
        for (frag in fragList) {
            if (frag.tag != null) {
                if (frag.tag.equals(tag)) {
                    transaction.show(frag)
                } else {
                    transaction.hide(frag)
                }
            } else {
                transaction.hide(frag)
            }
        }
    }

    companion object {
        const val DASHBOARD_TAG = "DASHBOARD"
        const val HOME_TAG = "HOME"
        const val DOWNLOADS_TAG = "DOWNLOADS"
        const val SETTINGS_TAG = "SETTINGS"
    }

    override fun onBackPressed() {
        finish()
    }
}