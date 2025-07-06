package com.jskaleel.fte.data.repository

import com.jskaleel.fte.data.source.datastore.UserPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : DataStoreRepository {
    override suspend fun setWelcomeShown() {
        userPreferencesDataStore.setWelcomeShown()
    }

    override fun getWelcomeShown(): Flow<Boolean> {
        return userPreferencesDataStore.isWelcomeShown()
    }
}
