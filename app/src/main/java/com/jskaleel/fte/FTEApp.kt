package com.jskaleel.fte

import android.app.Application
import com.jskaleel.fte.database.AppDatabase

class FTEApp : Application() {

    override fun onCreate() {
        super.onCreate()

        AppDatabase.getAppDatabase(this@FTEApp)
    }
}
