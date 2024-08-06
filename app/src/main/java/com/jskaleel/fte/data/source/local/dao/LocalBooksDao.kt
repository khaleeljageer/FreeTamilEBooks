package com.jskaleel.fte.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jskaleel.fte.data.source.local.entities.BookEntity
import com.jskaleel.fte.domain.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalBooksDao {
    @Query("SELECT * from localBooks")
    fun getBooks(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(localBooks: List<BookEntity>)

    @Query("DELETE from localBooks")
    suspend fun deleteAll()

    @Query("DELETE FROM localBooks WHERE bookid = :bookId")
    suspend fun deleteBookById(bookId: String)

    @Query("UPDATE localBooks SET downloaded = :isDownloaded WHERE bookid = :bookId")
    suspend fun updateDownloadStatus(bookId: String, isDownloaded: Boolean)

    @Query("SELECT * from localBooks WHERE downloaded=1")
    fun getDownloadedBooks(): Flow<List<Book>>
}