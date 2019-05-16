package com.jskaleel.fte.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jskaleel.fte.R
import com.jskaleel.fte.model.SelectedMenuItem
import com.jskaleel.fte.utils.AppPreference
import com.jskaleel.fte.utils.AppPreference.get
import com.jskaleel.fte.utils.Constants
import com.jskaleel.fte.utils.RxBus
import kotlinx.android.synthetic.main.bottomsheet_main.*

class BottomSheetSettings : BottomSheetDialogFragment() {

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottomsheet_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val selectedMenu =
            AppPreference.customPrefs(mContext)[Constants.SharedPreference.BOOK_LIST_TYPE, R.id.menuRandom]
        navigationView.setCheckedItem(selectedMenu)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            RxBus.publish(SelectedMenuItem(menuItem))
            true
        }
    }
}