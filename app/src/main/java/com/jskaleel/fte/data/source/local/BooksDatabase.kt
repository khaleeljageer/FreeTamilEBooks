package com.jskaleel.fte.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jskaleel.fte.data.source.local.dao.BookDao
import com.jskaleel.fte.data.source.local.dao.DownloadedBookDao
import com.jskaleel.fte.data.source.local.dao.SyncStatusDao
import com.jskaleel.fte.data.source.local.entity.BookEntity
import com.jskaleel.fte.data.source.local.entity.DownloadedBookEntity
import com.jskaleel.fte.data.source.local.entity.SyncStatusEntity

@Database(
    entities = [
        SyncStatusEntity::class,
        DownloadedBookEntity::class,
        BookEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class BooksDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun syncStatusDao(): SyncStatusDao
    abstract fun downloadedBookDao(): DownloadedBookDao
}
