package com.jskaleel.fte.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.snackbar.Snackbar
import com.jskaleel.fte.R
import com.jskaleel.fte.model.NetWorkMessage
import com.jskaleel.fte.model.ScrollList
import com.jskaleel.fte.model.SelectedMenu
import com.jskaleel.fte.ui.base.BaseActivity
import com.jskaleel.fte.ui.fragments.BottomNavigationDrawerFragment
import com.jskaleel.fte.utils.NetworkSchedulerService
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
            val currentDestination = findNavController(R.id.navHostFragment).currentDestination
            if (currentDestination != null) {
                if (currentDestination.label == getString(R.string.home_fragment)) {
                    RxBus.publish(ScrollList(true))
                } else {
                    findNavController(R.id.navHostFragment).popBackStack(R.id.homeFragment, false)
                }
                slideUp()
            }
        }
    }

    private fun slideUp() {
        (bottomAppBar.behavior as HideBottomViewOnScrollBehavior).slideUp(bottomAppBar)
    }

    private fun subscribeBus() {
        RxBus.subscribe {
            when (it) {
                is SelectedMenu ->
                    switchFragment(it)
                is NetWorkMessage -> displayMaterialSnackBar(it.message)
            }
        }
    }

    private fun switchFragment(it: SelectedMenu) {
        val navOptions = NavOptions.Builder()
        navOptions.setEnterAnim(android.R.anim.slide_in_left)
        navOptions.setExitAnim(android.R.anim.slide_out_right)
        navOptions.setPopEnterAnim(android.R.anim.slide_in_left)
        navOptions.setPopExitAnim(android.R.anim.slide_out_right)
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
                findNavController(R.id.navHostFragment).navigate(R.id.feedBackFragment, null, navOptions.build())
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
            R.id.abSearchMenu -> {
                val navOptions = NavOptions.Builder()
                navOptions.setEnterAnim(android.R.anim.slide_in_left)
                navOptions.setExitAnim(android.R.anim.slide_out_right)
                navOptions.setPopEnterAnim(android.R.anim.slide_in_left)
                navOptions.setPopExitAnim(android.R.anim.slide_out_right)
                navOptions.setLaunchSingleTop(true)
                findNavController(R.id.navHostFragment).navigate(R.id.searchFragment, null, navOptions.build())
            }
        }
        return true
    }

    private var backToExitPressedOnce: Boolean = false

    override fun onBackPressed() {
        val currentDestination = findNavController(R.id.navHostFragment).currentDestination
        if (currentDestination != null) {
            if (currentDestination.label == getString(R.string.home_fragment)) {
                if (backToExitPressedOnce) {
                    super.onBackPressed()
                    return
                }

                displayMaterialSnackBar(getString(R.string.press_back_again))
                backToExitPressedOnce = true

                Handler().postDelayed({ backToExitPressedOnce = false }, 2000)
            } else {
                super.onBackPressed()
            }
        }
        slideUp()
    }

    fun displayMaterialSnackBar(message: String) {
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

    override fun onStart() {
        super.onStart()
        // Start service and provide it a way to communicate with this class.
        val startServiceIntent = Intent(this, NetworkSchedulerService::class.java)
        startService(startServiceIntent)
    }

    override fun onStop() {
        stopService(Intent(this, NetworkSchedulerService::class.java))
        super.onStop()
    }
}
