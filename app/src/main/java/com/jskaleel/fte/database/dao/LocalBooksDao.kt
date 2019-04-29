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

    @Query("SELECT * from localBooks WHERE title LIKE :title")
    fun getBooksByTitle(title: String): List<LocalBooks>

    @Query("SELECT * from localBooks WHERE author LIKE :author")
    fun getBooksByAuthor(author: String): List<LocalBooks>

    @Query("SELECT * from localBooks WHERE download_id = :downloadId")
    fun getDownloadedBook(downloadId: Long): LocalBooks

    @Query("UPDATE localBooks SET download_id = :downloadId, saved_path = :filePath WHERE bookid = :bookId")
    fun updateDownloadDetails(filePath: String, downloadId: Long, bookId: String)

    @Query("UPDATE localBooks SET is_downloaded = :isDownloaded WHERE download_id = :downloadId")
    fun updateStatus(isDownloaded: Boolean, downloadId: Long)
}