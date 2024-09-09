package com.epubreader.android.utils

sealed class ReaderError(val msg: String) : Throwable(msg)

class LocalReaderError(msg: String) : ReaderError(msg) {
    override fun toString(): String = "LocalException: $msg"
}

class ApiReaderError(msg: String) : ReaderError(msg) {
    override fun toString(): String = "ApiException: $msg"
}