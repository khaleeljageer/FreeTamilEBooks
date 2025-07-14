package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.model.toImage
import com.jskaleel.fte.data.repository.BooksRepository
import com.jskaleel.fte.data.repository.DownloadRepository
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DownloadsUseCaseImpl @Inject constructor(
    private val downloadRepository: DownloadRepository,
) : DownloadsUseCase {

    override suspend fun observeDownloadedBooks(): Flow<List<Book>> {
        return downloadRepository.getAllDownloadedBook().map { list ->
            list.map {
                Book(
                    title = it.title,
                    url = "",
                    id = it.bookId,
                    downloaded = true,
                    author = it.author,
                    image = it.image.toImage(),
                    category = it.category,
                    readerId = it.readerId
                )
            }
        }
    }

    override suspend fun deleteBook(bookId: String) {
        downloadRepository.deleteBook(bookId)
    }
}