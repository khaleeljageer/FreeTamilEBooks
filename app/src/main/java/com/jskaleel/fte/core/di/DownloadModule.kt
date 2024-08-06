package com.jskaleel.fte.core.di

import com.jskaleel.fte.core.downloader.FileDownloader
import com.jskaleel.fte.core.downloader.FileDownloaderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DownloadModule {
    @Binds
    @Singleton
    abstract fun bindFileDownloader(
        fileDownloaderImpl: FileDownloaderImpl
    ): FileDownloader
}