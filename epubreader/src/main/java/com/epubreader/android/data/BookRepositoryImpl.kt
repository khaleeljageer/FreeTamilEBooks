package com.epubreader.android.data

import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import com.epubreader.android.db.BooksDao
import com.epubreader.android.domain.model.Book
import com.epubreader.android.domain.model.Bookmark
import com.epubreader.android.domain.model.Highlight
import com.epubreader.android.utils.extensions.authorName
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.indexOfFirstWithHref
import org.readium.r2.shared.util.mediatype.MediaType
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(private val booksDao: BooksDao) : BookRepository {

    override fun books(): LiveData<List<Book>> = booksDao.getAllBooks()

    override suspend fun get(id: Long): Book? = booksDao.get(id)

    override suspend fun insertBook(
        href: String,
        mediaType: MediaType,
        publication: Publication
    ): Long {
        val book = Book(
            creation = DateTime().toDate().time,
            title = publication.metadata.title,
            author = publication.metadata.authorName,
            href = href,
            identifier = publication.metadata.identifier ?: "",
            type = mediaType.toString(),
            progression = "{}"
        )
        return booksDao.insertBook(book)
    }

    override suspend fun deleteBook(id: Long): Unit = booksDao.deleteBook(id)

    override suspend fun saveProgression(locator: Locator, bookId: Long): Unit =
        booksDao.saveProgression(locator.toJSON().toString(), bookId)

    override suspend fun insertBookmark(
        bookId: Long,
        publication: Publication,
        locator: Locator
    ): Long {
        val resource = publication.readingOrder.indexOfFirstWithHref(locator.href)!!
        val bookmark = Bookmark(
            creation = DateTime().toDate().time,
            bookId = bookId,
            publicationId = publication.metadata.identifier ?: publication.metadata.title,
            resourceIndex = resource.toLong(),
            resourceHref = locator.href,
            resourceType = locator.type,
            resourceTitle = locator.title.orEmpty(),
            location = locator.locations.toJSON().toString(),
            locatorText = Locator.Text().toJSON().toString()
        )

        return booksDao.insertBookmark(bookmark)
    }

    override fun bookmarksForBook(bookId: Long): LiveData<List<Bookmark>> =
        booksDao.getBookmarksForBook(bookId)

    override suspend fun deleteBookmark(bookmarkId: Long): Unit =
        booksDao.deleteBookmark(bookmarkId)

    override suspend fun highlightById(id: Long): Highlight? =
        booksDao.getHighlightById(id)

    override fun highlightsForBook(bookId: Long): Flow<List<Highlight>> =
        booksDao.getHighlightsForBook(bookId)

    override suspend fun addHighlight(
        bookId: Long,
        style: Highlight.Style,
        @ColorInt tint: Int,
        locator: Locator,
        annotation: String
    ): Long =
        booksDao.insertHighlight(Highlight(bookId, style, tint, locator, annotation))

    override suspend fun deleteHighlight(id: Long): Unit = booksDao.deleteHighlight(id)

    override suspend fun updateHighlightAnnotation(id: Long, annotation: String) {
        booksDao.updateHighlightAnnotation(id, annotation)
    }

    override suspend fun updateHighlightStyle(
        id: Long,
        style: Highlight.Style,
        @ColorInt tint: Int
    ) {
        booksDao.updateHighlightStyle(id, style, tint)
    }
}
