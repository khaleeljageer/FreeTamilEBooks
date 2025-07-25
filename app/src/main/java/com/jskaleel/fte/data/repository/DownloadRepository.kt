package com.jskaleel.fte.data.repository

import com.jskaleel.fte.data.model.DownloadResult
import com.jskaleel.fte.data.source.local.entity.DownloadedBookEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface DownloadRepository {
    val downloadStatus: SharedFlow<DownloadResult>
    fun startDownload(bookId: String, title: String, url: String, format: String = "epub")
    suspend fun removeBook(id: String)
    fun getAllDownloadedBook(): Flow<List<DownloadedBookEntity>>
    suspend fun getBookById(bookId: String): String
    fun fetchRecentReads(): Flow<List<DownloadedBookEntity>>
    suspend fun deleteBook(bookId: String)
    suspend fun getReaderId(bookId: String): Long
    suspend fun updateLastRead(bookId: Long)
}
