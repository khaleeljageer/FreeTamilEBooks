package com.jskaleel.fte.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jskaleel.fte.data.entities.LocalBooks

@Dao
interface LocalBooksDao {
    @Query("SELECT * from localBooks")
    fun getAllLocalBooks(): List<LocalBooks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(localBooks: LocalBooks)

    @Query("DELETE from localBooks")
    fun deleteAll()

    @Query("SELECT EXISTS(SELECT bookid from localBooks WHERE bookid = :bookId)")
    fun isIdAvailable(bookId: String): Boolean

    @Query("SELECT * from localBooks WHERE bookid = :bookId")
    fun getBookByBookId(bookId: String): LocalBooks

    @Query("SELECT * from localBooks WHERE title LIKE (:key) OR author LIKE (:key)")
    fun getBooksByKey(key: String): List<LocalBooks>
}