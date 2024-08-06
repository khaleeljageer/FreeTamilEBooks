package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.data.repository.BooksRepository
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetBooksUseCase {
    suspend fun getBooks(): Flow<List<Book>>
}

class GetBooksUseCaseImpl @Inject constructor(private val repository: BooksRepository) :
    GetBooksUseCase {
    override suspend fun getBooks(): Flow<List<Book>> {
        return repository.getBooks()
    }
}