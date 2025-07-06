package com.jskaleel.fte.data.model

import java.io.File

sealed interface DownloadResult {
    data class Queued(val id: String) : DownloadResult
    data class Progress(val id: String, val percent: Int) : DownloadResult
    data class Success(val id: String, val file: File, val title: String) : DownloadResult
    data class Error(val id: String, val message: String) : DownloadResult
}
