package com.jskaleel.fte.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jskaleel.fte.R
import com.jskaleel.fte.data.local.AppDatabase
import com.jskaleel.fte.data.dao.LocalBooksDao
import com.jskaleel.fte.data.entities.LocalBooks
import com.jskaleel.fte.data.entities.DownloadCompleted
import com.jskaleel.fte.ui.base.BookClickListener
import com.jskaleel.fte.ui.base.BookListAdapter
import com.jskaleel.fte.utils.DeviceUtils
import com.jskaleel.fte.utils.RxBus

class DownloadsFragment : Fragment(), BookClickListener {
    override fun bookRemoveClickListener(adapterPosition: Int, book: LocalBooks) {
//        val newBook = DownloadUtil.removeDownload(mContext, book)
//        adapter.removeItem(adapterPosition, newBook)
//        if (adapter.itemCount == 0) {
//            rvDownloadList?.visibility = View.GONE
//            emptyLayout?.visibility = View.VISIBLE
//        }
    }

    override fun bookItemClickListener(adapterPosition: Int, book: LocalBooks) {
//        DownloadUtil.openSavedBook(mContext, book)
    }

    private var toolBar: Toolbar? = null
    private var rvDownloadList: RecyclerView? = null
    private var emptyLayout: LinearLayout? = null
    private lateinit var adapter: BookListAdapter
    private lateinit var mContext: Context
    private lateinit var appDataBase: LocalBooksDao

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.appDataBase = AppDatabase.getAppDatabase(mContext).localBooksDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_downloads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar = view.findViewById<Toolbar>(R.id.toolBar)
        emptyLayout = view.findViewById<LinearLayout>(R.id.emptyLayout)
        rvDownloadList = view.findViewById<RecyclerView>(R.id.rvDownloadList)

        toolBar?.setNavigationOnClickListener {
            DeviceUtils.hideSoftKeyboard(requireActivity())
            requireActivity().findNavController(R.id.navHostFragment).navigateUp()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvDownloadList?.setHasFixedSize(true)

        adapter = BookListAdapter(mutableListOf()) { _, _ ->

        }
        val layoutManger = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        rvDownloadList?.layoutManager = layoutManger
        rvDownloadList?.adapter = adapter

//        val downloadedList = appDataBase.getDownloadedBooks(true)
//        adapter.loadBooks(downloadedList)

        rvDownloadList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val `val` = rvDownloadList!!.computeVerticalScrollOffset()
                    .toFloat() / 150.toFloat() * 100.toFloat()
                if (`val` <= 100) {
                    toolBar?.alpha = 1 - `val` / 100
                } else {
                    toolBar?.alpha = 0f
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {

            }
        })

        RxBus.subscribe {
            when (it) {
                is DownloadCompleted -> {
                    if (isAdded) {
//                        val downloadedBook = appDataBase.getDownloadedBook(it.downloadId)
//                        adapter.addNewItem(downloadedBook)
                    }
                }
            }
        }
    }
}