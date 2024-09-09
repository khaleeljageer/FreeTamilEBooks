package com.epubreader.android.data

import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import com.epubreader.android.domain.model.Book
import com.epubreader.android.domain.model.Bookmark
import com.epubreader.android.domain.model.Highlight
import kotlinx.coroutines.flow.Flow
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.mediatype.MediaType

interface BookRepository {
    suspend fun get(id: Long): Book?
    suspend fun deleteBook(id: Long)
    suspend fun deleteBookmark(bookmarkId: Long)
    suspend fun deleteHighlight(id: Long)
    suspend fun saveProgression(
        locator: Locator,
        bookId: Long
    )

    suspend fun updateHighlightAnnotation(
        id: Long,
        annotation: String
    )

    suspend fun updateHighlightStyle(
        id: Long,
        style: Highlight.Style,
        @ColorInt tint: Int
    )

    fun books(): LiveData<List<Book>>
    suspend fun highlightById(id: Long): Highlight?
    fun bookmarksForBook(bookId: Long): LiveData<List<Bookmark>>
    fun highlightsForBook(bookId: Long): Flow<List<Highlight>>
    suspend fun insertBook(
        href: String,
        mediaType: MediaType,
        publication: Publication
    ): Long

    suspend fun insertBookmark(
        bookId: Long,
        publication: Publication,
        locator: Locator
    ): Long

    suspend fun addHighlight(
        bookId: Long,
        style: Highlight.Style,
        @ColorInt tint: Int,
        locator: Locator,
        annotation: String
    ): Long
}