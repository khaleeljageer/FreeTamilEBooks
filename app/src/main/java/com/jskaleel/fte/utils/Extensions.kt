package com.jskaleel.fte.utils

import android.content.Context
import android.view.View
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.model.DownloadResult
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream



fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}