package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.ResultState
import com.jskaleel.fte.core.model.toImage
import com.jskaleel.fte.data.repository.BooksRepository
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookShelfUseCaseImpl @Inject constructor(
    private val booksRepository: BooksRepository,
) : BookShelfUseCase {
    override suspend fun syncIfNeeded(): ResultState<Unit> {
        return booksRepository.syncIfNeeded()
    }

    override suspend fun observeBooks(): Flow<List<Book>> {
        return booksRepository.observeBooks().map { list ->
            list.map {
                Book(
                    id = it.id,
                    title = it.title,
                    author = it.author,
                    image = it.image.toImage(),
                    url = it.epub,
                    category = it.category
                )
            }
        }
    }
}