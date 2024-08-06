package com.jskaleel.fte.core.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.jskaleel.fte.core.extensions.emptyString

sealed class ImageType {
    data class ResourceImage(@DrawableRes val id: Int) : ImageType()
    data class NetworkImage(val url: String) : ImageType()

    companion object {
        val EMPTY = NetworkImage(emptyString())
    }
}

@Composable
fun ImageType.getImagePainter(): Painter {
    return when (this) {
        is ImageType.NetworkImage -> rememberAsyncImagePainter(model = url)
        is ImageType.ResourceImage -> painterResource(id = id)
    }
}

fun ImageType.toTypeString(): String {
    return when (this) {
        is ImageType.ResourceImage -> "Drawable:${this.id}"
        is ImageType.NetworkImage -> "Url:${this.url}"
    }
}

fun String.toImage(): ImageType {
    return if (this.startsWith("Drawable:")) {
        ImageType.ResourceImage(this.replace("Drawable:", "").toInt())
    } else {
        ImageType.NetworkImage(this.replace("Url:", ""))
    }
}
