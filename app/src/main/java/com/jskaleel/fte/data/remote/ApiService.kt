package com.jskaleel.fte.data.remote

import com.jskaleel.fte.data.entities.BooksResponse
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {
    @GET("master/booksdb.json")
    fun getNewBooks(): Observable<BooksResponse>
}