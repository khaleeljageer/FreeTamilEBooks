package com.jskaleel.fte.core.downloader

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.math.roundToInt

interface FileDownloader {
    suspend fun downloadFile(
        url: String,
        uniqueId: String,
        coroutineScope: CoroutineScope
    ): Flow<DownloadResult>
}

class FileDownloaderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: OkHttpClient
) : FileDownloader {

    override suspend fun downloadFile(
        url: String,
        uniqueId: String,
        coroutineScope: CoroutineScope
    ): Flow<DownloadResult> = flow {
        if (!isValidUrl(url)) {
            emit(DownloadResult.Error(IllegalArgumentException("Invalid URL")))
            return@flow
        }

        val destinationFile = File(context.filesDir, "${uniqueId}.epub")

        val request = Request.Builder().url(url).build()
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                emit(DownloadResult.Error(IOException("Unexpected response ${response.code}")))
                return@flow
            }

            val body = response.body ?: throw IOException("Response body is null")

            val input = body.byteStream()
            val output = destinationFile.outputStream()
            var bytesRead: Int
            var totalBytesRead: Long = 0
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

            while (input.read(buffer).also { bytesRead = it } != -1) {
                if (!coroutineScope.isActive) {
                    throw CancellationException("Download was cancelled")
                }
                output.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
            }

            output.flush()
            output.close()
            input.close()

            emit(DownloadResult.Success(destinationFile))
        } catch (e: CancellationException) {
            destinationFile.delete() // Clean up partial file
            throw e // Re-throw cancellation exception
        } catch (e: Exception) {
            destinationFile.delete() // Clean up partial file
            emit(DownloadResult.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    private fun isValidUrl(url: String): Boolean {
        return try {
            java.net.URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8192
    }
}

sealed interface DownloadResult {
    data class Success(val file: File) : DownloadResult
    data class Error(val exception: Exception) : DownloadResult
    data class Progress(val percentage: Int) : DownloadResult
}