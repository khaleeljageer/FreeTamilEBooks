package com.jskaleel.fte.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "localBooks")
data class LocalBooks(
    @ColumnInfo(name = "title")
    val title: String,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "bookid")
    var bookid: String,
    @ColumnInfo(name = "author")
    val author: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "epub")
    val epub: String,
    @ColumnInfo(name = "category")
    val category: String,

    var isExpanded: Boolean = false,

    @ColumnInfo(name = "is_downloaded")
    var isDownloaded: Boolean = false,
    @ColumnInfo(name = "download_id")
    var downloadId: Long = -1L,
    @ColumnInfo(name = "saved_path")
    var savedPath: String? = null
) : Serializable