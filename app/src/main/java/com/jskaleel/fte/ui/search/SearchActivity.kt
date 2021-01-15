package com.jskaleel.fte.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.fte.data.local.AppDatabase
import com.jskaleel.fte.databinding.ActivitySearchBinding
import com.jskaleel.fte.utils.PrintLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivitySearchBinding
    private val appDataBase: AppDatabase by inject()
    private val job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val key = it.toString()
                    binding.ivClear.visibility = if (key.trim().isNotEmpty()) {
                        View.VISIBLE
                    } else {
                        View.INVISIBLE
                    }
                }
            }
        })

        binding.ivBack.setOnClickListener { closeSearchActivity() }
        binding.ivClear.setOnClickListener { binding.edtSearch.text?.clear() }

        binding.edtSearch.setOnEditorActionListener { _, actionId, event ->
            if (event != null) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    performSearch(binding.edtSearch.text)
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener false
        }
    }

    private fun performSearch(editable: Editable?) {
        editable?.let {
            val key = it.toString().trim()
            launch {
                val searchResult = appDataBase.localBooksDao().getBooksByKey("%$key%")
                PrintLog.info("Search Key : $key Result : ${searchResult.size}")
            }
        }
    }

    private fun closeSearchActivity() {
        onBackPressed()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}