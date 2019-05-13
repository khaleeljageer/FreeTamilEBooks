package com.jskaleel.fte.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.LongSparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.model.DownloadCompleted
import com.jskaleel.fte.model.NewBookAdded
import com.jskaleel.fte.model.ScrollList
import com.jskaleel.fte.ui.base.BookClickListener
import com.jskaleel.fte.ui.base.BookListAdapter
import com.jskaleel.fte.utils.RxBus
import com.jskaleel.fte.utils.downloader.DownloadUtil
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), BookClickListener {

    private var downloadsPositions = LongSparseArray<Long>()
    private lateinit var appDataBase: AppDatabase
    override fun bookRemoveClickListener(adapterPosition: Int, book: LocalBooks) {
        val newBook = DownloadUtil.removeDownload(mContext, book)
        adapter.updateItemStatus(adapterPosition, newBook)
    }

    override fun bookItemClickListener(adapterPosition: Int, book: LocalBooks) {
        if (book.isDownloaded) {
            DownloadUtil.openSavedBook(mContext, book)
        } else {
            if (book.downloadId == -1L) {
                val downloadID = DownloadUtil.queueForDownload(mContext, book)
                if(downloadID != 0L) {
                    adapter.updateDownloadId(adapterPosition, downloadID)
                    downloadsPositions.put(downloadID, adapterPosition.toLong())
                }
            }
        }
    }

    private lateinit var adapter: BookListAdapter
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.appDataBase = AppDatabase.getAppDatabase(mContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val booksList = appDataBase.localBooksDao().getAllLocalBooksByOrder()
        rvBookList.setHasFixedSize(true)

        adapter = BookListAdapter(mContext, this@HomeFragment, booksList as MutableList<LocalBooks>, 1)
        val layoutManger = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        rvBookList.layoutManager = layoutManger
        rvBookList.adapter = adapter

        RxBus.subscribe {
            when (it) {
                is ScrollList -> {
                    try {
                        rvBookList.smoothScrollToPosition(0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                is DownloadCompleted -> {
                    if (isAdded) {
                        val downloadedBook = appDataBase.localBooksDao().getDownloadedBook(it.downloadId)
                        adapter.updateItemStatus(downloadsPositions.get(it.downloadId).toInt(), downloadedBook)
                    }
                }
                is NewBookAdded -> {
                    if (it.isBookAdded) {
                        val newBookList = appDataBase.localBooksDao().getAllLocalBooksByOrder()
                        adapter.loadBooks(newBookList)
                    }
                }
            }
        }
    }
}