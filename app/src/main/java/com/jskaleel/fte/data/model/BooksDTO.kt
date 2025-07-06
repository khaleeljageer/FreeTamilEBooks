package com.jskaleel.fte.data.model

import com.google.gson.annotations.SerializedName

data class BooksResponse(
    @SerializedName("books")
    val books: List<BooksDTO>,
)

data class BooksDTO(
    @SerializedName("bookid")
    val bookId: String,
    val title: String,
    val author: String,
    val image: String,
    val epub: String,
    val category: String,
)
