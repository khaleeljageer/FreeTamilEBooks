package com.jskaleel.fte.data.repository

import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    fun getBooks(): Flow<List<Book>>
    suspend fun refreshBooks()
}