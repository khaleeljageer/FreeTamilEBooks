package com.jskaleel.fte.data.source.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "localBooks")
data class BookEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    val bookid: String,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val author: String,
    @ColumnInfo
    val image: String,
    @ColumnInfo
    val epub: String,
    @ColumnInfo
    val category: String,
) : Serializable