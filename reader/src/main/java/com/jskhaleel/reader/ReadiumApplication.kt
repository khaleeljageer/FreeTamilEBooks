/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskhaleel.reader

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.color.DynamicColors
import com.jskhaleel.reader.data.BookRepository
import com.jskhaleel.reader.data.db.AppDatabase
import com.jskhaleel.reader.domain.CoverStorage
import com.jskhaleel.reader.domain.LibraryManager
import com.jskhaleel.reader.domain.PublicationRetriever
import com.jskhaleel.reader.reader.ReaderRepository
import com.jskhaleel.reader.utils.tryOrLog
import java.io.File

open class ReadiumApplication : Application() {

    lateinit var readium: Readium
        protected set

    lateinit var storageDir: File
        protected set

    lateinit var bookRepository: BookRepository
        protected set

    lateinit var bookshelf: LibraryManager
        protected set

    lateinit var readerRepository: ReaderRepository
        protected set

    protected val Context.navigatorPreferences: DataStore<Preferences>
            by preferencesDataStore(name = "navigator-preferences")

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        initializeReadium()
        initializeRepositories()
    }

    protected open fun initializeReadium() {
        readium = Readium(this)
        storageDir = computeStorageDir()
    }

    private fun computeStorageDir(): File {
        return File(filesDir?.path + "/")
    }

    protected open fun initializeRepositories() {
        val database = AppDatabase.getDatabase(this)

        bookRepository = BookRepository(database.booksDao())

        val downloadsDir = File(cacheDir, "downloads")

        // Cleans the download dir.
        tryOrLog { downloadsDir.delete() }

        val publicationRetriever = PublicationRetriever(
            context = applicationContext,
            assetRetriever = readium.assetRetriever,
            bookshelfDir = storageDir,
            tempDir = downloadsDir,
            httpClient = readium.httpClient,
            lcpService = readium.lcpService.getOrNull()
        )

        bookshelf = LibraryManager(
            bookRepository,
            CoverStorage(storageDir, httpClient = readium.httpClient),
            readium.publicationOpener,
            readium.assetRetriever,
            publicationRetriever
        )

        readerRepository = ReaderRepository(
            this@ReadiumApplication,
            readium,
            bookRepository,
            navigatorPreferences
        )
    }
}
