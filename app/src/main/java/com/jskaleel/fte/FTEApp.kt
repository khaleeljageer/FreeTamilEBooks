package com.jskaleel.fte

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.jskaleel.fte.utils.AppPreference
import com.jskaleel.fte.utils.AppPreference.get
import com.jskaleel.fte.utils.Constants
import com.jskaleel.fte.utils.NetworkSchedulerService
import java.util.*

class FTEApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .setUserAgent("FTEAndroid")
            .build()
        PRDownloader.initialize(applicationContext, config)

        scheduleJob()

        initNotificationChannel()

        val isSubscribed =
            AppPreference.customPrefs(applicationContext)[Constants.SharedPreference.NEW_BOOKS_UPDATE, true]

        if (isSubscribed) {
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.CHANNEL_NAME)
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.CHANNEL_NAME)
        }
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


    private fun initNotificationChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationChannels = ArrayList<NotificationChannel>()

            val pushNotification = NotificationChannel(
                Constants.CHANNEL_NAME,
                "New Releases & Recommendations",
                NotificationManager.IMPORTANCE_HIGH
            )
            pushNotification.setShowBadge(true)

            notificationChannels.add(pushNotification)

            notificationManager.createNotificationChannels(notificationChannels)
        }
    }
/*ndk-build NDK_PROJECT_PATH=/Users/khaleeljageer/Documents/KotlinWorkspace/FreeTamilEbooks/reader/build APP_BUILD_SCRIPT=/Users/khaleeljageer/Documents/KotlinWorkspace/FreeTamilEbooks/reader/src/main/jni/Android.mk NDK_APPLICATION_MK=/Users/khaleeljageer/Documents/KotlinWorkspace/FreeTamilEbooks/reader/src/main/jni/Application.mk
*/
}
