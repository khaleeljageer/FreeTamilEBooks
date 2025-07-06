package com.jskaleel.fte.data.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setWelcomeShown()
    fun getWelcomeShown(): Flow<Boolean>
}
