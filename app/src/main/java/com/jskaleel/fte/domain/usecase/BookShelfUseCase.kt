package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.model.ResultState
import com.jskaleel.fte.data.model.DownloadResult
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface BookShelfUseCase {
    suspend fun syncIfNeeded(): ResultState<Unit>
    suspend fun observeBooks(): Flow<List<Book>>
    fun startDownload(bookId: String, title: String, url: String)
    val downloadStatus: SharedFlow<DownloadResult>
    suspend fun observeDownloadedBooks(): Flow<List<Book>>
    suspend fun deleteBook(bookId: String)
    fun fetchDownloadedBooks(): Flow<List<String>>
}