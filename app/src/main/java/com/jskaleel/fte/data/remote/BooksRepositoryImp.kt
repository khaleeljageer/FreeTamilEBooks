package com.jskaleel.fte.data.remote

import com.jskaleel.fte.data.domain.ApiRepository
import com.jskaleel.fte.data.entities.BooksResponse
import com.jskaleel.fte.utils.PrintLog
import javax.net.ssl.HttpsURLConnection

class BooksRepositoryImp(private val apiService: ApiService) : ApiRepository {
    override suspend fun getAllBooks(): BooksResponse {
        val response = apiService.getAllBooks()
        return if (response.code() == HttpsURLConnection.HTTP_OK) {
            apiService.getAllBooks().body()!!
        } else {
            BooksResponse(emptyList())
        }
    }
}