package com.jskaleel.fte.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.database.dao.LocalBooksDao
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.ui.base.BookClickListener
import com.jskaleel.fte.ui.base.BookListAdapter
import com.jskaleel.fte.utils.DeviceUtils
import com.jskaleel.fte.utils.downloader.DownloadUtil
import kotlinx.android.synthetic.main.fragment_downloads.*

class DownloadsFragment : Fragment(), BookClickListener {
    override fun bookRemoveClickListener(adapterPosition: Int, book: LocalBooks) {

    }

    override fun bookItemClickListener(adapterPosition: Int, book: LocalBooks) {
        DownloadUtil.openSavedBook(mContext, book)
    }

    private lateinit var adapter: BookListAdapter
    private lateinit var mContext: Context
    private lateinit var appDataBase: LocalBooksDao

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.appDataBase = AppDatabase.getAppDatabase(mContext).localBooksDao()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_downloads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar.setNavigationOnClickListener {
            DeviceUtils.hideSoftKeyboard(activity!!)
            activity!!.findNavController(R.id.navHostFragment).navigateUp()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvDownloadList.setHasFixedSize(true)

        adapter = BookListAdapter(mContext, this@DownloadsFragment, mutableListOf(), 1)
        val layoutManger = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        rvDownloadList.layoutManager = layoutManger
        rvDownloadList.adapter = adapter

        val downloadedList = appDataBase.getDownloadedBooks(true)
        adapter.loadBooks(downloadedList)

        rvDownloadList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val `val` = rvDownloadList.computeVerticalScrollOffset().toFloat() / 150.toFloat() * 100.toFloat()
                if (`val` <= 100) {
                    toolBar.alpha = 1 - `val` / 100
                } else {
                    toolBar.alpha = 0f
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {

            }
        })
    }
}