package com.jskhaleel.reader.domain

import android.net.Uri
import com.jskhaleel.reader.data.BookRepository
import com.jskhaleel.reader.data.model.Book
import com.jskhaleel.reader.utils.tryOrLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.DebugError
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.file.FileSystemError
import org.readium.r2.shared.util.format.Format
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.toUrl
import org.readium.r2.streamer.PublicationOpener
import timber.log.Timber
import java.io.File

class LibraryManager(
    private val bookRepository: BookRepository,
    private val coverStorage: CoverStorage,
    private val publicationOpener: PublicationOpener,
    private val assetRetriever: AssetRetriever,
    private val publicationRetriever: PublicationRetriever
) {
    sealed class ImportResult {
        data object Success : ImportResult()
        data class Error(val error: ImportError) : ImportResult()
    }

    private val coroutineScope: CoroutineScope = MainScope()

//    fun importPublicationFromStorage(uri: Uri): Flow<ImportResult> = flow {
//        emit(handleImportResult(publicationRetriever.retrieveFromStorage(uri)))
//    }.flowOn(Dispatchers.IO)

    suspend fun importPublicationFromStorage(uri: Uri): Long {
        publicationRetriever.retrieveFromStorage(uri)
        handleImportResult()
        return 0L
    }

    fun addPublicationFromStorage(url: AbsoluteUrl): Flow<ImportResult> = flow {
        emit(handleImportResult(url))
    }.flowOn(Dispatchers.IO)

    private suspend fun handleImportResult(
        retrieverResult: Try<PublicationRetriever.Result, ImportError>
    ): ImportResult {
        return retrieverResult
            .map { addBook(it.publication.toUrl(), it.format, it.coverUrl) }
            .fold(
                onSuccess = { ImportResult.Success },
                onFailure = { ImportResult.Error(it) }
            )
    }

    private suspend fun handleImportResult(
        url: AbsoluteUrl,
        format: Format? = null,
        coverUrl: AbsoluteUrl? = null
    ): ImportResult {
        return addBook(url, format, coverUrl).fold(
            onSuccess = { ImportResult.Success },
            onFailure = { ImportResult.Error(it) }
        )
    }

    suspend fun addBook(
        url: AbsoluteUrl,
        format: Format? = null,
        coverUrl: AbsoluteUrl? = null
    ): Try<Unit, ImportError> {
        val asset = if (format == null) {
            assetRetriever.retrieve(url)
        } else {
            assetRetriever.retrieve(url, format)
        }.getOrElse {
            return Try.failure(ImportError.Publication(PublicationError(it)))
        }

        publicationOpener.open(asset, allowUserInteraction = false)
            .onSuccess { publication ->
                val coverFile = coverStorage.storeCover(publication, coverUrl)
                    .getOrElse {
                        return Try.failure(ImportError.FileSystem(FileSystemError.IO(it)))
                    }

                val id = bookRepository.insertBook(
                    url,
                    asset.format.mediaType,
                    publication,
                    coverFile
                )
                if (id == -1L) {
                    coverFile.delete()
                    return Try.failure(
                        ImportError.Database(
                            DebugError("Could not insert book into database.")
                        )
                    )
                }
            }
            .onFailure {
                Timber.e("Cannot open publication: $it.")
                return Try.failure(ImportError.Publication(PublicationError(it)))
            }

        return Try.success(Unit)
    }

    suspend fun deleteBook(book: Book) {
        val id = book.id!!
        bookRepository.deleteBook(id)
        tryOrLog { book.url.toFile()?.delete() }
        tryOrLog { File(book.cover).delete() }
    }
}