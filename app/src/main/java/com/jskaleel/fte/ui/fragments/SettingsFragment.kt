package com.jskaleel.fte.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.jskaleel.fte.BuildConfig
import com.jskaleel.fte.R
import com.jskaleel.fte.databinding.FragmentSettingsBinding
import com.jskaleel.fte.ui.main.MainLandingActivity
import com.jskaleel.fte.utils.AppPreference
import com.jskaleel.fte.utils.AppPreference.get
import com.jskaleel.fte.utils.AppPreference.set
import com.jskaleel.fte.utils.Constants

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSettingsBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSettingsBinding.bind(view)

        (activity as MainLandingActivity).apply {
            setSupportActionBar(binding.toolBar)
            supportActionBar?.elevation = 0.5f
        }


        val isPushChecked =
            AppPreference.customPrefs(requireContext())[Constants.SharedPreference.NEW_BOOKS_UPDATE, true]

        binding.ivNotificationLogo.setImageResource(
            if (isPushChecked) {
                R.drawable.ic_round_notifications_active_24
            } else {
                R.drawable.ic_round_notifications_off_24
            }
        )

        binding.swPush.isChecked = isPushChecked
        binding.txtPushStatus.text =
            if (isPushChecked) getString(R.string.on) else getString(R.string.off)

        binding.swPush.setOnCheckedChangeListener { _, isChecked ->
            AppPreference.customPrefs(requireContext())[Constants.SharedPreference.NEW_BOOKS_UPDATE] =
                isChecked

            if (isChecked) {
                binding.txtPushStatus.text = getString(R.string.on)
                FirebaseMessaging.getInstance().subscribeToTopic(Constants.CHANNEL_NAME)
                binding.ivNotificationLogo.setImageResource(R.drawable.ic_round_notifications_active_24)
            } else {
                binding.txtPushStatus.text = getString(R.string.off)
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.CHANNEL_NAME)
                binding.ivNotificationLogo.setImageResource(R.drawable.ic_round_notifications_off_24)
            }
        }

        binding.rlOSSLayout.setOnClickListener {
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.open_sources))
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
        }

        binding.rlSourceCodeLayout.setOnClickListener {
            val url = "https://github.com/khaleeljageer/FreeTamilEBooks"
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
            }
            startActivity(shareIntent)
        }

        binding.llShareApp.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    String.format(getString(R.string.share_text), Constants.STORE_URL)
                )
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_app))
            startActivity(shareIntent)

        }

        binding.txtAppVersion.text =
            String.format(getString(R.string.version, BuildConfig.VERSION_NAME))

        binding.txtKaniyamDesc.text = HtmlCompat.fromHtml(
            getString(R.string.kaniyam_foundation_desc),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.txtVglugDesc.text = HtmlCompat.fromHtml(
            getString(R.string.vglug_foundation_desc),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }
}