package com.epubreader.android

import android.net.Uri
import com.epubreader.android.utils.getOrNull
import com.epubreader.android.utils.onSuccess
import timber.log.Timber
import kotlin.coroutines.suspendCoroutine

class ReaderImpl(private val readerConfig: ReaderConfig) : Reader {
    override suspend fun openBook(id: Long) {
        readerConfig.openBook(id)
    }

    override suspend fun deleteBook(id: Long) {

    }

    override suspend fun importPublicationFromStorage(toUri: Uri): Long {
        return readerConfig.importBookFromUri(toUri).getOrNull() ?: 0L
    }
}