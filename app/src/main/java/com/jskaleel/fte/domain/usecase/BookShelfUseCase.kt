package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.model.ResultState
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookShelfUseCase {
    suspend fun syncIfNeeded(): ResultState<Unit>
    suspend fun observeBooks(): Flow<List<Book>>
}