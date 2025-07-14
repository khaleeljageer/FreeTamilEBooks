package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.getDetailedRelativeDateInTamil
import com.jskaleel.fte.core.model.toImage
import com.jskaleel.fte.data.model.DownloadResult
import com.jskaleel.fte.data.repository.BooksRepository
import com.jskaleel.fte.data.repository.DownloadRepository
import com.jskaleel.fte.data.source.local.entity.BookEntity
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.model.CategoryItem
import com.jskaleel.fte.domain.model.RecentReadItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchUseCaseImpl @Inject constructor(
    private val booksRepository: BooksRepository,
    private val downloadRepository: DownloadRepository,
) : SearchUseCase {
    override val downloadStatus: SharedFlow<DownloadResult>
        get() = downloadRepository.downloadStatus

    override fun fetchCategories(): Flow<List<CategoryItem>> {
        return booksRepository.fetchCategories()
            .map { categories ->
                categories
                    .map { category ->
                        "$category (${categories.count { it == category }})"
                        CategoryItem(
                            name = category,
                            count = categories.count { it == category }
                        )
                    }
                    .distinct()
                    .sortedByDescending { it.count }
            }
    }

    override fun fetchRecentReads(): Flow<List<RecentReadItem>> {
        return downloadRepository.fetchRecentReads().map { books ->
            books.map { book ->
                RecentReadItem(
                    id = book.bookId,
                    title = book.title,
                    image = book.image.toImage(),
                    lastRead = getDetailedRelativeDateInTamil(book.lastRead)
                )
            }
        }
    }

    override fun fetchBooksByQuery(query: String): Flow<List<Book>> {
        return booksRepository.fetchBooksByQuery(query).map {
            it.map { book: BookEntity ->
                Book(
                    id = book.id,
                    title = book.title,
                    author = book.author,
                    image = book.image.toImage(),
                    category = book.category,
                    url = book.epub,
                    downloaded = false
                )
            }
        }
    }

    override fun fetchBooksByCategory(category: String): Flow<List<Book>> {
        return booksRepository.fetchBooksByCategory(category).map {
            it.map { book ->
                Book(
                    id = book.id,
                    title = book.title,
                    author = book.author,
                    image = book.image.toImage(),
                    category = book.category,
                    url = book.epub,
                    downloaded = false
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

    override fun fetchDownloadedBooks(): Flow<List<String>> {
        return downloadRepository.getAllDownloadedBook().map {
            it.map { book ->
                book.bookId
            }
        }
    }
}