package com.jskaleel.fte.utils

import android.content.Context
import android.view.View
import com.jskaleel.fte.data.entities.LocalBooks


fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun LocalBooks.openBook(context: Context) {
    FileUtils.openSavedBook(context, this)
}