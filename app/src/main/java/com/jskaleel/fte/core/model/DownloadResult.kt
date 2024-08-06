package com.jskaleel.fte.core.model

import java.io.File

sealed interface DownloadResult {
    data class Queued(val id: String) : DownloadResult
    data class Progress(val id: String, val percentage: Int) : DownloadResult
    data class Success(val id: String, val name: String, val file: File) : DownloadResult
    data class Error(val id: String, val exception: Exception) : DownloadResult
}