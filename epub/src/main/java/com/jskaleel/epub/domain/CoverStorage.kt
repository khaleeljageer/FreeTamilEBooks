package com.jskaleel.epub.domain

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.shared.util.Try
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CoverStorage(
    val appStorageDir: File
) {
    suspend fun storeCover(
        publication: Publication
    ): Try<File, Exception> {
        val coverBitmap: Bitmap? = publication.cover()
        return try {
            Try.success(storeCover(coverBitmap))
        } catch (e: Exception) {
            Try.failure(e)
        }
    }

    private suspend fun storeCover(cover: Bitmap?): File =
        withContext(Dispatchers.IO) {
            val coverImageFile = File(coverDir(), "${UUID.randomUUID()}.png")
            val resized = cover?.let { Bitmap.createScaledBitmap(it, 120, 200, true) }
            val fos = FileOutputStream(coverImageFile)
            resized?.compress(Bitmap.CompressFormat.PNG, 80, fos)
            fos.flush()
            fos.close()
            coverImageFile
        }

    private fun coverDir(): File =
        File(appStorageDir, "covers/")
            .apply { if (!exists()) mkdirs() }
}
