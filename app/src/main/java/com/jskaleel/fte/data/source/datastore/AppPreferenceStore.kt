package com.jskaleel.fte.data.source.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jskaleel.fte.core.extensions.defaultThemeConfig
import com.jskaleel.fte.core.model.ThemeConfig
import com.jskaleel.fte.data.model.UserThemeData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPreferenceStore @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = PREF_NAME)

    suspend fun clearUserPreference() {
        context.dataStore.edit {
            it.clear()
        }
    }

    fun getUserThemeData(): Flow<UserThemeData> {
        return context.dataStore.data.map { preference ->
            val themeConfig: EThemeConfig? = preference[KEY_THEME_CONFIG].defaultThemeConfig()
            UserThemeData(
                themeConfig = when (themeConfig) {
                    null,
                    EThemeConfig.DARK_THEME_CONFIG_UNSPECIFIED,
                    EThemeConfig.UNRECOGNIZED,
                    EThemeConfig.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                    -> ThemeConfig.FOLLOW_SYSTEM

                    EThemeConfig.DARK_THEME_CONFIG_LIGHT -> ThemeConfig.LIGHT
                    EThemeConfig.DARK_THEME_CONFIG_DARK -> ThemeConfig.DARK
                },
            )
        }
    }

    suspend fun setThemeConfig(themeConfig: ThemeConfig) {
        context.dataStore.edit { preference ->
            val config = when (themeConfig) {
                ThemeConfig.FOLLOW_SYSTEM -> EThemeConfig.DARK_THEME_CONFIG_FOLLOW_SYSTEM

                ThemeConfig.LIGHT -> EThemeConfig.DARK_THEME_CONFIG_LIGHT
                ThemeConfig.DARK -> EThemeConfig.DARK_THEME_CONFIG_DARK
            }.ordinal

            preference[KEY_THEME_CONFIG] = config
        }
    }

    suspend fun setLastSync() {
        context.dataStore.edit { preference ->
            preference[KEY_LAST_SYNC] = System.currentTimeMillis()
        }
    }

    suspend fun getLastSync(): Long? {
        val preference = context.dataStore.data.firstOrNull()
        return preference?.get(KEY_LAST_SYNC)
    }

    companion object {
        private const val PREF_NAME = "fte_preferences"
        private val KEY_THEME_CONFIG = intPreferencesKey("key.theme_config")
        private val KEY_LAST_SYNC = longPreferencesKey("key.last_sync")
    }
}

enum class EThemeConfig(value: Int) {
    DARK_THEME_CONFIG_UNSPECIFIED(0), DARK_THEME_CONFIG_FOLLOW_SYSTEM(1), DARK_THEME_CONFIG_LIGHT(2), DARK_THEME_CONFIG_DARK(
        3
    ),
    UNRECOGNIZED(-1);

    companion object {
        fun safeValueOf(i: Int): EThemeConfig? {
            return values().find { it.ordinal == i }
        }
    }
}