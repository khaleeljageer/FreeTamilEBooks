package com.jskaleel.fte.database.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val category: String
)