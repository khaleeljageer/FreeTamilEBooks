package com.jskaleel.fte.data.source.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "savedBooks")
data class ESavedBooks(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    var bookid: String,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val image: String,
    @ColumnInfo
    val author: String,
    @ColumnInfo
    val epub: String,
    @ColumnInfo
    val category: String,
    @ColumnInfo
    var path: String
) : Serializable