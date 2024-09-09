package com.epubreader.android.data

import android.content.Context
import androidx.datastore.core.DataStore
import com.epubreader.android.reader.Readium
import com.epubreader.android.reader.DummyReaderInitData
import com.epubreader.android.reader.EpubReaderInitData
import com.epubreader.android.reader.ReaderInitData
import com.epubreader.android.reader.VisualReaderInitData
import com.epubreader.android.reader.preferences.EpubPreferencesManagerFactory
import org.json.JSONObject
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.asset.FileAsset
import org.readium.r2.shared.publication.services.isRestricted
import org.readium.r2.shared.publication.services.protectionError
import org.readium.r2.shared.util.Try
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import androidx.datastore.preferences.core.Preferences as JetpackPreferences

/**
 * Open and store publications in order for them to be listened or read.
 *
 * Ensure you call [open] before any attempt to start a [ReaderActivity].
 * Pass the method result to the activity to enable it to know which current publication it must
 * retrieve from this repository - media or visual.
 */
@OptIn(ExperimentalReadiumApi::class)
class ReaderRepositoryImpl @Inject constructor(
    private val readium: Readium,
    private val bookRepositoryImpl: BookRepositoryImpl,
    private val preferencesDataStore: DataStore<JetpackPreferences>,
) : ReaderRepository {
    object CancellationException : Exception()

    private val repository: MutableMap<Long, ReaderInitData> =
        mutableMapOf()

    override fun getReaderInit(bookId: Long): ReaderInitData {
        Timber.tag("Khaleel").d("getReaderInit : $bookId")
        return repository[bookId] ?: DummyReaderInitData(bookId = bookId)
    }

    override suspend fun open(bookId: Long, context: Context): Try<Unit, Exception> {
        return try {
            openThrowing(bookId, context)
            Try.success(Unit)
        } catch (e: Exception) {
            Try.failure(e)
        }
    }

    override fun close(bookId: Long) {
        when (val initData = repository.remove(bookId)) {
            is VisualReaderInitData -> {
                initData.publication.close()
            }

            null, is DummyReaderInitData -> {
                // Do nothing
            }
        }
    }

    private suspend fun openThrowing(bookId: Long, context: Context) {
        if (bookId in repository.keys) {
            return
        }

        val book = bookRepositoryImpl.get(bookId)
            ?: throw Exception("Cannot find book in database.")

        val file = File(book.href)
        require(file.exists())
        val asset = FileAsset(file)

        val publication =
            readium.streamer.open(asset, allowUserInteraction = true, sender = context)
                .getOrThrow()

        // The publication is protected with a DRM and not unlocked.
        if (publication.isRestricted) {
            throw publication.protectionError
                ?: CancellationException
        }

        val initialLocator = book.progression?.let { Locator.fromJSON(JSONObject(it)) }

        val readerInitData = when {
            publication.conformsTo(Publication.Profile.EPUB) ->
                openEpub(bookId, publication, initialLocator)

            else ->
                throw Exception("Publication is not supported.")
        }

        repository[bookId] = readerInitData
    }

    private suspend fun openEpub(
        bookId: Long,
        publication: Publication,
        initialLocator: Locator?
    ): EpubReaderInitData {
        val preferencesManager =
            EpubPreferencesManagerFactory(preferencesDataStore).createPreferenceManager(bookId)
        val navigatorFactory = EpubNavigatorFactory(publication)

        return EpubReaderInitData(
            bookId, publication, initialLocator,
            preferencesManager, navigatorFactory
        )
    }
}
