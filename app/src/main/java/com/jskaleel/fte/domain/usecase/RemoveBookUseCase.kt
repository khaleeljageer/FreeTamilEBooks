package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.data.repository.BooksRepository
import com.jskaleel.fte.data.repository.DownloadRepository
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RemoveBookUseCase {
    suspend fun removeBook(id: String)
}

class RemoveBookUseCaseImpl @Inject constructor(private val repository: DownloadRepository) :
    RemoveBookUseCase {
    override suspend fun removeBook(id: String) {
        repository.removeBook(id = id)
    }
}