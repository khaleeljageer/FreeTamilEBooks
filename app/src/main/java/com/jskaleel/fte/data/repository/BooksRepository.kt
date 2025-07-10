package com.jskaleel.fte.data.repository

import com.jskaleel.fte.core.model.ResultState
import com.jskaleel.fte.data.source.local.entity.BookEntity
import com.jskaleel.fte.domain.model.RecentReadItem
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    suspend fun syncIfNeeded(): ResultState<Unit>
    suspend fun observeBooks(): Flow<List<BookEntity>>
    fun fetchCategories(): Flow<List<String>>
}
