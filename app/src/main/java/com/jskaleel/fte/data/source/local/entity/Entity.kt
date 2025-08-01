package com.jskaleel.fte.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val image: String,
    val epub: String,
    val category: String,
)

@Entity(tableName = "sync_status")
data class SyncStatusEntity(
    @PrimaryKey val id: Int = 0,
    val lastSynced: Long
)

@Entity(tableName = "downloaded_books")
data class DownloadedBookEntity(
    @PrimaryKey val bookId: String,
    val filePath: String,
    val author: String,
    val category: String,
    val title: String,
    val image: String,
    val timestamp: Long,
    val lastRead: Long = 0L,
    val readerId: Long = 0L,
)
