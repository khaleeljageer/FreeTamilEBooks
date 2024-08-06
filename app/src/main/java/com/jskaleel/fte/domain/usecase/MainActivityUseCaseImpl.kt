package com.jskaleel.fte.domain.usecase

import com.jskaleel.fte.core.utils.network.NetworkMonitor
import com.jskaleel.fte.data.model.UserThemeData
import com.jskaleel.fte.data.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainActivityUseCaseImpl @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val userDataRepository: UserDataRepository
) : MainActivityUseCase {
    override fun subscribeNetworkMonitor(): Flow<Boolean> {
        return networkMonitor.isOnline
    }

    override fun getUserConfig(): Flow<UserThemeData> {
        return userDataRepository.getUserThemeData()
    }
}