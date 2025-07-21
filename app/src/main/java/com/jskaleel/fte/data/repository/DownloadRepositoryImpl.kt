package com.jskaleel.fte.data.repository

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.jskaleel.epub.reader.EBookReaderRepository
import com.jskaleel.epub.utils.IResult
import com.jskaleel.fte.core.downloader.FileDownloader
import com.jskaleel.fte.core.getDownloadDir
import com.jskaleel.fte.data.model.DownloadResult
import com.jskaleel.fte.data.source.local.dao.BookDao
import com.jskaleel.fte.data.source.local.dao.DownloadedBookDao
import com.jskaleel.fte.data.source.local.entity.DownloadedBookEntity
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
    private val downloadDao: DownloadedBookDao,
    private val bookDao: BookDao,
    private val fileDownloader: FileDownloader,
    private val eBookReaderRepository: EBookReaderRepository,
) : DownloadRepository {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val _downloadStatus = MutableSharedFlow<DownloadResult>(extraBufferCapacity = 10)
    override val downloadStatus: SharedFlow<DownloadResult> = _downloadStatus
    private val lastProgressMap = mutableMapOf<String, Int>()

    override fun startDownload(bookId: String, title: String, url: String, format: String) {
        CoroutineScope(Dispatchers.IO).launch {
            fileDownloader.downloadFile(
                url = url,
                uniqueId = bookId,
                fileName = "$title.$format",
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
                        val file = File(context.getDownloadDir(), "$bookId.$format")
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
                parseEBook(file = status.file, bookId = status.id)
            }
        }
    }

    private fun parseEBook(file: File, bookId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val book = bookDao.getById(bookId)
            val importResult = eBookReaderRepository.importBook(file)
            when (importResult) {
                is IResult.Success -> {
                    downloadDao.insert(
                        DownloadedBookEntity(
                            bookId = book.id,
                            filePath = file.path,
                            title = book.title,
                            author = book.author,
                            category = book.category,
                            image = book.image,
                            timestamp = System.currentTimeMillis(),
                            readerId = importResult.id,
                        )
                    )
                }

                is IResult.Failure -> {
                    emitStatus(DownloadResult.Error(bookId, "புத்தகத்தை திறக்க முடியவில்லை."))
                }
            }
        }
    }

    override suspend fun removeBook(id: String) {
        downloadDao.delete(bookId = id)
    }

    override fun getAllDownloadedBook(): Flow<List<DownloadedBookEntity>> {
        return downloadDao.getAll()
    }

    override suspend fun getBookById(bookId: String): String {
        return downloadDao.get(bookId)?.filePath ?: ""
    }

    override fun fetchRecentReads(): Flow<List<DownloadedBookEntity>> {
        return downloadDao.getRecentReads()
    }

    override suspend fun deleteBook(bookId: String) {
        val book = downloadDao.get(bookId)
        if (book != null) {
            val file = File(book.filePath)
            if (file.exists()) {
                file.delete()
            }
            downloadDao.delete(bookId)
        }
    }

    override suspend fun getReaderId(bookId: String): Long {
        return downloadDao.get(bookId)?.readerId ?: -1L
    }

    override suspend fun updateLastRead(bookId: Long) {
        downloadDao.getBookByReaderId(bookId)?.let { book ->
            downloadDao.upsert(
                book.copy(lastRead = System.currentTimeMillis())
            )
        }
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
