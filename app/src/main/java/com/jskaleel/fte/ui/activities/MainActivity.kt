package com.jskaleel.fte.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.jskaleel.fte.R
import com.jskaleel.fte.model.SelectedMenu
import com.jskaleel.fte.ui.base.BaseActivity
import com.jskaleel.fte.ui.fragments.BottomNavigationDrawerFragment
import com.jskaleel.fte.utils.RxBus
import kotlinx.android.synthetic.main.activity_main.*

/* https://medium.com/material-design-in-action/implementing-bottomappbar-behavior-fbfbc3a30568
* https://github.com/firatkarababa/BottomAppBar
* */

class MainActivity : BaseActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNavDrawerFragment: BottomNavigationDrawerFragment

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(bottomAppBar)
        subscribeBus()

        bottomNavDrawerFragment = BottomNavigationDrawerFragment()

        /*val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment? ?: return

        navController = host.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)*/

    }

    private fun subscribeBus() {
        RxBus.subscribe {
            if (it is SelectedMenu) {
                switchFragment(it)
            }
        }
    }

    private fun switchFragment(it: SelectedMenu) {
        when (it.menuItem) {
            R.id.menuAbout -> {
                findNavController(R.id.navHostFragment).navigate(R.id.settingsFragment)
            }
            R.id.menuContribute -> {
                findNavController(R.id.navHostFragment).navigate(R.id.settingsFragment)
            }
            R.id.menuPublish -> {
                findNavController(R.id.navHostFragment).navigate(R.id.settingsFragment)
            }
            R.id.menuFeedBack -> {
                Toast.makeText(applicationContext, "Nav4", Toast.LENGTH_SHORT).show()
            }
            R.id.menuSettings -> {
                Toast.makeText(applicationContext, "Nav5", Toast.LENGTH_SHORT).show()
                findNavController(R.id.navHostFragment).navigate(R.id.settingsFragment)
            }
        }
        if (bottomNavDrawerFragment.isVisible) {
            bottomNavDrawerFragment.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottomappbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
            }
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        appDatabase.localBooksDao().deleteAll()
    }
}
