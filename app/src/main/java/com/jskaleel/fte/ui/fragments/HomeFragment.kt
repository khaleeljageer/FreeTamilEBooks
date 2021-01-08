package com.jskaleel.fte.ui.fragments

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
import com.jskaleel.fte.ui.base.BookListAdapter
import java.lang.reflect.Type


class HomeFragment : Fragment() {

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

    fun Context.loadJSONFromAssets(fileName: String): String {
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

        bookListAdapter = BookListAdapter(booksList)
        with(rvBookList) {
            this.setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            this.adapter = bookListAdapter
        }
    }
}