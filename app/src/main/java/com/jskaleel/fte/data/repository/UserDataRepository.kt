package com.jskaleel.fte.data.repository

import com.jskaleel.fte.core.model.ThemeConfig
import com.jskaleel.fte.data.model.UserThemeData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    fun getUserThemeData(): Flow<UserThemeData>

    /**
     * Sets the desired dark theme config.
     */
    suspend fun setThemeConfig(themeConfig: ThemeConfig)
}
