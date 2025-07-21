package com.jskaleel.fte.domain.usecase

import com.jskaleel.epub.reader.EBookReaderRepository
import com.jskaleel.epub.utils.IResult
import com.jskaleel.fte.core.model.ResultState
import com.jskaleel.fte.core.model.toImage
import com.jskaleel.fte.data.repository.DownloadRepository
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DownloadsUseCaseImpl @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val eBookReaderRepository: EBookReaderRepository
) : DownloadsUseCase {

    override fun observeDownloadedBooks(): Flow<List<Book>> {
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

    override suspend fun openBook(readerId: Long): ResultState<Long> {
        val result = eBookReaderRepository.openBook(readerId)
        return when (result) {
            is IResult.Success -> {
                withContext(Dispatchers.IO) {
                    downloadRepository.updateLastRead(result.id)
                }
                ResultState.Success(result.id)
            }

            is IResult.Failure -> ResultState.Error(result.message)
        }
    }

    override suspend fun updateLastRead(readerId: Long) {
        downloadRepository.updateLastRead(readerId)
    }
}