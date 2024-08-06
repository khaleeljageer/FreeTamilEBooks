package com.jskaleel.fte.data.di

import android.content.Context
import androidx.room.Room
import com.jskaleel.fte.data.source.local.AppDatabase
import com.jskaleel.fte.data.source.local.dao.LocalBooksDao
import com.jskaleel.fte.data.source.local.dao.SavedBooksDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppDatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            APP_DATABASE_NAME,
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun getSavedBooks(appDatabase: AppDatabase): SavedBooksDao {
        return appDatabase.getSavedBooks()
    }

    @Provides
    @Singleton
    fun getLocalBooks(appDatabase: AppDatabase): LocalBooksDao {
        return appDatabase.getLocalBooks()
    }

    companion object {
        private const val APP_DATABASE_NAME = "fte-db"
    }
}