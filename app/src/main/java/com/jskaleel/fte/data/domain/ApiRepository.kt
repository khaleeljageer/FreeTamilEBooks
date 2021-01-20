package com.jskaleel.fte.data.domain

import com.jskaleel.fte.data.entities.BooksResponse

interface ApiRepository {
    suspend fun getAllBooks(): BooksResponse
}