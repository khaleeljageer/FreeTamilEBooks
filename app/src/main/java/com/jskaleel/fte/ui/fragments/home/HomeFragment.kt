package com.jskaleel.fte.ui.fragments.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.database.entities.SavedBooks
import com.jskaleel.fte.model.DownloadResult
import com.jskaleel.fte.ui.base.BookListAdapter
import com.jskaleel.fte.utils.FileUtils
import com.jskaleel.fte.utils.PrintLog
import kotlinx.coroutines.*
import java.lang.reflect.Type

class HomeFragment : Fragment(), CoroutineScope, (Int, LocalBooks) -> Unit {

    val job = Job()
    override val coroutineContext = Dispatchers.Main + job
    private val appDataBase: AppDatabase by lazy {
        AppDatabase.getAppDatabase(mContext)
    }

//    override fun bookRemoveClickListener(adapterPosition: Int, book: LocalBooks) {
//        val newBook = DownloadUtil.removeDownload(mContext, book)
//        adapter.updateItemStatus(adapterPosition, newBook)
//    }
//
//    override fun bookItemClickListener(adapterPosition: Int, book: LocalBooks) {
//        if (book.isDownloaded) {
//            DownloadUtil.openSavedBook(mContext, book)
//        } else {
//            if (book.downloadId == -1L) {
//                val downloadID = DownloadUtil.queueForDownload(mContext, book)
//                if (downloadID != 0L) {
//                    adapter.updateDownloadId(adapterPosition, downloadID)
//                    downloadsPositions.put(downloadID, adapterPosition.toLong())
//                }
//            }
//        }
//    }

    private lateinit var bookListAdapter: BookListAdapter
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun Context.loadJSONFromAssets(fileName: String): String {
        return applicationContext.assets.open(fileName).bufferedReader().use { reader ->
            reader.readText()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvBookList = view.findViewById<RecyclerView>(R.id.rvBookList)

        val localBooks = requireActivity().loadJSONFromAssets("booksdb.json")
        val bookListType: Type = object : TypeToken<MutableList<LocalBooks>>() {}.type
        val booksList: MutableList<LocalBooks> = Gson().fromJson(localBooks, bookListType)
//        val booksList = appDataBase.localBooksDao().getAllLocalBooks()

        bookListAdapter = BookListAdapter(booksList, this)
        with(rvBookList) {
            this.setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            this.adapter = bookListAdapter
        }

        PrintLog.info("DBSize : ${appDataBase.savedBooksDao().getAllLocalBooks()}")
    }

    override fun invoke(position: Int, book: LocalBooks) {
        if (position == -1) {
            openBook(book)
        } else {
            downloadBook(position, book)
        }
    }

    private fun openBook(book: LocalBooks) {
        FileUtils.openSavedBook(requireContext(), book)
    }

    private fun downloadBook(position: Int, book: LocalBooks) {
        launch {
            val result = withContext(Dispatchers.IO) {
                FileUtils.downloadBook(requireContext(), book)
            }
            when (result) {
                is DownloadResult.Success -> {
                    book.savedPath = result.filePath.absolutePath
                    book.isDownloaded = true
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
                book.savedPath
            )
        )
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}