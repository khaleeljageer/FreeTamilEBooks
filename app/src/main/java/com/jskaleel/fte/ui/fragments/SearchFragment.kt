package com.jskaleel.fte.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.ui.base.BookClickListener
import com.jskaleel.fte.ui.base.HomeListAdapter
import com.jskaleel.fte.utils.DeviceUtils
import com.jskaleel.fte.utils.PrintLog
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment(), BookClickListener {
    override fun bookItemClickListener(adapterPosition: Int) {

    }

    private lateinit var adapter: HomeListAdapter
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
        toolBar.setNavigationOnClickListener {
            DeviceUtils.hideSoftKeyboard(activity!!)
            activity!!.findNavController(R.id.navHostFragment).navigateUp()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvSearchList.setHasFixedSize(true)
        adapter = HomeListAdapter(mContext, this@SearchFragment, mutableListOf(), 2)
        val layoutManger = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        rvSearchList.layoutManager = layoutManger
        rvSearchList.adapter = adapter

        val appDataBase = AppDatabase.getAppDatabase(mContext)
        searchHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                if (msg != null) {
                    if (msg.what == triggerNewService && edtSearch != null && isAdded) {
                        val query = edtSearch.text.toString()
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

        edtSearch.tag = "FILTER"
        edtSearch.setOnTouchListener(OnTouchListener { _, event ->
            val drawableRight = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= edtSearch.right - edtSearch.compoundDrawables[drawableRight].bounds.width()) {
                    when (edtSearch.tag) {
                        "CLEAR" -> {
                            edtSearch.text?.clear()
                        }
                        "FILTER" -> {
                            Toast.makeText(mContext, "Filter Clicked", Toast.LENGTH_SHORT).show()
                        }
                    }
                    return@OnTouchListener true
                }
            }
            false
        })

        edtSearch.doAfterTextChanged { edt ->
            if (edt != null) {
                val query = edt.toString().trim()
                if (query.isNotBlank() && query.isNotEmpty()) {
                    if (query.length > 3) {
                        PrintLog.info("Text ${edt.toString()}")

                        searchHandler.removeMessages(triggerNewService)
                        searchHandler.sendEmptyMessageDelayed(triggerNewService, 1000)
                    }
                    edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_clear_black_24dp, 0)
                    edtSearch.tag = "CLEAR"
                } else {
                    edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_tune_black_24dp, 0)
                    edtSearch.tag = "FILTER"
                    searchHandler.removeMessages(triggerNewService)
                    adapter.clearBooks()
                }
            }
        }
    }

    private fun loadBooks(books: List<LocalBooks>) {
        adapter.loadBooks(books)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
}