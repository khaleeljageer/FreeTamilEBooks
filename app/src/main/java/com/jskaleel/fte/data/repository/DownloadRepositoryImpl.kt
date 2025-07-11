package com.jskaleel.fte.data.repository

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.jskaleel.fte.core.downloader.FileDownloader
import com.jskaleel.fte.data.model.DownloadResult
import com.jskaleel.fte.data.source.local.BooksDatabase
import com.jskaleel.fte.data.source.local.entity.DownloadedBookEntity
import com.jskaleel.fte.domain.model.RecentReadItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: BooksDatabase,
    private val fileDownloader: FileDownloader
) : DownloadRepository {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val _downloadStatus = MutableSharedFlow<DownloadResult>(extraBufferCapacity = 10)
    override val downloadStatus: SharedFlow<DownloadResult> = _downloadStatus
    private val lastProgressMap = mutableMapOf<String, Int>()

    override fun startDownload(bookId: String, title: String, url: String, format: String) {
        val file = File(getDownloadDir(), "$title.${format}")
        CoroutineScope(Dispatchers.IO).launch {
            fileDownloader.downloadFile(
                url = url,
                uniqueId = bookId,
                fileName = "${title}.${format}",
                coroutineScope = this
            ).collect { result ->
                when (result) {
                    is DownloadResult.Queued -> emitStatus(DownloadResult.Queued(bookId))
                    is DownloadResult.Progress -> {
                        val percent = result.percent
                        emitStatus(DownloadResult.Progress(bookId, percent))
                    }

                    is DownloadResult.Success -> {
                        showDownloadSuccessNotification(result.id, result.title)
                        emitStatus(DownloadResult.Success(bookId, file, title))
                    }

                    is DownloadResult.Error -> {
                        emitStatus(DownloadResult.Error(bookId, result.message))
                    }
                }
            }
        }
    }

    private fun emitStatus(status: DownloadResult) {
        CoroutineScope(Dispatchers.IO).launch {
            if (status is DownloadResult.Progress) {
                val last = lastProgressMap[status.id]
                if (last == status.percent) return@launch
                lastProgressMap[status.id] = status.percent
            }
            _downloadStatus.emit(status)
            if (status is DownloadResult.Success) {
                val book = database.bookDao().getById(status.id)
                database.downloadedBookDao().insert(
                    DownloadedBookEntity(
                        bookId = book.id,
                        filePath = status.file.path,
                        title = book.title,
                        author = book.author,
                        category = book.category,
                        image = book.image,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    private fun getDownloadDir(): File {
        return File(context.filesDir, "downloads").apply {
            if (!exists()) mkdirs()
        }
    }

    override suspend fun removeBook(id: String) {
        database.downloadedBookDao().delete(bookId = id)
    }

    override fun getAllDownloadedBook(): Flow<List<DownloadedBookEntity>> {
        return database.downloadedBookDao().getAll()
    }

    override suspend fun getBookById(bookId: String): String {
        return database.downloadedBookDao().get(bookId)?.filePath ?: ""
    }

    override fun fetchRecentReads(): Flow<List<DownloadedBookEntity>> {
        return database.downloadedBookDao().getRecentReads()
    }

    private fun showDownloadSuccessNotification(id: String, fileName: String) {
        val notification = NotificationCompat.Builder(context, "download_channel")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(fileName)
            .setContentText("புத்தகம் பதிவிறக்கப்பட்டது...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id.hashCode(), notification)
    }
}
