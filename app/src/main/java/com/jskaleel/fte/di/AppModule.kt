package com.jskaleel.fte.di

import android.content.Context
import com.jskaleel.epub.EpubApplication
import com.jskaleel.epub.reader.EBookReaderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideEpubApplication(@ApplicationContext applicationContext: Context): EpubApplication {
        return applicationContext as EpubApplication
    }

    @Provides
    @Singleton
    fun provideReaderRepository(epubApplication: EpubApplication): EBookReaderRepository {
        return epubApplication.eBookReaderRepository
    }
}
