package com.jskaleel.fte.domain.model

import com.jskaleel.fte.core.model.ImageType

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val image: ImageType,
    val url: String,
    val downloadProgress: Int = 0,
    val downloading: Boolean = false,
    val downloaded: Boolean = false
)