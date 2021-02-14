package com.jskaleel.fte.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.fte.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var viewType = 1
        if (intent != null && intent.hasExtra(KEY_TYPE)) {
            viewType = intent.getIntExtra(KEY_TYPE, 1)
        }

        val assesUri = when (viewType) {
            1 -> "file:///android_asset/htmlfiles/about_us.html"
            2 -> "file:///android_asset/htmlfiles/team.html"
            3 -> "file:///android_asset/htmlfiles/how_to_publish.html"
            else -> ""
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.domStorageEnabled = true
        binding.webView.loadUrl(assesUri)
    }

    companion object {
        const val KEY_TYPE = "key_type"
        const val TYPE_ABOUT_US = 1
        const val TYPE_TEAM = 2
        const val TYPE_PUBLISH = 3
    }
}