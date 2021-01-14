package com.jskaleel.fte.data.remote

import com.jskaleel.fte.data.domain.ApiRepository
import com.jskaleel.fte.data.entities.BooksResponse

class BooksRepositoryImp(private val apiService: ApiService) : ApiRepository {
    override suspend fun getAllBooks(): BooksResponse {
        return apiService.getAllBooks()
    }
}