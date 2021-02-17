package com.jskaleel.fte.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jskaleel.fte.data.entities.DownloadResult
import com.jskaleel.fte.data.entities.LocalBooks
import com.jskaleel.fte.data.entities.SavedBooks
import com.jskaleel.fte.data.local.AppDatabase
import com.jskaleel.fte.databinding.FragmentHomeBinding
import com.jskaleel.fte.ui.base.BookListAdapter
import com.jskaleel.fte.ui.search.SearchActivity
import com.jskaleel.fte.utils.FileUtils
import com.jskaleel.fte.utils.PrintLog
import com.jskaleel.fte.utils.openBook
import kotlinx.coroutines.*

class HomeFragment : Fragment(), CoroutineScope, (Int, LocalBooks) -> Unit {

    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job
    private val appDataBase: AppDatabase by lazy {
        AppDatabase.getAppDatabase(requireContext())
    }

    private lateinit var bookListAdapter: BookListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentHomeBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PrintLog.info("OnViewCreated....")
        val binding = FragmentHomeBinding.bind(view)

        binding.searchWidget.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        val booksList = appDataBase.localBooksDao().getAllLocalBooks() as MutableList<LocalBooks>

        bookListAdapter = BookListAdapter(booksList, this)
        with(binding.rvBookList) {
            this.setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            this.adapter = bookListAdapter
        }
    }

    override fun invoke(position: Int, book: LocalBooks) {
        if (position == -1) {
            book.openBook(requireContext())
        } else {
            downloadBook(position, book)
        }
    }

    private fun downloadBook(position: Int, book: LocalBooks) {
        launch {
            val result = withContext(Dispatchers.IO) {
                FileUtils.downloadBook(requireContext(), book)
            }
            when (result) {
                is DownloadResult.Success -> {
                    book.isDownloaded = true
                    book.savedPath = result.filePath.absolutePath
                    bookListAdapter.successUiUpdate(position, book)
                    updateDatabase(book)
                }
                is DownloadResult.Error -> {
                    bookListAdapter.errorUiUpdate(position)
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
                book.savedPath!!
            )
        )
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}