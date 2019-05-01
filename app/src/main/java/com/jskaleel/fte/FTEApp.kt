package com.jskaleel.fte

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.utils.CommonAppData
import com.jskaleel.fte.utils.NetworkSchedulerService
import io.fabric.sdk.android.Fabric
import org.geometerplus.android.fbreader.FBReaderApplication
import org.geometerplus.android.fbreader.util.FBReaderConfig


class FTEApp : FBReaderApplication() {
    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())

        MultiDex.install(this@FTEApp)
        scheduleJob()
        AppDatabase.getAppDatabase(this@FTEApp)

        FBReaderConfig.init(this)
    }

    private fun scheduleJob() {
        val myJob = JobInfo.Builder(0, ComponentName(this, NetworkSchedulerService::class.java))
            .setRequiresCharging(true)
            .setMinimumLatency(1000)
            .setOverrideDeadline(2000)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .build()

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(myJob)
    }
}
