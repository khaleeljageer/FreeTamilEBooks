package com.jskaleel.fte.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jskaleel.fte.R
import com.jskaleel.fte.database.entities.LocalBooks

class BookListAdapter(
    private val mContext: Context,
    private val listener: BookClickListener,
    private val booksList: MutableList<LocalBooks>,
    private val type: Int
) : RecyclerView.Adapter<BookViewHolder>() {
    private var previousClickedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_list_item, parent, false)
        val lp = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        lp.height = if (type == 1) parent.measuredHeight / 3 else (parent.measuredHeight / 2.5).toInt()
        view.layoutParams = lp
        return BookViewHolder(mContext, view, listener)
    }

    override fun getItemCount(): Int {
        return booksList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val bookItem = booksList[holder.adapterPosition]
        holder.bindData(bookItem, holder.adapterPosition)
        holder.itemView.setOnClickListener {
            if (previousClickedPosition == holder.adapterPosition) {
                return@setOnClickListener
            }
            if (previousClickedPosition != -1) {
                booksList[previousClickedPosition].isExpanded = false
                notifyItemChanged(previousClickedPosition)
            }
            previousClickedPosition = holder.adapterPosition
            val expanded = bookItem.isExpanded
            bookItem.isExpanded = !expanded
            notifyItemChanged(holder.adapterPosition)
        }
    }

    fun loadBooks(books: List<LocalBooks>) {
        previousClickedPosition = -1
        booksList.clear()
        booksList.addAll(books)
        notifyDataSetChanged()
    }

    fun clearBooks() {
        previousClickedPosition = -1
        booksList.clear()
        notifyDataSetChanged()
    }

    fun updateItemStatus(itemPosition: Int, downloadedBook: LocalBooks) {
        booksList[itemPosition].savedPath = downloadedBook.savedPath
        booksList[itemPosition].isDownloaded = downloadedBook.isDownloaded
        booksList[itemPosition].downloadId = downloadedBook.downloadId
        notifyItemChanged(itemPosition)
    }

    fun updateDownloadId(itemPosition: Int, downloadID: Long) {
        booksList[itemPosition].downloadId = downloadID
        notifyItemChanged(itemPosition)
    }

    fun removeItem(adapterPosition: Int, newBook: LocalBooks) {
        previousClickedPosition = -1
        booksList.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
        notifyItemRangeChanged(adapterPosition, itemCount)
    }

    fun addNewItem(downloadedBook: LocalBooks) {
        previousClickedPosition = -1;
        booksList.add(downloadedBook)
        notifyItemInserted(itemCount + 1)
    }
}