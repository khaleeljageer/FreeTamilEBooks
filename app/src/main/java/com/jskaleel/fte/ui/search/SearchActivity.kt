package com.jskaleel.fte.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jskaleel.fte.data.entities.DownloadResult
import com.jskaleel.fte.data.entities.LocalBooks
import com.jskaleel.fte.data.entities.SavedBooks
import com.jskaleel.fte.data.local.AppDatabase
import com.jskaleel.fte.databinding.ActivitySearchBinding
import com.jskaleel.fte.ui.base.BookListAdapter
import com.jskaleel.fte.utils.FileUtils
import com.jskaleel.fte.utils.PrintLog
import com.jskaleel.fte.utils.openBook
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity(), CoroutineScope, (Int, LocalBooks) -> Unit {

    private lateinit var binding: ActivitySearchBinding
    private val appDataBase: AppDatabase by inject()
    private val job = Job()

    private val searchListAdapter by lazy {
        BookListAdapter(mutableListOf(), this)
    }

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

        with(binding.rvBookList) {
            this.setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(
                baseContext,
                androidx.recyclerview.widget.RecyclerView.VERTICAL,
                false
            )
            this.adapter = searchListAdapter
        }
    }

    private fun performSearch(editable: Editable?) {
        editable?.let {
            val key = it.toString().trim()
            launch {
                val searchResult = appDataBase.localBooksDao().getBooksByKey("%$key%")
                PrintLog.info("Search Key : $key Result : ${searchResult.size}")
                runOnUiThread {
                    searchListAdapter.clearBooks()
                    searchListAdapter.loadBooks(searchResult)
                }
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

    override fun invoke(position: Int, book: LocalBooks) {
        if (position == -1) {
            book.openBook(baseContext)
        } else {
            downloadBook(position, book)
        }
    }

    private fun downloadBook(position: Int, book: LocalBooks) {
        launch {
            val result = withContext(Dispatchers.IO) {
                FileUtils.downloadBook(baseContext, book)
            }
            when (result) {
                is DownloadResult.Success -> {
                    book.isDownloaded = true
                    book.savedPath = result.filePath.absolutePath
                    searchListAdapter.successUiUpdate(position, book)
                    updateDatabase(book)
                }
                is DownloadResult.Error -> {
                    searchListAdapter.errorUiUpdate(position)
                    //Show Snack bar. if needed
                }
            }
        }
    }

    private fun updateDatabase(book: LocalBooks) {
        appDataBase.savedBooksDao().insert(
            SavedBooks(
                book.title,
                book.image,
                book.author,
                book.epub,
                book.bookid,
                book.savedPath
            )
        )
    }
}