package com.epubreader.android

import android.net.Uri

interface Reader {
    suspend fun openBook(id: Long)
    suspend fun deleteBook(id: Long)
    suspend fun importPublicationFromStorage(toUri: Uri): Long
}

sealed interface FileType {
    data class Url(val url: String) : FileType
    data class Storage(val uri: String) : FileType
    data class Asset(val uri: String) : FileType
}