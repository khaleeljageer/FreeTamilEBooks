package com.jskaleel.fte.data.remote

import com.jskaleel.fte.data.entities.BooksResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("master/booksdb.json")
    suspend fun getAllBooks(): Response<BooksResponse>
}