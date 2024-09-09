package com.epubreader.android.di

import android.content.Context
import androidx.room.Room
import com.epubreader.android.db.BookDatabase
import com.epubreader.android.db.BooksDao
import com.epubreader.android.db.CatalogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideBookDatabase(@ApplicationContext context: Context): BookDatabase {
        return Room.databaseBuilder(
            context,
            BookDatabase::class.java,
            APP_DATABASE_NAME,
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun getBooksDao(appDatabase: BookDatabase): BooksDao {
        return appDatabase.booksDao()
    }

    @Provides
    @Singleton
    fun getCatalogDao(appDatabase: BookDatabase): CatalogDao {
        return appDatabase.catalogDao()
    }

    companion object {
        private const val APP_DATABASE_NAME = "reader_db"
    }
}