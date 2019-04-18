package com.jskaleel.fte.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.utils.DeviceUtils
import com.jskaleel.fte.utils.PrintLog
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment() {

    private lateinit var mContext: Context
    private var filterType = 1

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
        val appDataBase = AppDatabase.getAppDatabase(mContext)

        edtSearch.setOnTouchListener(OnTouchListener { _, event ->
            val drawableRight = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= edtSearch.right - edtSearch.compoundDrawables[drawableRight].bounds.width()) {
                    Toast.makeText(mContext, "Filter Clicked", Toast.LENGTH_SHORT).show()
                    return@OnTouchListener true
                }
            }
            false
        })

        edtSearch.doAfterTextChanged { edt ->
            if (edt != null) {
                val query = edt.toString().trim()
                if (query.isNotBlank() && query.isNotEmpty()) {
                    PrintLog.info("Text ${edt.toString()}")
                    when (filterType) {
                        1 -> {
                            val books = appDataBase.localBooksDao().getBooksByTitle("%$query%")
                            PrintLog.info("Books $books")
                        }
                        2 -> {
                            appDataBase.localBooksDao().getBooksByAuthor("%$query%")
                        }
                    }
                } else {
                    //TODO: Search else part
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
}