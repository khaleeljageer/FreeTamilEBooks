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

    @Query("SELECT EXISTS(SELECT bookid from localBooks WHERE bookid = :bookId)")
    fun isIdAvailable(bookId: String): Boolean

    @Query("SELECT * from localBooks WHERE title LIKE :title")
    fun getBooksByTitle(title: String): List<LocalBooks>

    @Query("SELECT * from localBooks WHERE author LIKE :author")
    fun getBooksByAuthor(author: String): List<LocalBooks>

    @Query("SELECT * from localBooks WHERE bookid = :bookId")
    fun getBookByBookId(bookId: String): LocalBooks

    @Query("UPDATE localBooks SET saved_path = :filePath WHERE bookid = :bookId")
    fun updateDownloadDetails(filePath: String, bookId: String)

    @Query("UPDATE localBooks SET is_downloaded = :isDownloaded, saved_path = :filePath WHERE bookid = :bookId")
    fun updateStatusByBookId(isDownloaded: Boolean, filePath: String, bookId: String)

    @Query("SELECT * from localBooks WHERE is_downloaded = :isDownloaded")
    fun getDownloadedBooks(isDownloaded: Boolean): List<LocalBooks>
}