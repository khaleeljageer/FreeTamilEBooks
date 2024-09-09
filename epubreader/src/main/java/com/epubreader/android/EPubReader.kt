package com.epubreader.android

import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import timber.log.Timber

object EPubReader {
    private var readerConfig: ReaderConfig? = null

    fun init(context: Context) {
        val application = context.applicationContext as Application
//        DynamicColors.applyToActivitiesIfAvailable(application)
        this.readerConfig = ReaderConfig(application)
    }

    fun getReader(): Reader {
        readerConfig ?: throw IllegalStateException("Readium config not initialized")
        return ReaderImpl(readerConfig!!)
    }
}