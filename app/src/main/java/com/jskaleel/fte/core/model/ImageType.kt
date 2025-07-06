package com.jskaleel.fte.core.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import coil3.compose.rememberAsyncImagePainter

sealed class ImageType {
    data class ResourceImage(@DrawableRes val id: Int) : ImageType()
    data class NetworkImage(val url: String) : ImageType()

    companion object {
        val EMPTY = NetworkImage("")
    }
}

@Composable
fun ImageType.getImagePainter(): Painter {
    return when (this) {
        is ImageType.NetworkImage -> rememberAsyncImagePainter(model = url)
        is ImageType.ResourceImage -> painterResource(id = id)
    }
}
