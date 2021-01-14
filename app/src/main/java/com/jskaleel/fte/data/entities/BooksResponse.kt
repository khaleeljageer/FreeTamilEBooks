package com.jskaleel.fte.data.entities

import java.io.File
import java.io.Serializable

data class BooksResponse(val books: List<LocalBooks>) : Serializable

sealed class DownloadResult {
    data class Error(val errorMessage: String) : DownloadResult()
    data class Success(val filePath: File) : DownloadResult()
}