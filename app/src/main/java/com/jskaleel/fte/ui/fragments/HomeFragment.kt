package com.jskaleel.fte.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.model.ScrollList
import com.jskaleel.fte.ui.base.BookClickListener
import com.jskaleel.fte.ui.base.BookListAdapter
import com.jskaleel.fte.utils.PrintLog
import com.jskaleel.fte.utils.RxBus
import com.jskaleel.fte.utils.downloader.DownloadManagerHelper
import com.jskaleel.fte.utils.downloader.DownloadUtil
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), BookClickListener {
    override fun bookItemClickListener(adapterPosition: Int, book: LocalBooks) {
        PrintLog.info("Search adapterPosition $adapterPosition ${book.title}")
        DownloadUtil.queueForDownload(mContext, book)
    }

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context;
    }

    override fun onResume() {
        super.onResume()

        val downloads = DownloadManagerHelper.getDownloads(mContext)
        PrintLog.info("Downloads $downloads")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appDataBase = AppDatabase.getAppDatabase(mContext)
        val booksList = appDataBase.localBooksDao().getAllLocalBooks()
        rvBookList.setHasFixedSize(true)

        val adapter = BookListAdapter(mContext, this@HomeFragment, booksList as MutableList<LocalBooks>, 1)
        val layoutManger = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        rvBookList.layoutManager = layoutManger
        rvBookList.adapter = adapter

        RxBus.subscribe {
            if (it is ScrollList) {
                rvBookList.smoothScrollToPosition(0)
            }
        }
    }
}