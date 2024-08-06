package com.jskaleel.fte.data.model

import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.toTypeString
import com.jskaleel.fte.data.source.local.entities.BookEntity

data class BookDto(
    val bookid: String,
    val title: String,
    val author: String,
    val image: String,
    val epub: String,
    val category: String,
)

data class BooksResponse(
    val books: List<BookDto>
)