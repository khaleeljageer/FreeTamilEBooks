package com.jskaleel.fte.data.repository

import com.jskaleel.fte.core.model.DownloadResult
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    suspend fun downloadBook(id: String, url: String, fileName: String): Flow<DownloadResult>
    suspend fun removeBook(id: String)
}