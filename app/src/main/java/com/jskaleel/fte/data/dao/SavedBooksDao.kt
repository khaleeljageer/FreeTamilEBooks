package com.jskaleel.fte.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jskaleel.fte.data.entities.SavedBooks

@Dao
interface SavedBooksDao {
    @Query("SELECT * from savedBooks")
    fun getAllLocalBooks(): LiveData<List<SavedBooks>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(savedBook: SavedBooks)

    @Query("SELECT EXISTS(SELECT bookid from savedBooks WHERE bookid = :bookId)")
    fun isIdAvailable(bookId: String): Boolean


    @Query("SELECT * from savedBooks WHERE bookid = :bookId")
    fun getBookByBookId(bookId: String): SavedBooks

    @Query("UPDATE savedBooks SET saved_path = :filePath WHERE bookid = :bookId")
    fun addBookDetails(filePath: String, bookId: String)
}