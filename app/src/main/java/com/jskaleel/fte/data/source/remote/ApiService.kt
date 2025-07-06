package com.jskaleel.fte.data.source.remote

import com.jskaleel.fte.data.model.BooksDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET(value = "master/booksdb.json")
    suspend fun fetchBooks(): Response<List<BooksDTO>>

    @FormUrlEncoded
    @POST(value = "e/1FAIpQLSc_BbE7RfJdUCEgzwGSeiaiUe3ugBdITgJIZY71ED93puqQ3g/formResponse")
    suspend fun sendFeedback(
        @Header(value = "accept") type: String,
        @Field(value = "entry_359626196") name: String,
        @Field(value = "entry_1250945452") email: String,
        @Field(value = "entry_1221905149") comments: String
    ): Response<ResponseBody>
}
