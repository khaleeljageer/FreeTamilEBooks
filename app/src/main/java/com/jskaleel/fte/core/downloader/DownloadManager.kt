package com.jskaleel.fte.core.downloader

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface DownloadManager {
    fun queueDownload(url: String, uniqueId: String)
}

class DownloadManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DownloadManager {
    private val downloadQueue = mutableListOf<DownloadInfo>()
    private var isDownloading = false

    override fun queueDownload(url: String, uniqueId: String) {
        downloadQueue.add(DownloadInfo(url, uniqueId))
        if (!isDownloading) {
            startDownload()
        }
    }

    private fun startDownload() {
        if (downloadQueue.isEmpty()) {
            isDownloading = false
            return
        }

        isDownloading = true
        val downloadInfo = downloadQueue.removeFirst()

        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(
                workDataOf(
                    "url" to downloadInfo.url,
                    "bookId" to downloadInfo.bookId
                )
            )
            .build()

        WorkManager.getInstance(context)
            .enqueue(downloadWorkRequest)
            .result
            .addListener({ startDownload() }, ContextCompat.getMainExecutor(context))
    }
}

data class DownloadInfo(
    val url: String,
    val bookId: String,
)