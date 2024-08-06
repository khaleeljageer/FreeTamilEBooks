package com.jskaleel.fte.data.repository

import com.jskaleel.fte.core.model.ThemeConfig
import com.jskaleel.fte.data.model.UserThemeData
import com.jskaleel.fte.data.source.datastore.AppPreferenceStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val applicationPreference: AppPreferenceStore,
) : UserDataRepository {

    override suspend fun setThemeConfig(themeConfig: ThemeConfig) {
        applicationPreference.setThemeConfig(themeConfig = themeConfig)
    }

    override fun getUserThemeData(): Flow<UserThemeData> {
        return applicationPreference.getUserThemeData()
    }
}