package com.jskaleel.fte.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jskaleel.fte.databinding.FragmentDashboardBinding
import com.jskaleel.fte.ui.feedback.FeedbackActivity
import com.jskaleel.fte.ui.webview.WebViewActivity

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mcvAboutUs.setOnClickListener {
            launchWebActivity(WebViewActivity.TYPE_ABOUT_US)
        }

        binding.mcvContributors.setOnClickListener {
            launchWebActivity(WebViewActivity.TYPE_TEAM)
        }

        binding.mcvComments.setOnClickListener {
            startActivity(Intent(requireContext(), FeedbackActivity::class.java))
        }

        binding.mcvReleaseBooks.setOnClickListener {
            launchWebActivity(WebViewActivity.TYPE_PUBLISH)
        }
    }

    private fun launchWebActivity(type: Int) {
        val intent = Intent(requireContext(), WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.KEY_TYPE, type)
        startActivity(intent)
    }
}