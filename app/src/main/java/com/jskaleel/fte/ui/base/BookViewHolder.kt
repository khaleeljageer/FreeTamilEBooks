package com.jskaleel.fte.ui.base

import android.content.Context
import android.view.View
import android.view.animation.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
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
            .centerCrop()
            .transition(withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(itemView.arBookImage)

        val expanded = book.isExpanded
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

        itemView.fabDownload.setOnClickListener {
            listener.bookItemClickListener(adapterPosition, book)
        }
    }
}