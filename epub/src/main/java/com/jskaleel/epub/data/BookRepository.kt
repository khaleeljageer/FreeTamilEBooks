/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.data

import androidx.annotation.ColorInt
import java.io.File
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.indexOfFirstWithHref
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.mediatype.MediaType
import com.jskaleel.epub.data.dao.EBooksDao
import com.jskaleel.epub.data.model.EBook
import com.jskaleel.epub.data.model.EBookBookmark
import com.jskaleel.epub.data.model.EBookHighlight
import com.jskaleel.epub.utils.extensions.readium.authorName

class BookRepository(
    private val booksDao: EBooksDao,
) {
    fun books(): Flow<List<EBook>> = booksDao.getAllBooks()

    suspend fun get(id: Long) = booksDao.get(id)

    suspend fun saveProgression(locator: Locator, bookId: Long) =
        booksDao.saveProgression(locator.toJSON().toString(), bookId)

    suspend fun insertBookmark(bookId: Long, publication: Publication, locator: Locator): Long {
        val resource = publication.readingOrder.indexOfFirstWithHref(locator.href)!!
        val bookmark = EBookBookmark(
            creation = DateTime().toDate().time,
            bookId = bookId,
            resourceIndex = resource.toLong(),
            resourceHref = locator.href.toString(),
            resourceType = locator.mediaType.toString(),
            resourceTitle = locator.title.orEmpty(),
            location = locator.locations.toJSON().toString(),
            locatorText = Locator.Text().toJSON().toString()
        )

        return booksDao.insertBookmark(bookmark)
    }

    fun bookmarksForBook(bookId: Long): Flow<List<EBookBookmark>> =
        booksDao.getBookmarksForBook(bookId)

    suspend fun deleteBookmark(bookmarkId: Long) = booksDao.deleteBookmark(bookmarkId)

    suspend fun highlightById(id: Long): EBookHighlight? =
        booksDao.getHighlightById(id)

    fun highlightsForBook(bookId: Long): Flow<List<EBookHighlight>> =
        booksDao.getHighlightsForBook(bookId)

    suspend fun addHighlight(
        bookId: Long,
        style: EBookHighlight.Style,
        @ColorInt tint: Int,
        locator: Locator,
        annotation: String,
    ): Long =
        booksDao.insertHighlight(EBookHighlight(bookId, style, tint, locator, annotation))

    suspend fun deleteHighlight(id: Long) = booksDao.deleteHighlight(id)

    suspend fun updateHighlightAnnotation(id: Long, annotation: String) {
        booksDao.updateHighlightAnnotation(id, annotation)
    }

    suspend fun updateHighlightStyle(id: Long, style: EBookHighlight.Style, @ColorInt tint: Int) {
        booksDao.updateHighlightStyle(id, style, tint)
    }

    suspend fun insertBook(
        url: Url,
        mediaType: MediaType,
        publication: Publication,
        cover: File,
    ): Long {
        val eBook = EBook(
            creation = DateTime().toDate().time,
            title = publication.metadata.title ?: url.filename,
            author = publication.metadata.authorName,
            href = url.toString(),
            identifier = publication.metadata.identifier ?: "",
            mediaType = mediaType,
            progression = "{}",
            cover = cover.path
        )
        return booksDao.insertBook(eBook)
    }

    suspend fun deleteBook(id: Long) =
        booksDao.deleteBook(id)
}
