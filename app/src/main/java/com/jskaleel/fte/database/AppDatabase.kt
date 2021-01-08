package com.jskaleel.fte.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jskaleel.fte.database.dao.LocalBooksDao
import com.jskaleel.fte.database.entities.LocalBooks

@Database(entities = [LocalBooks::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun localBooksDao(): LocalBooksDao

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