package com.jskaleel.fte.data.source.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val IS_WELCOME_SHOWN = booleanPreferencesKey("is_welcome_shown")
    }

    suspend fun setWelcomeShown() {
        dataStore.edit { preferences ->
            preferences[IS_WELCOME_SHOWN] = true
        }
    }

    fun isWelcomeShown(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_WELCOME_SHOWN] == true
        }
    }
}
