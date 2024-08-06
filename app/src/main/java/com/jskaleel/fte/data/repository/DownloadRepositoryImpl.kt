package com.jskaleel.fte.data.repository

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.jskaleel.fte.core.downloader.FileDownloader
import com.jskaleel.fte.core.model.DownloadResult
import com.jskaleel.fte.data.source.local.dao.LocalBooksDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localBooks: LocalBooksDao,
    private val downloadManager: FileDownloader,
) : DownloadRepository {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun downloadBook(
        id: String,
        url: String,
        fileName: String
    ): Flow<DownloadResult> {
        return flow {
            downloadManager.downloadFile(url, id, fileName, CoroutineScope(Dispatchers.IO))
                .collect { result ->
                    when (result) {
                        is DownloadResult.Progress -> {
                            emit(DownloadResult.Progress(id, result.percentage))
                        }

                        is DownloadResult.Success -> {
                            showDownloadSuccessNotification(result.id, result.name)
                            localBooks.updateDownloadStatus(result.id, true)
                            emit(DownloadResult.Success(id, fileName, result.file))
                        }

                        is DownloadResult.Error -> {
                            emit(DownloadResult.Error(id, result.exception))
                        }

                        is DownloadResult.Queued -> {
                            emit(DownloadResult.Queued(id))
                        }
                    }
                }
        }
    }

    override suspend fun removeBook(id: String) {
        localBooks.deleteBookById(bookId = id)
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