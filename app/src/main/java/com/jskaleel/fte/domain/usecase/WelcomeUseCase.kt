package com.jskaleel.fte.domain.usecase

import kotlinx.coroutines.flow.Flow

interface WelcomeUseCase {
    suspend fun setWelcomeShown()
    fun getWelcomeShown(): Flow<Boolean>
}
