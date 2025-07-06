package com.jskaleel.fte

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FTEBooksApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(context, DEFAULT_MEM_CACHE_SIZE)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.cacheDir.resolve(IMAGE_CACHE_DIR))
                        .maxSizePercent(DEFAULT_DISK_CACHE_SIZE)
                        .build()
                }
                .build()
        }
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(READ_TIMEOUT)
            .setConnectTimeout(CONNECT_TIMEOUT)
            .build()
        PRDownloader.initialize(applicationContext, config)
    }

    companion object {
        private const val READ_TIMEOUT = 60000
        private const val CONNECT_TIMEOUT = 60000
        private const val DEFAULT_MEM_CACHE_SIZE = 0.25 // 25% of available memory
        private const val DEFAULT_DISK_CACHE_SIZE = 0.02 // 2% of available disk space
        private const val IMAGE_CACHE_DIR = "image_cache"
    }
}