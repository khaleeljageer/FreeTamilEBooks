package com.jskaleel.fte.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jskaleel.fte.R
import com.jskaleel.fte.model.SelectedMenu
import com.jskaleel.fte.utils.RxBus
import kotlinx.android.synthetic.main.fragment_bottomsheet.*

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottomsheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigationView.setNavigationItemSelectedListener { menuItem ->
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