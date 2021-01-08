package com.jskaleel.fte.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Status
import com.jskaleel.fte.R
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.databinding.NewBookListItemBinding
import com.jskaleel.fte.utils.FileUtils
import com.jskaleel.fte.utils.PrintLog

class BookListAdapter(
    private val booksList: MutableList<LocalBooks>
) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding =
            NewBookListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return booksList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        with(holder) {
            with(booksList[holder.adapterPosition]) {
                binding.txtBookTitle.text = this.title
                binding.txtBookAuthor.text = this.author

                Glide.with(holder.itemView.context)
                    .load(this.image)
                    .placeholder(R.drawable.placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivBookCover)

                val status = PRDownloader.getStatus(this.bookid.hashCode())
                binding.txtDownload.text = when (status) {
                    Status.COMPLETED -> {
                        "Open"
                    }
                    Status.FAILED, Status.CANCELLED, Status.UNKNOWN -> {
                        "Download"
                    }
                    Status.RUNNING, Status.QUEUED -> {
                        "Downloading"
                    }
                    else -> {
                        "Download"
                    }
                }

                binding.txtDownload.setOnClickListener {
                    val dirPath = FileUtils.getRootDirPath(holder.itemView.context)
                    val prDownloader =
                        PRDownloader.download(this.epub, dirPath, "${this.bookid}.epub").build()
                    prDownloader.start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            PrintLog.info("DownloadComplete : ${prDownloader.downloadId}")
                        }

                        override fun onError(error: Error?) {
                            PrintLog.info("onError : ${error?.responseCode}")
                        }
                    })
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