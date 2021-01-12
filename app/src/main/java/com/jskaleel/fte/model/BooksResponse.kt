package com.jskaleel.fte.model

import com.jskaleel.fte.database.entities.LocalBooks
import java.io.File

data class BooksResponse(val books: List<LocalBooks>)

sealed class DownloadResult {
    data class Error(val errorMessage: String) : DownloadResult()
    data class Success(val filePath: File) : DownloadResult()
}