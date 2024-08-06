package com.jskaleel.fte.data.repository

import com.jskaleel.fte.core.downloader.FileDownloader
import com.jskaleel.fte.core.downloader.DownloadResult
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.toImage
import com.jskaleel.fte.core.model.toTypeString
import com.jskaleel.fte.data.model.BookDto
import com.jskaleel.fte.data.source.datastore.AppPreferenceStore
import com.jskaleel.fte.data.source.local.dao.LocalBooksDao
import com.jskaleel.fte.data.source.local.entities.BookEntity
import com.jskaleel.fte.data.source.remote.ApiService
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

class BooksRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val localBooks: LocalBooksDao,
    private val appPreferenceStore: AppPreferenceStore,
    private val downloadManager: FileDownloader,
) : BooksRepository {
    override fun getBooks(): Flow<List<Book>> {
        return localBooks.getBooks().map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun refreshBooks() {
        val lastSyncInfo = appPreferenceStore.getLastSync()
        val currentTime = System.currentTimeMillis()
        val oneHourInMillis = 60 * 60 * 1000
        if (lastSyncInfo == null || currentTime - lastSyncInfo > oneHourInMillis) {
            val response = apiService.getBooks()
            if (response.code() == HttpsURLConnection.HTTP_OK) {
                response.body()?.books?.map {
                    it.toEntity()
                }?.also {
                    localBooks.deleteAll()
                    localBooks.insert(it)
                    appPreferenceStore.setLastSync()
                }
            }
        }
    }

    override suspend fun downloadBook(id: String, url: String) {
        downloadManager.downloadFile(url, id, CoroutineScope(Dispatchers.IO))
            .collect { result ->
                when (result) {
                    is DownloadResult.Progress -> {
                        Timber.tag("Khaleel").d("Progress: ${result.percentage}")
                    }

                    is DownloadResult.Success -> {
                        Timber.tag("Khaleel").d("Success: ${result.file.path}")
                    }

                    is DownloadResult.Error -> {
                        Timber.tag("Khaleel").d("Success: ${result.exception}")
                    }
                }
            }
    }
}


private fun BookEntity.toDomain() = Book(
    bookid = bookid,
    title = title,
    author = author,
    image = image.toImage(),
    epub = epub,
    category = category,
)

private fun BookDto.toEntity() =
    BookEntity(
        bookid = bookid,
        title = title,
        author = author,
        image = ImageType.NetworkImage(image).toTypeString(),
        epub = epub,
        category = category
    )