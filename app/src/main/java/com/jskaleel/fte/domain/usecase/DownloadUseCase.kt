package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.data.repository.BooksRepository
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DownloadUseCase {
    suspend fun downloadBook(
        id: String,
        url: String,
        fileName: String,
    )
}

class DownloadUseCaseImpl @Inject constructor(private val repository: BooksRepository) :
    DownloadUseCase {

    override suspend fun downloadBook(
        id: String,
        url: String,
        fileName: String,
    ) {
        repository.downloadBook(id = id, url = url, fileName = fileName)
    }
}