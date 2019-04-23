package com.jskaleel.fte.utils.downloader

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor

object DownloadManagerHelper {
    
    fun getDownloadedFile(context: Context, id: Long): DownloadedFile {

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val query = DownloadManager.Query()
        query.setFilterById(id)

        val cursor = downloadManager.query(query)
        return if (cursor.moveToFirst()) {
            cursorToDownloadedFile(cursor)
        } else {
            DownloadedFile.cancelled(id)
        }
    }

    private fun cursorToDownloadedFile(cursor: Cursor): DownloadedFile {

        val id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
        val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
        val totalSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
        val downloadedSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
        val lastModifiedAt = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP))

        return DownloadedFile(id, title, status, reason, totalSize, downloadedSize, lastModifiedAt)
    }

    fun getDownloads(context: Context): Set<Long> {
        return PreferencesHelper.getDownloads(context)
    }

    fun removeDownload(context: Context, id: Long) {
        PreferencesHelper.removeDownload(context, id)
    }

    fun saveDownload(context: Context, id: Long) {
        PreferencesHelper.saveDownload(context, id)
    }

}