package com.jskaleel.fte.utils

object Constants {
    const val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    const val BASE_URL = "https://raw.githubusercontent.com/KaniyamFoundation/Free-Tamil-Ebooks/"

    const val CHANNEL_NAME = "fte_books"

    object SharedPreference {
        val FILE_NAME: String
            get() = "com.jskaleel.fte.PREFERENCE"

        val NEW_BOOKS_UPDATE: String
            get() = "com.jskaleel.fte.NEW_BOOKS_UPDATE"
    }
}