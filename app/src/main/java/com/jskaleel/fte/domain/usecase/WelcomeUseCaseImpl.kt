package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.data.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WelcomeUseCaseImpl @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : WelcomeUseCase {
    override suspend fun setWelcomeShown() {
        dataStoreRepository.setWelcomeShown()
    }

    override fun getWelcomeShown(): Flow<Boolean> {
        return dataStoreRepository.getWelcomeShown()
    }
}
