package com.jskaleel.fte.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.jskaleel.fte.R
import com.jskaleel.fte.data.entities.SelectedMenu
import com.jskaleel.fte.utils.RxBus

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {

    private var navigationView: NavigationView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationView = view.findViewById<NavigationView>(R.id.navigationView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigationView?.setNavigationItemSelectedListener { menuItem ->
            RxBus.publish(SelectedMenu(menuItem.itemId))
            true
        }
    }

    private fun dismissWithDelay() {
        Handler().postDelayed({
            dismiss()
        }, 100)
    }
}