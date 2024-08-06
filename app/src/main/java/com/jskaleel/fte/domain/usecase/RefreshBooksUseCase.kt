package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.data.repository.BooksRepository
import javax.inject.Inject


interface RefreshBooksUseCase {
    suspend fun refreshBooks()
}

class RefreshBooksUseCaseImpl @Inject constructor(private val repository: BooksRepository) :
    RefreshBooksUseCase {
    override suspend fun refreshBooks() {
        repository.refreshBooks()
    }
}