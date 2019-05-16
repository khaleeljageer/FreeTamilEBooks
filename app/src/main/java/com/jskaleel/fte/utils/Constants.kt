package com.jskaleel.fte.utils

object Constants {
    const val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"

    const val CHANNEL_NAME = "fte_books"
    const val LIST_BY_DEFAULT = 101
    const val LIST_BY_CATEGORY = 102

    object SharedPreference {
        val FILE_NAME: String
            get() = "com.jskaleel.fte.PREFERENCE"

        val NEW_BOOKS_UPDATE: String
            get() = "com.jskaleel.fte.NEW_BOOKS_UPDATE"

        val IS_CATEGORY_MODIFIED: String
            get() = "com.jskaleel.fte.IS_CATEGORY_MODIFIED"

        val BOOK_LIST_TYPE: String
            get() = "com.jskaleel.fte.BOOK_LIST_TYPE"
    }
}