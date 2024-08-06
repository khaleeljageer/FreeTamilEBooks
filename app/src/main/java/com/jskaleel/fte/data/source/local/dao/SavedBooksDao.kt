package com.jskaleel.fte.data.source.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jskaleel.fte.data.source.local.entities.ESavedBooks

@Dao
interface SavedBooksDao {
    @Query("SELECT * from savedBooks")
    fun getSavedBooks(): LiveData<List<ESavedBooks>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(savedBook: ESavedBooks)

    @Query("SELECT EXISTS(SELECT bookid from savedBooks WHERE bookid = :bookId)")
    fun isIdAvailable(bookId: String): Boolean
}