package com.jskaleel.fte.data.remote

import com.jskaleel.fte.data.entities.BooksResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("master/booksdb.json")
    suspend fun getAllBooks(): Response<BooksResponse>

    @FormUrlEncoded
    @POST("e/1FAIpQLSc_BbE7RfJdUCEgzwGSeiaiUe3ugBdITgJIZY71ED93puqQ3g/formResponse")
    suspend fun sendFeedback(
        @Header("accept") type: String,
        @Field("entry_359626196") name: String,
        @Field("entry_1250945452") email: String,
        @Field("entry_1221905149") comments: String
    ): Response<ResponseBody>
}