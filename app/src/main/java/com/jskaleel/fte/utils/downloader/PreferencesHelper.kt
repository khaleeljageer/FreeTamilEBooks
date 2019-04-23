package com.jskaleel.fte.utils.downloader

import android.content.Context
import android.content.SharedPreferences
import com.jskaleel.fte.BuildConfig

object PreferencesHelper {

    const val PREFERENCES_NAME = "${BuildConfig.APPLICATION_ID}.preferences"
    const val DOWNLOADS_KEY = "$PREFERENCES_NAME.downloads"

    private fun get(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun saveDownload(context: Context, id: Long) {
        val preferences = get(context)

        val downloads = preferences.getStringSet(DOWNLOADS_KEY, HashSet<String>())
        downloads?.add(id.toString())

        val editor = preferences.edit()
        editor.putStringSet(DOWNLOADS_KEY, downloads)
        editor.apply()
    }

    fun removeDownload(context: Context, id: Long) {
        val preferences = get(context)

        val downloads = preferences.getStringSet(DOWNLOADS_KEY, emptySet())
        downloads?.remove(id.toString())

        val editor = preferences.edit()
        editor.putStringSet(DOWNLOADS_KEY, downloads)
        editor.apply()
    }

    fun getDownloads(context: Context): Set<Long> {

        val longs = HashSet<Long>()

        val preferences = get(context)
        val downloads = preferences.getStringSet(DOWNLOADS_KEY, emptySet())
        downloads?.forEach { it ->
            longs.add(it.toLong())
        }

        return longs
    }

    fun clearDownloads(context: Context) {
        val editor = get(context).edit()
        editor.remove(DOWNLOADS_KEY)
        editor.apply()
    }
}