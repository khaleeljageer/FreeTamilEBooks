package com.jskaleel.fte.core.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import coil3.compose.rememberAsyncImagePainter
import com.google.gson.Gson

sealed class ImageType {
    data class ResourceImage(@DrawableRes val id: Int) : ImageType()
    data class Vector(val vector: ImageVector) : ImageType()
    data class NetworkImage(val url: String) : ImageType()

    companion object {
        val EMPTY = NetworkImage("")
    }
}

@Composable
fun ImageType.getImagePainter(): Painter {
    return when (this) {
        is ImageType.NetworkImage -> rememberAsyncImagePainter(url)

        is ImageType.ResourceImage -> painterResource(id = id)
        is ImageType.Vector -> rememberVectorPainter(image = vector)
    }
}

fun ImageType.toTypeString(): String {
    return when (this) {
        is ImageType.ResourceImage -> "Drawable:${this.id}"
        is ImageType.NetworkImage -> "Url:${this.url}"
        is ImageType.Vector -> "Vector:${Gson().toJson(this)}"
    }
}

fun String.toImage(): ImageType {
    return if (this.startsWith("Drawable:")) {
        ImageType.ResourceImage(this.replace("Drawable:", "").toInt())
    } else {
        ImageType.NetworkImage(this.replace("Url:", ""))
    }
}