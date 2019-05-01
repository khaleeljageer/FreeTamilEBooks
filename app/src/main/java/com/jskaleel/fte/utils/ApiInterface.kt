package com.jskaleel.fte.utils

import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.model.BooksResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiInterface {
    @FormUrlEncoded
    @POST("e/1FAIpQLSc_BbE7RfJdUCEgzwGSeiaiUe3ugBdITgJIZY71ED93puqQ3g/formResponse")
    fun postFeedBack(
        @Header("accept") type: String,
        @Field("entry_359626196") name: String,
        @Field("entry_1250945452") email: String,
        @Field("entry_1221905149") comments: String
    ): Observable<ResponseBody>

    @GET("master/booksdb.json")
    fun getNewBooks(): Observable<BooksResponse>
}