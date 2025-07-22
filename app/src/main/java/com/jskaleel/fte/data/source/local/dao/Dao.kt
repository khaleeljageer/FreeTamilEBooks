package com.jskaleel.fte.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.jskaleel.fte.data.source.local.entity.BookEntity
import com.jskaleel.fte.data.source.local.entity.DownloadedBookEntity
import com.jskaleel.fte.data.source.local.entity.SyncStatusEntity
import kotlinx.coroutines.flow.Flow

@Suppress("detekt:MaxLineLength")
@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<BookEntity>)

    @Query(value = "SELECT * FROM books")
    fun getAll(): Flow<List<BookEntity>>

    @Query(value = "SELECT * FROM books WHERE id = :id")
    suspend fun getById(id: String): BookEntity

    @Query(value = "SELECT category FROM books")
    fun getCategories(): Flow<List<String>>

    @Query(
        value = "SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' OR  category LIKE '%' || :query || '%'"
    )
    fun getBooksByQuery(query: String): Flow<List<BookEntity>>

    @Query(value = "SELECT * FROM books WHERE category = :category")
    fun getBooksByCategory(category: String): Flow<List<BookEntity>>
}

@Dao
interface SyncStatusDao {
    @Query(value = "SELECT * FROM sync_status WHERE id = 0")
    suspend fun getStatus(): SyncStatusEntity?
}

@Dao
interface DownloadedBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DownloadedBookEntity)

    @Query(value = "SELECT * FROM downloaded_books WHERE bookId = :bookId")
    suspend fun get(bookId: String): DownloadedBookEntity?

    @Query(value = "SELECT * FROM downloaded_books ORDER BY timestamp DESC")
    fun getAll(): Flow<List<DownloadedBookEntity>>

    @Query(value = "DELETE FROM downloaded_books WHERE bookId = :bookId")
    suspend fun delete(bookId: String)

    @Query(value = "SELECT * FROM downloaded_books WHERE lastRead != 0 ORDER BY lastRead DESC")
    fun getRecentReads(): Flow<List<DownloadedBookEntity>>

    @Query(value = "SELECT * FROM downloaded_books WHERE readerId = :readerId")
    fun getBookByReaderId(readerId: Long): DownloadedBookEntity?

    @Upsert
    suspend fun upsert(entity: DownloadedBookEntity)
}
