package com.jskaleel.fte.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.jskaleel.fte.R
import com.jskaleel.fte.model.SelectedMenu
import com.jskaleel.fte.utils.PrintLog
import com.jskaleel.fte.utils.RxBus
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    private lateinit var bottomSheet: BottomSheetSettings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeBus()
        bottomSheet = BottomSheetSettings()

        rlListTypeLayout.setOnClickListener {
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        toolBar.setNavigationOnClickListener{
            activity!!.findNavController(R.id.navHostFragment).navigateUp()
        }
    }


    private fun subscribeBus() {
        RxBus.subscribe {
            if (it is SelectedMenu) {
                PrintLog.info("Selected Menu : ${it.menuItem}")
                if (bottomSheet.isVisible) {
                    bottomSheet.dismiss()
                }
            }
        }
    }
}