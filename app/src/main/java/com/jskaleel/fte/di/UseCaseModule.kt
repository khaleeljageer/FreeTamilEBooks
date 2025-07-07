package com.jskaleel.fte.di

import com.jskaleel.fte.domain.usecase.BookShelfUseCase
import com.jskaleel.fte.domain.usecase.BookShelfUseCaseImpl
import com.jskaleel.fte.domain.usecase.DownloadsUseCase
import com.jskaleel.fte.domain.usecase.DownloadsUseCaseImpl
import com.jskaleel.fte.domain.usecase.SearchUseCase
import com.jskaleel.fte.domain.usecase.SearchUseCaseImpl
import com.jskaleel.fte.domain.usecase.WelcomeUseCase
import com.jskaleel.fte.domain.usecase.WelcomeUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun getWelcomeUseCase(
        useCase: WelcomeUseCaseImpl,
    ): WelcomeUseCase

    @Binds
    @Singleton
    abstract fun getBookShelfUseCase(
        useCase: BookShelfUseCaseImpl,
    ): BookShelfUseCase

    @Binds
    @Singleton
    abstract fun getDownloadsUseCase(
        useCase: DownloadsUseCaseImpl,
    ): DownloadsUseCase

    @Binds
    @Singleton
    abstract fun getSearchUseCase(
        useCase: SearchUseCaseImpl,
    ): SearchUseCase
}
