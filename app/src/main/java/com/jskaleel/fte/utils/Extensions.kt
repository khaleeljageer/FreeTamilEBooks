package com.jskaleel.fte.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


fun InputStream.copyStreamToFile(outputFile: File) {
    this.use { input ->
        val outputStream = FileOutputStream(outputFile)
        outputStream.use { output ->
            val buffer = ByteArray(4 * 1024)
            while (true) {
                val byteCount = input.read(buffer)
                if (byteCount < 0) break
                output.write(buffer, 0, byteCount)
            }
            output.flush()
        }
    }
}