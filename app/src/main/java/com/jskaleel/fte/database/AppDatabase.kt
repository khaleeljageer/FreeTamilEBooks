package com.jskaleel.fte.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jskaleel.fte.database.dao.LocalBooksDao
import com.jskaleel.fte.database.dao.SavedBooksDao
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.database.entities.SavedBooks

@Database(entities = [LocalBooks::class, SavedBooks::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun localBooksDao(): LocalBooksDao
    abstract fun savedBooksDao(): SavedBooksDao

    companion object {
        private var instance: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "local_books.db"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as AppDatabase
        }
    }
}