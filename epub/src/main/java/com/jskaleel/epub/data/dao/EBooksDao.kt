/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.data.dao

import androidx.annotation.ColorInt
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jskaleel.epub.data.model.EBookBookmark
import com.jskaleel.epub.data.model.EBook
import com.jskaleel.epub.data.model.EBookHighlight
import kotlinx.coroutines.flow.Flow

@Dao
interface EBooksDao {

    /**
     * Inserts a book
     * @param EBook The book to insert
     * @return ID of the book that was added (primary key)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(eBook: EBook): Long

    /**
     * Deletes a book
     * @param bookId The ID of the book
     */
    @Query("DELETE FROM " + EBook.TABLE_NAME + " WHERE " + EBook.ID + " = :bookId")
    suspend fun deleteBook(bookId: Long)

    /**
     * Retrieve a book from its ID.
     */
    @Query("SELECT * FROM " + EBook.TABLE_NAME + " WHERE " + EBook.ID + " = :id")
    suspend fun get(id: Long): EBook?

    /**
     * Retrieve all books
     * @return List of books as Flow
     */
    @Query("SELECT * FROM " + EBook.TABLE_NAME + " ORDER BY " + EBook.CREATION_DATE + " desc")
    fun getAllBooks(): Flow<List<EBook>>

    /**
     * Retrieve all bookmarks for a specific book
     * @param bookId The ID of the book
     * @return List of bookmarks for the book as Flow
     */
    @Query("SELECT * FROM " + EBookBookmark.TABLE_NAME + " WHERE " + EBookBookmark.BOOK_ID + " = :bookId")
    fun getBookmarksForBook(bookId: Long): Flow<List<EBookBookmark>>

    /**
     * Retrieve all highlights for a specific book
     */
    @Query(
        "SELECT * FROM ${EBookHighlight.TABLE_NAME} WHERE ${EBookHighlight.BOOK_ID} = :bookId ORDER BY ${EBookHighlight.TOTAL_PROGRESSION} ASC"
    )
    fun getHighlightsForBook(bookId: Long): Flow<List<EBookHighlight>>

    /**
     * Retrieves the highlight with the given ID.
     */
    @Query("SELECT * FROM ${EBookHighlight.TABLE_NAME} WHERE ${EBookHighlight.ID} = :highlightId")
    suspend fun getHighlightById(highlightId: Long): EBookHighlight?

    /**
     * Inserts a bookmark
     * @param bookmark The bookmark to insert
     * @return The ID of the bookmark that was added (primary key)
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(bookmark: EBookBookmark): Long

    /**
     * Inserts a highlight
     * @param highlight The highlight to insert
     * @return The ID of the highlight that was added (primary key)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: EBookHighlight): Long

    /**
     * Updates a highlight's annotation.
     */
    @Query(
        "UPDATE ${EBookHighlight.TABLE_NAME} SET ${EBookHighlight.ANNOTATION} = :annotation WHERE ${EBookHighlight.ID} = :id"
    )
    suspend fun updateHighlightAnnotation(id: Long, annotation: String)

    /**
     * Updates a highlight's tint and style.
     */
    @Query(
        "UPDATE ${EBookHighlight.TABLE_NAME} SET ${EBookHighlight.TINT} = :tint, ${EBookHighlight.STYLE} = :style WHERE ${EBookHighlight.ID} = :id"
    )
    suspend fun updateHighlightStyle(id: Long, style: EBookHighlight.Style, @ColorInt tint: Int)

    /**
     * Deletes a bookmark
     */
    @Query("DELETE FROM " + EBookBookmark.TABLE_NAME + " WHERE " + EBookBookmark.ID + " = :id")
    suspend fun deleteBookmark(id: Long)

    /**
     * Deletes the highlight with given id.
     */
    @Query("DELETE FROM ${EBookHighlight.TABLE_NAME} WHERE ${EBookHighlight.ID} = :id")
    suspend fun deleteHighlight(id: Long)

    /**
     * Saves book progression
     * @param locator Location of the book
     * @param id The book to update
     */
    @Query(
        "UPDATE " + EBook.TABLE_NAME + " SET " + EBook.PROGRESSION + " = :locator WHERE " + EBook.ID + "= :id"
    )
    suspend fun saveProgression(locator: String, id: Long)
}
