package com.jskaleel.fte.ui.fragments.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.ui.base.BookListAdapter
import com.jskaleel.fte.utils.FileUtils
import com.jskaleel.fte.utils.copyStreamToFile
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.android.inject
import java.io.File
import java.lang.reflect.Type


class HomeFragment : Fragment(), CoroutineScope, (Int, LocalBooks) -> Unit {

    override val coroutineContext = Dispatchers.Main + Job()
    private val appDataBase: AppDatabase by lazy {
        AppDatabase.getAppDatabase(mContext)
    }

    private val httpClient: OkHttpClient by inject()

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
    }

    override fun invoke(position: Int, book: LocalBooks) {

        launch {
            val message = withContext(Dispatchers.IO) {
                downloadBook(book)
            }
            Toast.makeText(requireContext(), "Status : $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadBook(book: LocalBooks): String {
        val filePath =
            File(
                FileUtils.getRootDirPath(requireContext()),
                "${book.bookid}.epub"
            )
        val request =
            Request.Builder()
                .url(book.epub)
                .build()
        val response = httpClient.newCall(request).execute()
        if (response.body() != null) {
            val buffer = response.body()!!.byteStream()
            buffer.copyStreamToFile(filePath)
        }

        return response.message()
    }
}