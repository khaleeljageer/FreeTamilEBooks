package com.jskaleel.fte.utils

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @FormUrlEncoded
    @POST("e/1FAIpQLSc_BbE7RfJdUCEgzwGSeiaiUe3ugBdITgJIZY71ED93puqQ3g/formResponse")
    fun postFeedBack(
        @Header("accept") type: String,
        @Field("entry_359626196") name: String,
        @Field("entry_1250945452") email: String,
        @Field("entry_1221905149") comments: String
    ): Observable<ResponseBody>
}