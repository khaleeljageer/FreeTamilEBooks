package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.data.model.UserThemeData
import kotlinx.coroutines.flow.Flow

interface MainActivityUseCase {
    fun subscribeNetworkMonitor(): Flow<Boolean>
    fun getUserConfig(): Flow<UserThemeData>
}