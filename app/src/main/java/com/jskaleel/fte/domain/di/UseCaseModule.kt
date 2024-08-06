package com.jskaleel.fte.domain.di

import com.jskaleel.fte.domain.usecase.GetBooksUseCase
import com.jskaleel.fte.domain.usecase.GetBooksUseCaseImpl
import com.jskaleel.fte.domain.usecase.MainActivityUseCase
import com.jskaleel.fte.domain.usecase.MainActivityUseCaseImpl
import com.jskaleel.fte.domain.usecase.RefreshBooksUseCase
import com.jskaleel.fte.domain.usecase.RefreshBooksUseCaseImpl
import com.jskaleel.fte.domain.usecase.SettingsUseCase
import com.jskaleel.fte.domain.usecase.SettingsUseCaseImpl
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
    abstract fun getMainActivityUseCase(
        mainActivityUseCaseImpl: MainActivityUseCaseImpl,
    ): MainActivityUseCase

    @Binds
    @Singleton
    abstract fun getBooksUseCase(
        getBooksUseCaseImpl: GetBooksUseCaseImpl,
    ): GetBooksUseCase

    @Binds
    @Singleton
    abstract fun refreshBooksUseCase(
        refreshBooksUseCaseImpl: RefreshBooksUseCaseImpl,
    ): RefreshBooksUseCase

    @Binds
    @Singleton
    abstract fun getSettingsUseCase(
        settingsUseCaseImpl: SettingsUseCaseImpl,
    ): SettingsUseCase
}