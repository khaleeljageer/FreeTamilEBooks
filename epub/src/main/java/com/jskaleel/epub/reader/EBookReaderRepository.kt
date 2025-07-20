/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.reader

import android.app.Application
import androidx.datastore.core.DataStore
import com.jskaleel.epub.data.BookRepository
import com.jskaleel.epub.domain.CoverStorage
import com.jskaleel.epub.domain.ImportError
import com.jskaleel.epub.domain.PublicationError
import com.jskaleel.epub.reader.preferences.AndroidTtsPreferencesManagerFactory
import com.jskaleel.epub.reader.preferences.EpubPreferencesManagerFactory
import com.jskaleel.epub.utils.CoroutineQueue
import com.jskaleel.epub.utils.IResult
import org.json.JSONObject
import org.readium.navigator.media.tts.TtsNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.allAreHtml
import org.readium.r2.shared.publication.services.isRestricted
import org.readium.r2.shared.publication.services.protectionError
import org.readium.r2.shared.util.DebugError
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.file.FileSystemError
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.toUrl
import timber.log.Timber
import java.io.File
import androidx.datastore.preferences.core.Preferences as JetpackPreferences

/**
 * Open and store publications in order for them to be listened or read.
 *
 * Ensure you call [open] before any attempt to start a [ReaderActivity].
 * Pass the method result to the activity to enable it to know which current publication it must
 * retrieve from this repository - media or visual.
 */
@OptIn(ExperimentalReadiumApi::class)
class EBookReaderRepository(
    private val application: Application,
    private val readium: Readium,
    private val bookRepository: BookRepository,
    private val preferencesDataStore: DataStore<JetpackPreferences>,
    private val coverStorage: CoverStorage,
) {

    private val coroutineQueue: CoroutineQueue = CoroutineQueue()

    private val repository: MutableMap<Long, ReaderInitData> =
        mutableMapOf()

    private val mediaServiceFacade: MediaServiceFacade =
        MediaServiceFacade(application)

    fun isEmpty() =
        repository.isEmpty()

    operator fun get(bookId: Long): ReaderInitData? =
        repository[bookId]

    private suspend fun open(bookId: Long): Try<Unit, OpeningError> =
        coroutineQueue.await { doOpen(bookId) }

    suspend fun openBook(readerId: Long): IResult {
        return open(readerId).fold(
            onSuccess = {
                IResult.Success(readerId)
            },
            onFailure = { error ->
                IResult.Failure(error.message)
            }
        )
    }

    suspend fun importBook(file: File): IResult {
        return loadBook(file).fold(
            onSuccess = { bookId ->
                IResult.Success(bookId)
            },
            onFailure = { error ->
                IResult.Failure(error.message)
            }
        )
    }

    private suspend fun loadBook(file: File): Try<Long, ImportError> {
        val asset = readium.assetRetriever.retrieve(file).getOrElse {
            return Try.failure(
                ImportError.Publication(
                    PublicationError(it)
                )
            )
        }

        val publication = readium.publicationOpener
            .open(asset, allowUserInteraction = false)
            .getOrElse {
                return Try.failure(
                    ImportError.Publication(PublicationError(it))
                )
            }

        val coverFile = coverStorage.storeCover(publication).getOrElse {
            return Try.failure(
                ImportError.FileSystem(
                    FileSystemError.IO(it)
                )
            )
        }

        val id = bookRepository.insertBook(
            file.absoluteFile.toUrl(),
            asset.format.mediaType,
            publication,
            coverFile
        )
        if (id == -1L) {
            return Try.failure(
                ImportError.Database(
                    DebugError("Failed to insert book into the database.")
                )
            )
        }

        return Try.success(id)
    }

    private suspend fun doOpen(bookId: Long): Try<Unit, OpeningError> {
        if (bookId in repository.keys) {
            return Try.success(Unit)
        }

        val book = checkNotNull(bookRepository.get(bookId)) { "Cannot find book in database." }

        val asset = readium.assetRetriever.retrieve(
            book.url,
            book.mediaType
        ).getOrElse {
            return Try.failure(
                OpeningError.PublicationError(
                    PublicationError(it)
                )
            )
        }

        val publication = readium.publicationOpener.open(
            asset,
            allowUserInteraction = true
        ).getOrElse {
            return Try.failure(
                OpeningError.PublicationError(
                    PublicationError(it)
                )
            )
        }

        // The publication is protected with a DRM and not unlocked.
        if (publication.isRestricted) {
            return Try.failure(
                OpeningError.RestrictedPublication(
                    publication.protectionError
                        ?: DebugError("Publication is restricted.")
                )
            )
        }

        val initialLocator = book.progression
            ?.let { Locator.fromJSON(JSONObject(it)) }

        val readerInitData = when {

            publication.conformsTo(Publication.Profile.EPUB) || publication.readingOrder.allAreHtml ->
                openEpub(bookId, publication, initialLocator)

            else ->
                Try.failure(
                    OpeningError.CannotRender(
                        DebugError("No navigator supports this publication.")
                    )
                )
        }

        return readerInitData.map {
            repository[bookId] = it
        }
    }

    private suspend fun openEpub(
        bookId: Long,
        publication: Publication,
        initialLocator: Locator?,
    ): Try<EpubReaderInitData, OpeningError> {
        val preferencesManager = EpubPreferencesManagerFactory(preferencesDataStore)
            .createPreferenceManager(bookId)
        val navigatorFactory = EpubNavigatorFactory(publication)
        val ttsInitData = getTtsInitData(bookId, publication)

        val initData = EpubReaderInitData(
            bookId,
            publication,
            initialLocator,
            preferencesManager,
            navigatorFactory,
            ttsInitData
        )
        return Try.success(initData)
    }

    private suspend fun getTtsInitData(
        bookId: Long,
        publication: Publication,
    ): TtsInitData? {
        val preferencesManager = AndroidTtsPreferencesManagerFactory(preferencesDataStore)
            .createPreferenceManager(bookId)
        val navigatorFactory = TtsNavigatorFactory(
            application,
            publication
        ) ?: return null
        return TtsInitData(mediaServiceFacade, navigatorFactory, preferencesManager)
    }

    fun close(bookId: Long) {
        coroutineQueue.launch {
            Timber.v("Closing Publication $bookId.")
            when (val initData = repository.remove(bookId)) {
                is VisualReaderInitData -> {
                    mediaServiceFacade.closeSession()
                    initData.publication.close()
                }

                null, is DummyReaderInitData -> {
                    // Do nothing
                }
            }
        }
    }
}
