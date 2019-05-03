package com.jskaleel.fte.utils.downloader

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.model.CheckForDownloadsMenu
import com.jskaleel.fte.model.DownloadCompleted
import com.jskaleel.fte.utils.PrintLog
import com.jskaleel.fte.utils.RxBus

class DownloadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
        if (id !in DownloadManagerHelper.getDownloads(context)) {
            return
        }
        val localBooksDao = AppDatabase.getAppDatabase(context.applicationContext).localBooksDao()
        val file = DownloadManagerHelper.getDownloadedFile(context, id)
        if (file.isSuccessful()) {
            localBooksDao.updateStatus(true, id)
            val newBook = localBooksDao.getDownloadedBook(id)
            RxBus.publish(DownloadCompleted(id))
            RxBus.publish(CheckForDownloadsMenu(newBook.bookid))
        }
    }
}