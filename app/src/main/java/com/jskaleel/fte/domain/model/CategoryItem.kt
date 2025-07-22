package com.jskaleel.fte.domain.model

import com.jskaleel.fte.core.model.ImageType

data class CategoryItem(
    val name: String,
    val count: Int
)

data class RecentReadItem(
    val id: String,
    val title: String,
    val image: ImageType,
    val lastRead: String
)
