package com.jskaleel.fte.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.jskaleel.fte.R
import kotlinx.android.synthetic.main.fragment_webview.*

class WebViewFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewType = arguments?.let {
            val safeArgs = WebViewFragmentArgs.fromBundle(it)
            safeArgs.TYPE
        }

        val assesUri = when(viewType) {
            1 -> "file:///android_asset/htmlfiles/about_us.html"
            2 -> "file:///android_asset/htmlfiles/team.html"
            3 -> "file:///android_asset/htmlfiles/how_to_publish.html"
            else -> ""
        }

        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.domStorageEnabled = true
        webView!!.loadUrl(assesUri)


        toolBar.setNavigationOnClickListener{
            activity!!.findNavController(R.id.navHostFragment).navigateUp()
        }
    }
}