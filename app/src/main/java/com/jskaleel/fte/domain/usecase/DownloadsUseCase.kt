package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface DownloadsUseCase {
    suspend fun observeDownloadedBooks(): Flow<List<Book>>
}