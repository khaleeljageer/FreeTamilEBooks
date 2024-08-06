package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.model.ThemeConfig
import com.jskaleel.fte.data.model.UserThemeData
import kotlinx.coroutines.flow.Flow

interface SettingsUseCase {
    suspend fun setThemeConfig(themeConfig: ThemeConfig)

    fun getUserThemeData(): Flow<UserThemeData>
}