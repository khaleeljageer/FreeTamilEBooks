package com.jskaleel.fte.data.repository

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

class BooksRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService,
    private val localBooks: LocalBooksDao,
    private val appPreferenceStore: AppPreferenceStore,
    private val downloadManager: FileDownloader,
) : BooksRepository {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

    override suspend fun downloadBook(id: String, url: String, fileName: String) {
        downloadManager.downloadFile(url, id, CoroutineScope(Dispatchers.IO))
            .collect { result ->
                when (result) {
                    is DownloadResult.Progress -> {
                        Timber.tag("Khaleel").d("Progress: ${result.percentage}")
                    }

                    is DownloadResult.Success -> {
                        Timber.tag("Khaleel").d("Success: ${result.file.path}")
                        showDownloadSuccessNotification(id, fileName)
                    }

                    is DownloadResult.Error -> {
                        Timber.tag("Khaleel").d("Success: ${result.exception}")
                    }
                }
            }
    }

    private fun showDownloadSuccessNotification(id: String, fileName: String) {
        val notification = NotificationCompat.Builder(context, "download_channel")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(fileName)
            .setContentText("Download complete successfully")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id.hashCode(), notification)
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