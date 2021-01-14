package com.jskaleel.fte.data.remote

import com.jskaleel.fte.data.domain.ApiRepository
import com.jskaleel.fte.data.entities.BooksResponse
import com.jskaleel.fte.data.remote.base.UseCase

class GetBooksUseCase constructor(
    private val apiRepository: ApiRepository,
    apiErrorHandle: ApiErrorHandle?
) : UseCase<BooksResponse>(apiErrorHandle) {

    override suspend fun run(): BooksResponse {
        return apiRepository.getAllBooks()
    }
}