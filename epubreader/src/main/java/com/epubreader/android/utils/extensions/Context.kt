/*
 * Copyright 2022 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.epubreader.android.utils.extensions

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.epubreader.android.R
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.Properties

@ColorInt
fun Context.color(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

/**
 * Displays a confirmation [AlertDialog] and returns the user choice.
 */
suspend fun Context.confirmDialog(
    message: String,
    @StringRes positiveButton: Int = R.string.ok,
    @StringRes negativeButton: Int = R.string.cancel
): Boolean =
    suspendCancellableCoroutine { cont ->
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(positiveButton)) { dialog, _ ->
                dialog.dismiss()
                cont.resume(true)
            }
            .setNegativeButton(getString(negativeButton)) { dialog, _ ->
                dialog.cancel()
            }
            .setOnCancelListener {
                cont.resume(false)
            }
            .show()
    }

fun Context.computeStorageDir(): File {
    val properties = Properties()
    val inputStream = assets.open("configs/config.properties")
    properties.load(inputStream)
    val useExternalFileDir =
        properties.getProperty("useExternalFileDir", "false")!!.toBoolean()

    return File(
        if (useExternalFileDir) getExternalFilesDir(null)?.path + "/"
        else filesDir?.path + "/"
    )
}
