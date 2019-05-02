package com.jskaleel.fte.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Environment
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import java.io.File
import java.util.regex.Pattern


object DeviceUtils {
    private fun getAppDirectory(context: Context): File {
        return File(context.getExternalFilesDir(null)!!.toString() + "/books")

    }

    fun convertDpToPixel(context: Context, dp: Float): Float {
        val metrics = context.resources.displayMetrics
        return dp * (metrics.densityDpi / 160f)
    }

    fun getRootDirPath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file = ContextCompat.getExternalFilesDirs(
                context.applicationContext,
                null
            )[0]
            file.absolutePath
        } else {
            context.applicationContext.filesDir.absolutePath
        }
    }

    fun hideSoftKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }
}