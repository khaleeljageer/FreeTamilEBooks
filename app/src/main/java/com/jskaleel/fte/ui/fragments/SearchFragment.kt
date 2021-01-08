package com.jskaleel.fte.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.*
import android.view.View.OnTouchListener
import android.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.ui.base.BookClickListener
import com.jskaleel.fte.ui.base.BookListAdapter
import com.jskaleel.fte.utils.DeviceUtils
import com.jskaleel.fte.utils.PrintLog
import com.jskaleel.fte.utils.downloader.DownloadUtil


class SearchFragment : Fragment(), BookClickListener {
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
                if (downloadID != 0L) {
                    adapter.updateDownloadId(adapterPosition, downloadID)
                }
            }
        }
    }

    private var searchView: MaterialCardView? = null
    private var rvSearchList: RecyclerView? = null
    private var edtSearch: TextInputEditText? = null
    private lateinit var adapter: BookListAdapter
    private lateinit var searchHandler: Handler
    private lateinit var mContext: Context
    private var filterType = 1
    private val triggerNewService = 1001


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar = view.findViewById<Toolbar>(R.id.toolBar)
        rvSearchList = view.findViewById<RecyclerView>(R.id.rvSearchList)
        edtSearch = view.findViewById<TextInputEditText>(R.id.edtSearch)
        searchView = view.findViewById<MaterialCardView>(R.id.searchView)

        toolBar.setNavigationOnClickListener {
            DeviceUtils.hideSoftKeyboard(requireActivity())
            requireActivity().findNavController(R.id.navHostFragment).navigateUp()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvSearchList?.setHasFixedSize(true)
        adapter = BookListAdapter(mutableListOf())
        val layoutManger = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        rvSearchList?.layoutManager = layoutManger
        rvSearchList?.adapter = adapter

//        edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search_white_24dp, 0)

        val appDataBase = AppDatabase.getAppDatabase(mContext)
        searchHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg != null) {
                    if (msg.what == triggerNewService && edtSearch != null && isAdded) {
                        val query = edtSearch!!.text.toString()
                        when (filterType) {
                            1 -> {
                                val books = appDataBase.localBooksDao().getBooksByTitle("%$query%")
                                PrintLog.info("Books by Title $books")
                                loadBooks(books)
                            }
                            2 -> {
                                val books = appDataBase.localBooksDao().getBooksByAuthor("%$query%")
                                PrintLog.info("Books by Author $books")
                                loadBooks(books)
                            }
                        }
                    }
                }
            }
        }

        edtSearch?.tag = "FILTER"
        edtSearch?.setOnTouchListener(OnTouchListener { _, event ->
            val drawableRight = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= edtSearch!!.right - edtSearch!!.compoundDrawables[drawableRight].bounds.width()) {
                    when (edtSearch!!.tag) {
                        "CLEAR" -> {
                            edtSearch!!.text?.clear()
                        }
                        "FILTER" -> {
                            val popup = PopupMenu(activity, searchView, Gravity.END)
                            popup.inflate(R.menu.popup_filter)
                            popup.menu.findItem(
                                when (filterType) {
                                    1 -> R.id.title
                                    2 -> R.id.author
                                    else -> R.id.title
                                }
                            ).isChecked = true
                            val query = edtSearch!!.text.toString()
                            popup.setOnMenuItemClickListener(object :
                                PopupMenu.OnMenuItemClickListener {
                                override fun onMenuItemClick(item: MenuItem): Boolean {
                                    when (item.itemId) {
                                        R.id.title -> {
                                            item.isChecked = true
                                            filterType = 1
                                            if (query.length > 3) {
                                                searchHandler.removeMessages(triggerNewService)
                                                searchHandler.sendEmptyMessageDelayed(
                                                    triggerNewService,
                                                    500
                                                )
                                            }
                                            return true
                                        }
                                        R.id.author -> {
                                            item.isChecked = true
                                            filterType = 2
                                            if (query.length > 3) {
                                                searchHandler.removeMessages(triggerNewService)
                                                searchHandler.sendEmptyMessageDelayed(
                                                    triggerNewService,
                                                    500
                                                )
                                            }
                                            return true
                                        }
                                        else -> return false
                                    }
                                }
                            })
                            popup.show()
                        }
                    }
                    return@OnTouchListener true
                }
            }
            false
        })

        edtSearch?.doAfterTextChanged { edt ->
            if (edt != null) {
                val query = edt.toString().trim()
                if (query.isNotBlank() && query.isNotEmpty()) {
                    if (query.length > 3) {
                        PrintLog.info("Text ${edt.toString()}")

                        searchHandler.removeMessages(triggerNewService)
                        searchHandler.sendEmptyMessageDelayed(triggerNewService, 1000)
                    }
                    edtSearch?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_clear_black_24dp,
                        0
                    )
                    edtSearch?.tag = "CLEAR"
                } else {
                    edtSearch?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_tune_black_24dp,
                        0
                    )
                    edtSearch?.tag = "FILTER"
                    searchHandler.removeMessages(triggerNewService)
                    adapter.clearBooks()
                }
            }
        }
    }

    private fun loadBooks(books: List<LocalBooks>) {
        adapter.loadBooks(books)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
}