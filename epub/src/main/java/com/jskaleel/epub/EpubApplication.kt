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
import com.jskaleel.epub.reader.ReaderRepository
import com.jskaleel.epub.reader.Readium
import com.jskaleel.epub.utils.tryOrLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import timber.log.Timber
import java.io.File
import java.util.Properties

class EpubApplication : android.app.Application() {

    lateinit var readium: Readium
        private set

    lateinit var storageDir: File

    lateinit var bookRepository: BookRepository
        private set

    lateinit var readerRepository: ReaderRepository
        private set

    private val coroutineScope: CoroutineScope =
        MainScope()

    private val Context.navigatorPreferences: DataStore<Preferences>
            by preferencesDataStore(name = "navigator-preferences")

    override fun onCreate() {
        Timber.plant(Timber.DebugTree())

        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)

        readium = Readium(this)

        storageDir = computeStorageDir()

        val database = AppDatabase.getDatabase(this)

        bookRepository = BookRepository(database.booksDao())

        val downloadsDir = File(cacheDir, "downloads")

        // Cleans the download dir.
        tryOrLog { downloadsDir.delete() }

        readerRepository = ReaderRepository(
            this@EpubApplication,
            readium,
            bookRepository,
            navigatorPreferences
        )
    }

    private fun computeStorageDir(): File {
        val properties = Properties()
        val inputStream = assets.open("configs/config.properties")
        properties.load(inputStream)
        val useExternalFileDir =
            properties.getProperty("useExternalFileDir", "false")!!.toBoolean()

        return File(
            if (useExternalFileDir) {
                getExternalFilesDir(null)?.path + "/"
            } else {
                filesDir?.path + "/"
            }
        )
    }
}
