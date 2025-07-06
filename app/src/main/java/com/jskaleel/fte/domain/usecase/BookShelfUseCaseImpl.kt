package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.model.ResultState
import com.jskaleel.fte.core.model.toImage
import com.jskaleel.fte.data.model.DownloadResult
import com.jskaleel.fte.data.repository.BooksRepository
import com.jskaleel.fte.data.repository.DownloadRepository
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookShelfUseCaseImpl @Inject constructor(
    private val booksRepository: BooksRepository,
    private val downloadRepository: DownloadRepository,
) : BookShelfUseCase {

    override val downloadStatus: SharedFlow<DownloadResult>
        get() = downloadRepository.downloadStatus

    override suspend fun syncIfNeeded(): ResultState<Unit> {
        return booksRepository.syncIfNeeded()
    }

    override suspend fun observeBooks(): Flow<List<Book>> {
        val downloads = downloadRepository.getAllDownloadedBook().firstOrNull().orEmpty()
        return booksRepository.observeBooks().map { list ->
            list.map {
                val localInfo = downloads.firstOrNull { it1 -> it1.bookId == it.id }
                Book(
                    id = it.id,
                    title = it.title,
                    author = it.author,
                    image = it.image.toImage(),
                    url = it.epub,
                    category = it.category,
                    downloaded = localInfo?.bookId == it.id,
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