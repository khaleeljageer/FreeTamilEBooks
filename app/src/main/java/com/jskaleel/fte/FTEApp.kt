package com.jskaleel.fte

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.multidex.MultiDexApplication
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import coil.util.CoilUtils
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.jskaleel.fte.di.fteModule
import com.jskaleel.fte.di.networkModule
import com.jskaleel.fte.utils.AppPreference
import com.jskaleel.fte.utils.AppPreference.get
import com.jskaleel.fte.utils.Constants
import com.jskaleel.fte.utils.FileUtils
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.io.File
import java.util.*


class FTEApp : MultiDexApplication(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        startKoin {
            androidLogger()
            modules(listOf(networkModule, fteModule))
            androidContext(this@FTEApp)
        }

        createFolder()

        initNotificationChannel()

        val isSubscribed =
            AppPreference.customPrefs(applicationContext)[Constants.SharedPreference.NEW_BOOKS_UPDATE, true]

        if (isSubscribed) {
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.CHANNEL_NAME)
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.CHANNEL_NAME)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(applicationContext))
                    .build()
            }
            .build()
    }

    private fun createFolder() {
        val dirPath = File(FileUtils.getRootDirPath(applicationContext))
        if (!dirPath.exists()) dirPath.mkdirs()
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
