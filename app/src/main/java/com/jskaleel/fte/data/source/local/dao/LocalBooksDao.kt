package com.jskaleel.fte.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jskaleel.fte.data.source.local.entities.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalBooksDao {
    @Query("SELECT * from localBooks")
    fun getBooks(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(localBooks: List<BookEntity>)

    @Query("DELETE from localBooks")
    suspend fun deleteAll()
}