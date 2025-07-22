package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.model.ResultState
import com.jskaleel.fte.data.model.DownloadResult
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.model.CategoryItem
import com.jskaleel.fte.domain.model.RecentReadItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface SearchUseCase {
    val downloadStatus: SharedFlow<DownloadResult>
    fun fetchCategories(): Flow<List<CategoryItem>>
    fun fetchRecentReads(): Flow<List<RecentReadItem>>
    fun fetchBooksByQuery(query: String): Flow<List<Book>>
    fun fetchBooksByCategory(category: String): Flow<List<Book>>
    fun startDownload(bookId: String, title: String, url: String)
    fun fetchDownloadedBooks(): Flow<List<String>>
    suspend fun getReaderId(bookId: String): Long
    suspend fun openBook(readerId: Long): ResultState<Long>
}
