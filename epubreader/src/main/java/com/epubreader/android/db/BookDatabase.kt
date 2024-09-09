package com.epubreader.android.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.epubreader.android.domain.model.Book
import com.epubreader.android.domain.model.Bookmark
import com.epubreader.android.domain.model.Catalog
import com.epubreader.android.domain.model.Highlight
import com.epubreader.android.domain.model.HighlightConverters

@Database(
    entities = [Book::class, Bookmark::class, Highlight::class, Catalog::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(HighlightConverters::class)
abstract class BookDatabase : RoomDatabase() {

    abstract fun booksDao(): BooksDao
    abstract fun catalogDao(): CatalogDao
}
