package com.jskaleel.fte.data.repository

import com.jskaleel.fte.core.model.ResultState
import com.jskaleel.fte.data.source.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    suspend fun syncIfNeeded(): ResultState<Unit>
    suspend fun observeBooks(): Flow<List<BookEntity>>
    fun fetchCategories(): Flow<List<String>>
    fun fetchBooksByQuery(query: String): Flow<List<BookEntity>>
    fun fetchBooksByCategory(category: String): Flow<List<BookEntity>>
}
