package com.jskaleel.fte.di

import android.content.Context
import androidx.room.Room
import com.jskaleel.fte.data.source.local.BooksDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
    @Provides
    @Singleton
    fun provideAppDb(@ApplicationContext context: Context): BooksDatabase {
        return Room.databaseBuilder(
            context,
            BooksDatabase::class.java,
            APP_DATABASE_NAME,
        ).fallbackToDestructiveMigration(true).build()
    }

    companion object {
        private const val APP_DATABASE_NAME = "fte_books_app.db"
    }
}
