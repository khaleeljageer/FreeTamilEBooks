package com.jskaleel.fte.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.jskaleel.fte.database.entities.LocalBooks

@Dao
interface LocalBooksDao {
    @Query("SELECT * from localBooks")
    fun getAllLocalBooks(): List<LocalBooks>

    @Insert(onConflict = REPLACE)
    fun insert(localBooks: LocalBooks)

    @Query("DELETE from localBooks")
    fun deleteAll()

    @Query("SELECT * from localBooks WHERE bookid = :bookId")
    fun isIdAvailable(bookId: String): Boolean
}