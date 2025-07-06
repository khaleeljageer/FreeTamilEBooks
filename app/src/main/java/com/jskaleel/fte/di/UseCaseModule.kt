package com.jskaleel.fte.di

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

//    @Binds
//    @Singleton
//    abstract fun getBooksUseCase(
//        useCase: BooksUseCaseImpl,
//    ): BooksUseCase
}
