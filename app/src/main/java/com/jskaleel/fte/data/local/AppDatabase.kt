package com.jskaleel.fte.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jskaleel.fte.data.dao.LocalBooksDao
import com.jskaleel.fte.data.dao.SavedBooksDao
import com.jskaleel.fte.data.entities.LocalBooks
import com.jskaleel.fte.data.entities.SavedBooks

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