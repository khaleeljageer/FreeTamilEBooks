package com.jskaleel.fte.utils

import android.content.Context
import android.content.SharedPreferences

object AppPreference {
    fun customPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(Constants.SharedPreference.FILE_NAME, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    /**
     * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
     */
    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is String? -> edit { it.putString(key, value) }
            is Int -> edit { it.putInt(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
        }
    }

    /**
     * finds value on given key.
     * [T] is the type of value
     * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
     */
    inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T): T {
        return when (T::class) {
            String::class -> getString(key, defaultValue as String) as T
            Int::class -> getInt(key, defaultValue as Int) as T
            Boolean::class -> getBoolean(key, defaultValue as Boolean) as T
            Float::class -> getFloat(key, defaultValue as Float) as T
            Long::class -> getLong(key, defaultValue as Long) as T
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }
}
