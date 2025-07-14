/*
 * Copyright 2023 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.jskaleel.epub.domain

import android.content.Context
import android.net.Uri
import com.jskaleel.epub.utils.extensions.copyToTempFile
import com.jskaleel.epub.utils.extensions.moveTo
import com.jskaleel.epub.utils.tryOrLog
import org.readium.r2.lcp.LcpService
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.asset.ResourceAsset
import org.readium.r2.shared.util.data.ReadError
import org.readium.r2.shared.util.file.FileSystemError
import org.readium.r2.shared.util.format.Format
import org.readium.r2.shared.util.format.FormatHints
import org.readium.r2.shared.util.format.Specification
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.http.HttpClient
import org.readium.r2.shared.util.mediatype.MediaType
import timber.log.Timber
import java.io.File
import java.util.UUID

/**
 * Retrieves a publication from a remote or local source and import it into the bookshelf storage.
 *
 * If the source file is a LCP license document, the protected publication will be downloaded.
 */
class PublicationRetriever(
    context: Context,
    private val assetRetriever: AssetRetriever,
    lcpService: LcpService?,
    private val bookshelfDir: File,
    private val tempDir: File,
) {
    data class Result(
        val publication: File,
        val format: Format,
        val coverUrl: AbsoluteUrl?,
    )

    private val localPublicationRetriever: LocalPublicationRetriever =
        LocalPublicationRetriever(context, tempDir, assetRetriever, lcpService)

    suspend fun retrieveFromStorage(
        uri: Uri,
    ): Try<Result, ImportError> {
        val localResult = localPublicationRetriever
            .retrieve(uri)
            .getOrElse { return Try.failure(it) }

        val finalResult = moveToBookshelfDir(
            localResult.tempFile,
            localResult.format,
            localResult.coverUrl
        )
            .getOrElse {
                tryOrLog { localResult.tempFile.delete() }
                return Try.failure(it)
            }

        return Try.success(
            Result(finalResult.publication, finalResult.format, finalResult.coverUrl)
        )
    }

    private suspend fun moveToBookshelfDir(
        tempFile: File,
        format: Format?,
        coverUrl: AbsoluteUrl?,
    ): Try<Result, ImportError> {
        val actualFormat = format
            ?: assetRetriever.sniffFormat(tempFile)
                .getOrElse {
                    return Try.failure(ImportError.Publication(PublicationError(it)))
                }

        val fileName = "${UUID.randomUUID()}.${actualFormat.fileExtension.value}"
        val bookshelfFile = File(bookshelfDir, fileName)

        try {
            tempFile.moveTo(bookshelfFile)
        } catch (e: Exception) {
            Timber.d(e)
            tryOrLog { bookshelfFile.delete() }
            return Try.failure(
                ImportError.Publication(
                    PublicationError.Reading(
                        ReadError.Access(FileSystemError.IO(e))
                    )
                )
            )
        }

        return Try.success(
            Result(bookshelfFile, actualFormat, coverUrl)
        )
    }
}

/**
 * Retrieves a publication from a file (publication or LCP license document) stored on the device.
 */
private class LocalPublicationRetriever(
    private val context: Context,
    private val tempDir: File,
    private val assetRetriever: AssetRetriever,
    private val lcpService: LcpService?,
) {

    data class Result(
        val tempFile: File,
        val format: Format?,
        val coverUrl: AbsoluteUrl?,
    )

    /**
     * Retrieves the publication from the given local [uri].
     */
    suspend fun retrieve(
        uri: Uri,
    ): Try<Result, ImportError> {
        val tempFile = uri.copyToTempFile(context, tempDir)
            .getOrElse {
                return Try.failure(ImportError.ContentResolver(it))
            }
        return retrieveFromStorage(tempFile, coverUrl = null)
            .onFailure { tryOrLog { tempFile.delete() } }
    }

    /**
     * Retrieves the publication stored at the given [tempFile].
     */
    suspend fun retrieve(
        tempFile: File,
        mediaType: MediaType? = null,
        coverUrl: AbsoluteUrl? = null,
    ): Try<Result, ImportError> {
        return retrieveFromStorage(tempFile, mediaType, coverUrl)
    }

    private suspend fun retrieveFromStorage(
        tempFile: File,
        mediaType: MediaType? = null,
        coverUrl: AbsoluteUrl? = null,
    ): Try<Result, ImportError> {
        val sourceAsset = assetRetriever.retrieve(tempFile, FormatHints(mediaType))
            .getOrElse {
                return Try.failure(ImportError.Publication(PublicationError(it)))
            }

        if (
            sourceAsset is ResourceAsset &&
            sourceAsset.format.conformsTo(Specification.LcpLicense)
        ) {
            return if (lcpService == null) {
                sourceAsset.close()
                Try.failure(ImportError.MissingLcpSupport)
            } else {
                val license = sourceAsset.resource.read()
                    .getOrElse {
                        sourceAsset.close()
                        return Try.failure(ImportError.Publication(PublicationError.Reading(it)))
                    }
                sourceAsset.close()
                val acquisition = lcpService.acquirePublication(license)
                    .getOrElse { return Try.failure(ImportError.LcpAcquisitionFailed(it)) }
                tryOrLog { tempFile.delete() }
                Try.success(Result(acquisition.localFile, acquisition.format, coverUrl))
            }
        }

        sourceAsset.close()
        return Try.success(
            Result(tempFile, sourceAsset.format, coverUrl)
        )
    }
}