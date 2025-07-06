package com.jskaleel.fte.di

import com.jskaleel.fte.core.downloader.FileDownloader
import com.jskaleel.fte.core.downloader.FileDownloaderImpl
import com.jskaleel.fte.data.repository.BooksRepository
import com.jskaleel.fte.data.repository.BooksRepositoryImpl
import com.jskaleel.fte.data.repository.DataStoreRepository
import com.jskaleel.fte.data.repository.DataStoreRepositoryImpl
import com.jskaleel.fte.data.repository.DownloadRepository
import com.jskaleel.fte.data.repository.DownloadRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(
        repository: DataStoreRepositoryImpl
    ): DataStoreRepository

    @Binds
    @Singleton
    abstract fun bindBooksRepository(
        repository: BooksRepositoryImpl
    ): BooksRepository

    @Binds
    @Singleton
    abstract fun bindDownloadRepository(
        repository: DownloadRepositoryImpl
    ): DownloadRepository

    @Binds
    @Singleton
    abstract fun bindFileDownloader(
        repository: FileDownloaderImpl
    ): FileDownloader
}
