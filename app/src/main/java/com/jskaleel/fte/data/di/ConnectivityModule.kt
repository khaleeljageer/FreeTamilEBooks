package com.jskaleel.fte.data.di

import com.jskaleel.fte.core.utils.network.NetworkConnectivityMonitor
import com.jskaleel.fte.core.utils.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ConnectivityModule {
    @Binds
    fun bindNetworkMonitor(
        connectivityMonitor: NetworkConnectivityMonitor
    ): NetworkMonitor
}