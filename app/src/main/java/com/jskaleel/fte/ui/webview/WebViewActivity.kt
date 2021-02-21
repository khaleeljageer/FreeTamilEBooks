package com.jskaleel.fte.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.fte.R
import com.jskaleel.fte.data.local.AppDatabase
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
            1 -> {
                binding.toolBar.title = getString(R.string.about_us)
                "file:///android_asset/htmlfiles/about_us.html"
            }
            2 -> {
                binding.toolBar.title = getString(R.string.contributors)
                "file:///android_asset/htmlfiles/team.html"
            }
            3 -> {
                binding.toolBar.title = getString(R.string.publish_books)
                "file:///android_asset/htmlfiles/how_to_publish.html"
            }
            else -> ""
        }
        binding.toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
        when (viewType) {
            1, 2, 3 -> {
                binding.webView.visibility = View.VISIBLE
                binding.listView.visibility = View.GONE
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
            else -> {

                binding.toolBar.title = getString(R.string.book_writers)
                binding.webView.visibility = View.GONE
                binding.listView.visibility = View.VISIBLE

                val localBooks =
                    AppDatabase.getAppDatabase(baseContext).localBooksDao().getAuthors()

                val authorsAdapter =
                    ArrayAdapter(
                        baseContext,
                        android.R.layout.simple_list_item_1,
                        localBooks.distinct()
                    )

                with(binding.listView) {
                    adapter = authorsAdapter
                }
            }
        }
    }

    companion object {
        const val KEY_TYPE = "key_type"
        const val TYPE_ABOUT_US = 1
        const val TYPE_TEAM = 2
        const val TYPE_PUBLISH = 3
        const val TYPE_AUTHORS = 4
    }
}