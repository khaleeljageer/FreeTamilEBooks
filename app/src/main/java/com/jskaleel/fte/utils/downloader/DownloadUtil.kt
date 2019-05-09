package com.jskaleel.fte.utils.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.model.CheckForDownloadsMenu
import com.jskaleel.fte.utils.PrintLog
import com.jskaleel.fte.utils.RxBus
import org.geometerplus.android.fbreader.FBReader
import java.io.File

object DownloadUtil {

    fun queueForDownload(context: Context, book: LocalBooks): Long {
        val localBooksDao = AppDatabase.getAppDatabase(context).localBooksDao()

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val url = book.epub
        PrintLog.info("Download : url $url")
        val request = DownloadManager.Request(Uri.parse(url))
        val extStorageDirectory = context.getExternalFilesDir("downloads")?.absolutePath
        request.setTitle(book.title)
        request.setDescription(context.getString(R.string.downloading))

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setVisibleInDownloadsUi(false)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE or DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val filePath = "file://$extStorageDirectory/${book.bookid}.epub"
        request.setDestinationUri(Uri.parse(filePath))
        val id = downloadManager.enqueue(request)

        DownloadManagerHelper.saveDownload(context, id)

        localBooksDao.updateDownloadDetails("$extStorageDirectory/${book.bookid}.epub", id, book.bookid)
        return id
    }

    fun openSavedBook(context: Context, book: LocalBooks) {
        PrintLog.info("savedPath ${book.savedPath}")
        FBReader.openBookActivity(context, book.savedPath)
    }

    fun removeDownload(context: Context, book: LocalBooks): LocalBooks {
        val localBooksDao = AppDatabase.getAppDatabase(context).localBooksDao()
        val filePath = File(book.savedPath)
        filePath.delete()
        localBooksDao.updateStatusByBookId(false, -1L, "", book.bookid)
        PreferencesHelper.removeDownload(context, book.downloadId)
        RxBus.publish(CheckForDownloadsMenu(book.bookid))
        return localBooksDao.getBookByBookId(book.bookid)
    }
}