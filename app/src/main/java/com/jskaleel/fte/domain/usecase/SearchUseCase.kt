package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.data.repository.BooksRepository
import javax.inject.Inject

interface SearchUseCase {
    suspend fun searchByQuery(
        query: String
    ): Set<String>
}

class SearchUseCaseImpl @Inject constructor(private val repository: BooksRepository) :
    SearchUseCase {
    override suspend fun searchByQuery(query: String): Set<String> {
        return repository.searchByQuery(query = query)
    }
}