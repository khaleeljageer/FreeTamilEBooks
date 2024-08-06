package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.model.ThemeConfig
import com.jskaleel.fte.data.model.UserThemeData
import com.jskaleel.fte.data.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsUseCaseImpl @Inject constructor(
    private val userDataRepository: UserDataRepository
) : SettingsUseCase {
    override suspend fun setThemeConfig(themeConfig: ThemeConfig) {
        userDataRepository.setThemeConfig(themeConfig = themeConfig)
    }

    override fun getUserThemeData(): Flow<UserThemeData> {
        return userDataRepository.getUserThemeData()
    }
}