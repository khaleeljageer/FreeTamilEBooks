/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.color.DynamicColors
import com.jskaleel.epub.data.BookRepository
import com.jskaleel.epub.data.db.AppDatabase
import com.jskaleel.epub.domain.CoverStorage
import com.jskaleel.epub.reader.EBookReaderRepository
import com.jskaleel.epub.reader.Readium
import com.jskaleel.epub.utils.tryOrLog
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.readium.r2.navigator.preferences.Preference
import timber.log.Timber
import java.io.File

open class EpubApplication : android.app.Application() {

    lateinit var readium: Readium
        private set

    lateinit var bookRepository: BookRepository
        private set

    lateinit var eBookReaderRepository: EBookReaderRepository
        private set

    private val Context.navigatorPreferences: DataStore<Preferences>
            by preferencesDataStore(name = "navigator-preferences")

    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)

        readium = Readium(this)

        val database = AppDatabase.getDatabase(this)
        val coverStorage = CoverStorage(File(this.filesDir.path + "/"))

        bookRepository = BookRepository(database.booksDao())

        val downloadsDir = File(cacheDir, "downloads")

        // Cleans the download dir.
        tryOrLog { downloadsDir.delete() }

        eBookReaderRepository = EBookReaderRepository(
            this@EpubApplication,
            readium,
            bookRepository,
            navigatorPreferences,
            coverStorage
        )
    }
}
