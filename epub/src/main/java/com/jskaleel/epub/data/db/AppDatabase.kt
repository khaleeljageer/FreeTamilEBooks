/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jskaleel.epub.data.dao.EBookCatalogDao
import com.jskaleel.epub.data.dao.EBooksDao
import com.jskaleel.epub.data.model.EBook
import com.jskaleel.epub.data.model.EBookBookmark
import com.jskaleel.epub.data.model.EBookCatalog
import com.jskaleel.epub.data.model.EBookHighlight
import com.jskaleel.epub.data.model.HighlightConverters

@Database(
    entities = [EBook::class, EBookBookmark::class, EBookHighlight::class, EBookCatalog::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    HighlightConverters::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun booksDao(): EBooksDao

    abstract fun catalogDao(): EBookCatalogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ebooks_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
