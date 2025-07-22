package com.jskaleel.fte.data.repository

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.NO_INTERNET_ERROR_CODE
import com.jskaleel.fte.core.model.ResultState
import com.jskaleel.fte.core.model.toTypeString
import com.jskaleel.fte.data.model.BooksDTO
import com.jskaleel.fte.data.source.local.BooksDatabase
import com.jskaleel.fte.data.source.local.entity.BookEntity
import com.jskaleel.fte.data.source.remote.ApiService
import com.jskaleel.fte.data.source.remote.NetworkManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BooksRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val networkManager: NetworkManager,
    private val booksDb: BooksDatabase,
    private val connectivityManager: ConnectivityManager,
) : BooksRepository {
    override suspend fun syncIfNeeded(): ResultState<Unit> {
        val lastStatus = booksDb.syncStatusDao().getStatus()
        return if (lastStatus == null && !isNetworkAvailable()) {
            ResultState.Error(
                "இணையம் இல்லை. முதன்மை தரவுகளை பதிவிறக்க முடியவில்லை.",
                code = NO_INTERNET_ERROR_CODE
            )
        } else {
            val now = System.currentTimeMillis()

            if (lastStatus == null || now - lastStatus.lastSynced >= SYNC_INTERVAL_MS) {
                syncAll()
            } else {
                ResultState.Success(Unit)
            }
        }
    }

    private suspend fun syncAll(): ResultState<Unit> {
        return when (
            val booksResult = networkManager.safeApiCall { apiService.fetchBooks() }
        ) {
            is ResultState.Error -> {
                booksResult
            }

            is ResultState.Success -> {
                val books: List<BooksDTO> = booksResult.data.books
                booksDb.bookDao().insertAll(
                    books = books.map {
                        BookEntity(
                            id = it.bookId,
                            title = it.title,
                            author = it.author,
                            image = ImageType.NetworkImage(
                                url = it.image
                            ).toTypeString(),
                            epub = it.epub,
                            category = it.category
                        )
                    }
                )

                ResultState.Success(Unit)
            }
        }
    }

    override suspend fun observeBooks(): Flow<List<BookEntity>> {
        return booksDb.bookDao().getAll()
    }

    override fun fetchCategories(): Flow<List<String>> {
        return booksDb.bookDao().getCategories()
    }

    override fun fetchBooksByQuery(query: String): Flow<List<BookEntity>> {
        return booksDb.bookDao().getBooksByQuery(query)
    }

    override fun fetchBooksByCategory(category: String): Flow<List<BookEntity>> {
        return booksDb.bookDao().getBooksByCategory(category)
    }

    private fun isNetworkAvailable(): Boolean {
        return connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } ?: false
    }

    companion object {
        private const val SYNC_INTERVAL_MS = 24 * 60 * 60 * 1000L
    }
}
