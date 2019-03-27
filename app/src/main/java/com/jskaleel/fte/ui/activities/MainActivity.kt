package com.jskaleel.fte.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var bottomNavDrawerFragment: BottomNavigationDrawerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(bottomAppBar)
        subscribeBus()

        bottomNavDrawerFragment = BottomNavigationDrawerFragment()

        fabHome.setOnClickListener {
            val navOptions = NavOptions.Builder()
            navOptions.setLaunchSingleTop(true)
            findNavController(R.id.navHostFragment).navigate(R.id.homeFragment, null, navOptions.build())
        }
    }

    private fun subscribeBus() {
        RxBus.subscribe {
            if (it is SelectedMenu) {
                switchFragment(it)
            }
        }
    }

    private fun switchFragment(it: SelectedMenu) {
        val navOptions = NavOptions.Builder()
        navOptions.setLaunchSingleTop(true)

        when (it.menuItem) {
            R.id.menuAbout -> {

                val args = Bundle()
                args.putInt("TYPE", 1)
                findNavController(R.id.navHostFragment).navigate(R.id.webViewFragment, args, navOptions.build())
            }
            R.id.menuContribute -> {
                val args = Bundle()
                args.putInt("TYPE", 2)
                findNavController(R.id.navHostFragment).navigate(R.id.webViewFragment, args, navOptions.build())
            }
            R.id.menuPublish -> {
                val args = Bundle()
                args.putInt("TYPE", 3)
                findNavController(R.id.navHostFragment).navigate(R.id.webViewFragment, args, navOptions.build())
            }
            R.id.menuFeedBack -> {
                Toast.makeText(applicationContext, "Nav4", Toast.LENGTH_SHORT).show()
            }
            R.id.menuSettings -> {
                findNavController(R.id.navHostFragment).navigate(R.id.settingsFragment, null, navOptions.build())
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
        (bottomAppBar.behavior as HideBottomViewOnScrollBehavior).slideUp(bottomAppBar)
        displayMaterialSnackBar("Back Pressed")
    }

    private fun displayMaterialSnackBar(message: String) {
        val marginSide = 0
        val marginBottom = 550
        val snackBar = Snackbar.make(
            container2,
            message,
            Snackbar.LENGTH_SHORT
        )

        val snackBarView = snackBar.view
        val params = snackBarView.layoutParams as CoordinatorLayout.LayoutParams

        params.setMargins(
            params.leftMargin + marginSide,
            params.topMargin,
            params.rightMargin + marginSide,
            params.bottomMargin + marginBottom
        )

        snackBarView.layoutParams = params
        snackBar.show()
    }

}
