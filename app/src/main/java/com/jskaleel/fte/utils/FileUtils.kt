package com.jskaleel.fte.utils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.folioreader.Config
import com.folioreader.FolioReader
import com.folioreader.util.AppUtil
import com.jskaleel.fte.R
import com.jskaleel.fte.data.entities.DownloadResult
import com.jskaleel.fte.data.entities.LocalBooks
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.*

object FileUtils {
    fun getRootDirPath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file: File = ContextCompat.getExternalFilesDirs(
                context.applicationContext,
                null
            )[0]
            file.absolutePath
        } else {
            context.applicationContext.filesDir.absolutePath
        } + "/books"
    }

    fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long): String {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes)
    }

    private fun getBytesToMBString(bytes: Long): String {
        return java.lang.String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
    }

    fun downloadBook(
        context: Context, book: LocalBooks
    ): DownloadResult {
        val filePath = File(getRootDirPath(context), "${book.bookid}.epub")
        val request = Request.Builder().url(book.epub).build()
        try {
            val response = OkHttpClient().newCall(request).execute()
            if (response.body() != null) {
                val buffer = response.body()!!.byteStream()
                val outputStream = FileOutputStream(filePath)
                outputStream.use { output ->
                    val bufferSize = ByteArray(4 * 1024)
                    while (true) {
                        val byteCount = buffer.read(bufferSize)
                        if (byteCount < 0) break
                        output.write(bufferSize, 0, byteCount)
                    }
                    output.flush()
                }
            }

            return DownloadResult.Success(filePath)
        } catch (e: Exception) {
            e.printStackTrace()
            return DownloadResult.Error("Something went wrong.")
        }
    }

    fun openSavedBook(context: Context, book: LocalBooks) {
        if (book.savedPath == null) {
            return
        }
        var config = AppUtil.getSavedConfig(context)
        if (config == null) config = Config()
        config.allowedDirection = Config.AllowedDirection.VERTICAL_AND_HORIZONTAL
        config.isShowTts = false
        config.setThemeColorInt(ContextCompat.getColor(context, R.color.fte_blue_700))
        val folioReader = FolioReader.get()
        folioReader.setConfig(config, true).openBook(book.savedPath!!.replace("file://", ""))
    }
}