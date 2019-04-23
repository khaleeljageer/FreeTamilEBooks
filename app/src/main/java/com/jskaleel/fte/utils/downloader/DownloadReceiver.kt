package com.jskaleel.fte.utils.downloader

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.utils.PrintLog

class DownloadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
        if (id !in DownloadManagerHelper.getDownloads(context)) {
            return
        }
        val localBooksDao = AppDatabase.getAppDatabase(context).localBooksDao()
        val file = DownloadManagerHelper.getDownloadedFile(context, id)
        if (file.isSuccessful()) {
            PrintLog.info("Download Completed $id")
            PrintLog.info("${localBooksDao.getDownloadedBook(id)}")
            localBooksDao.updateStatus(true, id)
            PrintLog.info("${localBooksDao.getDownloadedBook(id)}")
        }
    }
}
