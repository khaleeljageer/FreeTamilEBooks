package com.jskaleel.fte.di

import android.content.Context
import com.jskaleel.fte.data.remote.ApiService
import com.jskaleel.fte.utils.Constants
import com.jskaleel.fte.utils.DeviceUtils
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { provideCache(get()) }
    single { provideHttpClient(get(), get()) }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
}

private fun provideCache(context: Context): Cache {
    val cacheSize: Long = 10 * 1024 * 1024 // 10mb

    return Cache(context.cacheDir, cacheSize)
}

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

private fun provideApiService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
}

private fun provideHttpClient(context: Context, cache: Cache): OkHttpClient {

    return OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .cache(cache)
        .addNetworkInterceptor { chain ->
            var request = chain.request()

            request = if (DeviceUtils.isNetworkAvailable(context))
                request
                    .newBuilder()
                    .header(
                        "Cache-Control",
                        "public, max-age=5"
                    )
                    .build()
            else
                request
                    .newBuilder()
                    .header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=${60 * 60 * 24 * 7}"
                    )
                    .build()

            chain.proceed(request)
        }
        .build()
}
