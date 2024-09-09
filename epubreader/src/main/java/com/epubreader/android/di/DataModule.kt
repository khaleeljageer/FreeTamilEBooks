package com.epubreader.android.di

import com.epubreader.android.data.BookRepository
import com.epubreader.android.data.BookRepositoryImpl
import com.epubreader.android.data.ReaderRepository
import com.epubreader.android.data.ReaderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun getBookRepository(
        bookRepositoryImpl: BookRepositoryImpl,
    ): BookRepository

    @Binds
    @Singleton
    abstract fun getReaderRepository(
        readerRepositoryImpl: ReaderRepositoryImpl,
    ): ReaderRepository
}