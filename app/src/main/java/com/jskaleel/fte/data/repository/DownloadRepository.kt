package com.jskaleel.fte.data.repository

import com.jskaleel.fte.data.model.DownloadResult
import com.jskaleel.fte.data.source.local.entity.DownloadedBookEntity
import com.jskaleel.fte.domain.model.RecentReadItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface DownloadRepository {
    val downloadStatus: SharedFlow<DownloadResult>
    fun startDownload(bookId: String, title: String, url: String, format: String = "epub")
    suspend fun removeBook(id: String)
    suspend fun getAllDownloadedBook(): Flow<List<DownloadedBookEntity>>
    suspend fun getBookById(bookId: String): String
    fun fetchRecentReads(): Flow<List<DownloadedBookEntity>>
}
