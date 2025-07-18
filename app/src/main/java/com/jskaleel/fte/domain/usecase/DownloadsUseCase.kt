package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.model.ResultState
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface DownloadsUseCase {
    fun observeDownloadedBooks(): Flow<List<Book>>
    suspend fun deleteBook(bookId: String)
    suspend fun openBook(bookId: Long): ResultState<Long>
}