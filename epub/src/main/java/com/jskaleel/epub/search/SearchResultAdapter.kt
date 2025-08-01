/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.search

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jskaleel.epub.databinding.ItemRecycleSearchBinding
import com.jskaleel.epub.utils.singleClick
import org.readium.r2.shared.publication.Locator

/**
 * This class is an adapter for Search results' recycler view.
 */
class SearchResultAdapter(private var listener: Listener) :
    PagingDataAdapter<Locator, SearchResultAdapter.ViewHolder>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecycleSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locator = getItem(position) ?: return
        val html =
            "${locator.text.before}<span style=\"background:yellow;\"><b>${locator.text.highlight}</b></span>${locator.text.after}"
        holder.textView.text =
            Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)

        holder.itemView.singleClick { v ->
            listener.onItemClicked(v, locator)
        }
    }

    inner class ViewHolder(val binding: ItemRecycleSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val textView = binding.text
    }

    interface Listener {
        fun onItemClicked(v: View, locator: Locator)
    }

    private class ItemCallback : DiffUtil.ItemCallback<Locator>() {

        override fun areItemsTheSame(oldItem: Locator, newItem: Locator): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Locator, newItem: Locator): Boolean =
            oldItem == newItem
    }
}
