package com.jskaleel.fte.data.entities

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

    @ColumnInfo(name = "downloaded")
    var isDownloaded: Boolean,
    @ColumnInfo(name = "saved_path")
    var savedPath: String,

    var isClicked: Boolean = false
) : Serializable