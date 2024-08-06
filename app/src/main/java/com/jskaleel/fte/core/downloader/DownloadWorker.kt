package com.jskaleel.fte.core.downloader

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val url = inputData.getString("url") ?: return@withContext Result.failure()
        val bookId = inputData.getString("bookId") ?: return@withContext Result.failure()

        try {
            val file = downloadFile(url, bookId)
            if (file != null) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun downloadFile(url: String, bookId: String): File? =
        withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            var outputStream: FileOutputStream? = null

            try {
                // Create a file in the app's internal storage
                val file = File(applicationContext.filesDir, "$bookId.epub")
                Timber.tag("Khaleel").d("Path: ${file.path}")

                // Open a connection to the URL
                connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                // Check if the request is successful
                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext null
                }

                // Open an output stream to the file
                outputStream = FileOutputStream(file)

                // Read the input stream and write to the file
                connection.inputStream.use { input ->
                    val buffer = ByteArray(4 * 1024) // 4KB buffer
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                }

                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                outputStream?.close()
                connection?.disconnect()
            }
        }
}