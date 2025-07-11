package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.getDetailedRelativeDateInTamil
import com.jskaleel.fte.core.model.toImage
import com.jskaleel.fte.data.repository.BooksRepository
import com.jskaleel.fte.data.repository.DownloadRepository
import com.jskaleel.fte.domain.model.Book
import com.jskaleel.fte.domain.model.CategoryItem
import com.jskaleel.fte.domain.model.RecentReadItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchUseCaseImpl @Inject constructor(
    private val booksRepository: BooksRepository,
    private val downloadRepository: DownloadRepository,
) : SearchUseCase {
    override fun fetchCategories(): Flow<List<CategoryItem>> {
        return booksRepository.fetchCategories().map { categories ->
            categories.map { category ->
                "$category (${categories.count { it == category }})"
                CategoryItem(
                    name = category,
                    count = categories.count { it == category }
                )
            }.distinct().sortedByDescending { it.count }
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
        return combine(
            booksRepository.fetchBooksByQuery(query),
            downloadRepository.getAllDownloadedBook()
        ) { books, downloadedBooks ->
            books.map { book ->
                Book(
                    id = book.id,
                    title = book.title,
                    author = book.author,
                    image = book.image.toImage(),
                    category = book.category,
                    url = book.epub,
                    downloaded = downloadedBooks.any { it.bookId == book.id }
                )
            }
        }
    }
}