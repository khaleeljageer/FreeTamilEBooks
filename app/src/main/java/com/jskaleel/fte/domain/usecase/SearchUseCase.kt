package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.model.CategoryItem
import com.jskaleel.fte.domain.model.RecentReadItem
import kotlinx.coroutines.flow.Flow

interface SearchUseCase {
    fun fetchCategories(): Flow<List<CategoryItem>>
    fun fetchRecentReads(): Flow<List<RecentReadItem>>
    fun fetchBooksByQuery(query: String): Flow<List<Book>>
}