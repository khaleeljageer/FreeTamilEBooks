package com.jskaleel.fte.module

import okhttp3.OkHttpClient
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { createHttpClient() }
}

fun createHttpClient(): OkHttpClient {

    return OkHttpClient().newBuilder()
        .readTimeout(60000, TimeUnit.MILLISECONDS)
        .connectTimeout(60000, TimeUnit.MILLISECONDS).build()
}
