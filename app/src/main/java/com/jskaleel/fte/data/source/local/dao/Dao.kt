package com.jskaleel.fte.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jskaleel.fte.data.source.local.entity.BookEntity
import com.jskaleel.fte.data.source.local.entity.DownloadedBookEntity
import com.jskaleel.fte.data.source.local.entity.SyncStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<BookEntity>)

    @Query("SELECT * FROM books")
    fun getAll(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getById(id: String): BookEntity

    @Query("SELECT category FROM books")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun getBooksByQuery(query: String): Flow<List<BookEntity>>

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteById(bookId: String)

    @Query("SELECT * FROM books WHERE category = :category")
    fun getBooksByCategory(category: String): Flow<List<BookEntity>>
}

@Dao
interface SyncStatusDao {
    @Query("SELECT * FROM sync_status WHERE id = 0")
    suspend fun getStatus(): SyncStatusEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(status: SyncStatusEntity)
}

@Dao
interface DownloadedBookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DownloadedBookEntity)

    @Query("SELECT * FROM downloaded_books WHERE bookId = :bookId")
    suspend fun get(bookId: String): DownloadedBookEntity?

    @Query("SELECT * FROM downloaded_books ORDER BY timestamp DESC")
    fun getAll(): Flow<List<DownloadedBookEntity>>

    @Query("DELETE FROM downloaded_books WHERE bookId = :bookId")
    suspend fun delete(bookId: String)

    @Query("SELECT * FROM downloaded_books WHERE lastRead != 0 ORDER BY lastRead DESC")
    fun getRecentReads(): Flow<List<DownloadedBookEntity>>
}
