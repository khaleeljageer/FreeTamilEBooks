package com.jskaleel.fte.domain.usecase

import com.jskaleel.epub.reader.EBookReaderRepository
import com.jskaleel.epub.utils.IResult
import com.jskaleel.fte.core.model.ResultState
import com.jskaleel.fte.core.model.toImage
import com.jskaleel.fte.data.model.DownloadResult
import com.jskaleel.fte.data.repository.BooksRepository
import com.jskaleel.fte.data.repository.DownloadRepository
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookShelfUseCaseImpl @Inject constructor(
    private val booksRepository: BooksRepository,
    private val downloadRepository: DownloadRepository,
    private val eBookReaderRepository: EBookReaderRepository
) : BookShelfUseCase {

    override val downloadStatus: SharedFlow<DownloadResult>
        get() = downloadRepository.downloadStatus

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
                    readerId = it.readerId,
                )
            }
        }
    }

    override suspend fun deleteBook(bookId: String) {
        downloadRepository.deleteBook(bookId)
    }

    override fun fetchDownloadedBooks(): Flow<List<String>> {
        return downloadRepository.getAllDownloadedBook().map {
            it.map { book ->
                book.bookId
            }
        }
    }

    override suspend fun getReaderId(bookId: String): Long {
        return downloadRepository.getReaderId(bookId)
    }

    override suspend fun openBook(readerId: Long): ResultState<Long> {
        val result = eBookReaderRepository.openBook(readerId)
        return when (result) {
            is IResult.Success -> {
                withContext(Dispatchers.IO) {
                    downloadRepository.updateLastRead(readerId)
                }
                ResultState.Success(result.id)
            }

            is IResult.Failure -> ResultState.Error(result.message)
        }
    }

    override suspend fun syncIfNeeded(): ResultState<Unit> {
        return booksRepository.syncIfNeeded()
    }

    override suspend fun observeBooks(): Flow<List<Book>> {
        return combine(
            booksRepository.observeBooks(),
            downloadRepository.getAllDownloadedBook()
        ) { books, downloads ->
            books.map { book ->
                val localInfo = downloads.firstOrNull { it1 -> it1.bookId == book.id }
                Book(
                    id = book.id,
                    title = book.title,
                    author = book.author,
                    image = book.image.toImage(),
                    url = book.epub,
                    category = book.category,
                    downloaded = localInfo?.bookId == book.id,
                    readerId = localInfo?.readerId ?: -1L,
                )
            }
        }
    }

    override fun startDownload(bookId: String, title: String, url: String) {
        downloadRepository.startDownload(
            bookId = bookId,
            title = title,
            url = url
        )
    }
}