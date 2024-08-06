package com.jskaleel.fte.data.model

data class BookDto(
    val bookid: String,
    val title: String,
    val author: String,
    val image: String,
    val epub: String,
    val category: String,
    val downloaded: Boolean = false,
)

data class BooksResponse(
    val books: List<BookDto>
)