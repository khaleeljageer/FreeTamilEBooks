package com.jskaleel.fte.utils.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.jskaleel.fte.R
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.utils.PrintLog

object DownloadUtil {

    fun queueForDownload(context: Context, book: LocalBooks) {

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
        request.setDestinationUri(Uri.parse("file://$extStorageDirectory/${book.bookid}.epub"))
        val id = downloadManager.enqueue(request)

        DownloadManagerHelper.saveDownload(context, id)
    }
}