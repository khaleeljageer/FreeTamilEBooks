package com.jskaleel.fte.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jskaleel.fte.R
import com.jskaleel.fte.data.entities.LocalBooks
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
                val context = holder.itemView.context
                binding.txtBookTitle.text = this.title
                binding.txtBookAuthor.text = this.author
                binding.ivBookCover.load(this.image) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder)
                }

                if (this.isClicked) {
                    binding.txtDownload.disableButton()
                    binding.progressIndicator.show()
                } else {
                    binding.txtDownload.enableButton(this.isDownloaded)
                    binding.progressIndicator.hide()
                }

                binding.txtDownload.setOnClickListener {
                    if (this.isDownloaded) {
                        mListener.invoke(-1, this)
                    } else {
                        this.isClicked = true
                        binding.txtDownload.disableButton()
                        binding.progressIndicator.show()
                        mListener.invoke(holder.adapterPosition, this)
                    }
                }
            }
        }
    }

    private fun TextView.enableButton(status: Boolean) {
        this.apply {
            isEnabled = true
            text = if (status) {
                context.getString(R.string.read)
            } else {
                context.getString(R.string.download)
            }
        }
    }

    private fun TextView.disableButton() {
        this.apply {
            isEnabled = false
            text = context.getString(R.string.wait)
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

    fun successUiUpdate(itemPosition: Int, book: LocalBooks) {
        booksList[itemPosition].apply {
            savedPath = book.savedPath
            isDownloaded = book.isDownloaded
            isClicked = false
        }
        notifyItemChanged(itemPosition)
    }

    fun errorUiUpdate(itemPosition: Int) {
        booksList[itemPosition].apply {
            savedPath = ""
            isDownloaded = false
            isClicked = false
        }
        notifyItemChanged(itemPosition)
    }

    inner class BookViewHolder(
        val binding: NewBookListItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root)
}