package com.jskaleel.fte.data.di

import com.jskaleel.fte.data.repository.BooksRepository
import com.jskaleel.fte.data.repository.BooksRepositoryImpl
import com.jskaleel.fte.data.repository.UserDataRepository
import com.jskaleel.fte.data.repository.UserDataRepositoryImpl
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
    abstract fun getUserDataRepository(
        userDataRepositoryImpl: UserDataRepositoryImpl,
    ): UserDataRepository

    @Binds
    @Singleton
    abstract fun getBooksRepository(
        booksRepositoryImpl: BooksRepositoryImpl,
    ): BooksRepository
}