package com.jskaleel.fte.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.jskaleel.fte.BuildConfig
import com.jskaleel.fte.R
import com.jskaleel.fte.model.SelectedMenuItem
import com.jskaleel.fte.utils.AppPreference
import com.jskaleel.fte.utils.AppPreference.get
import com.jskaleel.fte.utils.AppPreference.set
import com.jskaleel.fte.utils.Constants
import com.jskaleel.fte.utils.RxBus

class SettingsFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    private lateinit var mContext: Context
    private lateinit var bottomSheet: BottomSheetSettings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeBus()
        val toolBar = view.findViewById<Toolbar>(R.id.toolBar)
        val rlListTypeLayout = view.findViewById<LinearLayout>(R.id.rlListTypeLayout)
        val rlSourceCodeLayout = view.findViewById<LinearLayout>(R.id.rlSourceCodeLayout)
        val rlOSSLayout = view.findViewById<LinearLayout>(R.id.rlOSSLayout)
        val swPush = view.findViewById<SwitchCompat>(R.id.swPush)
        val txtPushStatus = view.findViewById<TextView>(R.id.txtPushStatus)
        val txtAppVersion = view.findViewById<TextView>(R.id.txtAppVersion)

        bottomSheet = BottomSheetSettings()

        rlListTypeLayout.setOnClickListener {
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        toolBar.setNavigationOnClickListener {
            requireActivity().findNavController(R.id.navHostFragment).navigateUp()
        }

        val isPushChecked =
            AppPreference.customPrefs(mContext)[Constants.SharedPreference.NEW_BOOKS_UPDATE, true]

        swPush.isChecked = isPushChecked
        txtPushStatus.text = if (isPushChecked) getString(R.string.on) else getString(R.string.off)

        swPush.setOnCheckedChangeListener { _, isChecked ->
            AppPreference.customPrefs(mContext)[Constants.SharedPreference.NEW_BOOKS_UPDATE] =
                isChecked
            txtPushStatus.text = if (isChecked) getString(R.string.on) else getString(R.string.off)

            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic(Constants.CHANNEL_NAME)
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.CHANNEL_NAME)
            }
        }

        rlOSSLayout.setOnClickListener {
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.open_sources))
            startActivity(Intent(mContext, OssLicensesMenuActivity::class.java))
        }
        rlSourceCodeLayout.setOnClickListener {

            val url = "https://github.com/khaleeljageer/FreeTamilEBooks"
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
            }
            startActivity(shareIntent)
        }

        txtAppVersion.text = String.format(getString(R.string.version, BuildConfig.VERSION_NAME))
    }


    private fun subscribeBus() {
        RxBus.subscribe {
            if (it is SelectedMenuItem) {
                if (bottomSheet.isVisible) {
                    bottomSheet.dismiss()
                }
            }
        }
    }
}