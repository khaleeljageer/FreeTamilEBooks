package com.jskaleel.fte.ui.base

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.jskaleel.fte.database.entities.LocalBooks
import kotlinx.android.synthetic.main.book_list_item.view.*

class BookViewHolder(val mContext: Context, view: View) :
    BaseViewHolder<LocalBooks>(view) {

    override fun bindData(article: LocalBooks) {
        Glide.with(mContext)
            .load(article.image)
            .centerCrop()
            .transition(withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(itemView.arBookImage)
    }
}