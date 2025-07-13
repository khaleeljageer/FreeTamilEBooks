package com.jskaleel.fte.domain.model

import com.jskaleel.fte.core.model.ImageType

data class About(
    val title: String,
    val items: List<AboutItem>
)

data class AboutItem(
    val label: String,
    val description: String = "",
    val asset: String? = null,
    val url: String? = null,
    val email: String? = null,
    val icon: ImageType.Vector,
)