package com.jskaleel.epub.utils

sealed interface ImportResult {
    data class Success(val id: Long) : ImportResult
    data class Failure(val message: String) : ImportResult
}