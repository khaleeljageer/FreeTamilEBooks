package com.jskaleel.fte.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jskaleel.fte.R
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.databinding.NewBookListItemBinding

class BookListAdapter(
    private val booksList: MutableList<LocalBooks>,
    private val mListener: (position: Int, book: LocalBooks) -> Unit
) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding =
            NewBookListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return booksList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        with(holder) {
            with(booksList[this.adapterPosition]) {
                binding.txtBookTitle.text = this.title
                binding.txtBookAuthor.text = this.author

                Glide.with(holder.itemView.context)
                    .load(this.image)
                    .placeholder(R.drawable.placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivBookCover)

                binding.txtDownload.text = "Download"

                binding.txtDownload.setOnClickListener {
                    binding.txtDownload.isEnabled = false
                    mListener.invoke(holder.adapterPosition, this)
                }
            }
        }
    }

    fun loadBooks(books: List<LocalBooks>) {
        booksList.clear()
        booksList.addAll(books)
        notifyDataSetChanged()
    }

    fun clearBooks() {
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
        booksList.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
        notifyItemRangeChanged(adapterPosition, itemCount)
    }

    fun addNewItem(downloadedBook: LocalBooks) {
        booksList.add(downloadedBook)
        notifyItemInserted(itemCount + 1)
    }

    inner class BookViewHolder(
        val binding: NewBookListItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root)
}