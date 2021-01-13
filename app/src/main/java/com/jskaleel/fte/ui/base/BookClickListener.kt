package com.jskaleel.fte.ui.base

import com.jskaleel.fte.data.entities.LocalBooks

interface BookClickListener {
    fun bookItemClickListener(adapterPosition: Int, book: LocalBooks)
    fun bookRemoveClickListener(adapterPosition: Int, book: LocalBooks)
}