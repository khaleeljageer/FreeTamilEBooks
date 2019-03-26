package com.jskaleel.fte.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jskaleel.fte.R
import com.jskaleel.fte.database.entities.LocalBooks

class HomeListAdapter(
    private val mContext: Context,
    private val booksList: MutableList<LocalBooks>
) : RecyclerView.Adapter<BookViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_list_item, parent, false)
        val lp = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        lp.height = parent.measuredHeight / 3
        view.layoutParams = lp
        return BookViewHolder(mContext, view)
    }

    override fun getItemCount(): Int {
        return booksList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bindData(booksList[position])
    }
}