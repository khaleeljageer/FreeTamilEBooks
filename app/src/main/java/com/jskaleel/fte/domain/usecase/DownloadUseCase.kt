package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.model.DownloadResult
import com.jskaleel.fte.data.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DownloadUseCase {
    suspend fun downloadBook(
        id: String,
        url: String,
        fileName: String,
    ): Flow<DownloadResult>
}

class DownloadUseCaseImpl @Inject constructor(private val repository: DownloadRepository) :
    DownloadUseCase {

    override suspend fun downloadBook(
        id: String,
        url: String,
        fileName: String,
    ): Flow<DownloadResult> {
        return repository.downloadBook(id = id, url = url, fileName = fileName)
    }
}