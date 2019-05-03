package com.jskaleel.fte.ui.base

import android.content.Context
import android.view.View
import android.view.animation.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.jskaleel.fte.R
import com.jskaleel.fte.database.entities.LocalBooks
import kotlinx.android.synthetic.main.book_list_item.view.*


class BookViewHolder(
    private val mContext: Context,
    view: View,
    val listener: BookClickListener
) :
    BaseViewHolder<LocalBooks>(view) {

    override fun bindData(book: LocalBooks, adapterPosition: Int) {
        Glide.with(mContext)
            .load(book.image)
            .transition(withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(itemView.arBookImage)

        val expanded = book.isExpanded

        itemView.fabDownload.run {
            if (book.isDownloaded) {
                itemView.fabDelete.visibility = View.VISIBLE
                itemView.fabDownload.visibility = View.VISIBLE
                itemView.pbDownloadProgress.visibility = View.INVISIBLE
                setImageResource(R.drawable.ic_read_book_black_24dp)
            } else {
                itemView.fabDelete.visibility = View.INVISIBLE
                itemView.fabDownload.visibility = View.VISIBLE
                itemView.pbDownloadProgress.visibility = View.INVISIBLE
                setImageResource(R.drawable.ic_file_download_black_24dp)
            }
        }

        // Set the visibility based on state
        itemView.rlDownloadView.visibility = if (expanded) View.VISIBLE else View.GONE
        if (expanded) {
            val scaleAnim = ScaleAnimation(
                0f, 1f,
                0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            val alphaAnim = AlphaAnimation(0f, 1f)
            val animSet = AnimationSet(true)
            animSet.addAnimation(scaleAnim)
            animSet.addAnimation(alphaAnim)
            animSet.isFillEnabled = true

            animSet.interpolator = OvershootInterpolator()
            animSet.duration = 300
            animSet.startOffset = 100
            itemView.fabDownload.startAnimation(animSet)
        }
        itemView.fabDelete.setOnClickListener {
            listener.bookRemoveClickListener(adapterPosition, book)
        }

        itemView.fabDownload.setOnClickListener {
            if (!book.isDownloaded) {
                itemView.fabDownload.visibility = View.INVISIBLE
                itemView.pbDownloadProgress.visibility = View.VISIBLE
            }
            listener.bookItemClickListener(adapterPosition, book)
        }
    }
}