package com.jskaleel.fte.di

import android.content.Context
import com.jskaleel.fte.data.domain.ApiRepository
import com.jskaleel.fte.data.local.AppDatabase
import com.jskaleel.fte.data.remote.ApiErrorHandle
import com.jskaleel.fte.data.remote.ApiService
import com.jskaleel.fte.data.remote.BooksRepositoryImp
import com.jskaleel.fte.data.remote.GetBooksUseCase
import com.jskaleel.fte.ui.feedback.FeedbackViewModel
import com.jskaleel.fte.ui.main.download.DownloadsRepositoryImpl
import com.jskaleel.fte.ui.main.download.DownloadsViewModel
import com.jskaleel.fte.ui.main.home.HomeViewModel
import com.jskaleel.fte.ui.splash.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val fteModule = module {

    viewModel { SplashViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { DownloadsViewModel(get()) }
    viewModel { FeedbackViewModel() }

    single { provideBooksUseCase(get(), provideApiErrorHandle()) }

    single { provideBooksRepository(get()) }
    single { provideDownloadsRepository(get()) }

    single { provideDatabase(androidContext()) }
}

fun provideDownloadsRepository(appDatabase: AppDatabase): DownloadsRepositoryImpl {
    return DownloadsRepositoryImpl(appDatabase)
}

fun provideDatabase(context: Context): AppDatabase {
    return AppDatabase.getAppDatabase(context)
}

fun provideBooksUseCase(
    postsRepository: ApiRepository,
    apiErrorHandle: ApiErrorHandle
): GetBooksUseCase {
    return GetBooksUseCase(postsRepository, apiErrorHandle)
}

fun provideApiErrorHandle(): ApiErrorHandle {
    return ApiErrorHandle()
}

fun provideBooksRepository(apiService: ApiService): ApiRepository {
    return BooksRepositoryImp(apiService)
}