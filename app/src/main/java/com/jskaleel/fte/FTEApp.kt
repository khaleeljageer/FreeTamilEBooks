package com.jskaleel.fte

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.utils.NetworkSchedulerService


class FTEApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        scheduleJob()
        AppDatabase.getAppDatabase(this@FTEApp)
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
