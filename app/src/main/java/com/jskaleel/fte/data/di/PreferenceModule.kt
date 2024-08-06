package com.jskaleel.fte.data.di

import android.content.Context
import com.jskaleel.fte.data.source.datastore.AppPreferenceStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferenceModule {

    @Provides
    @Singleton
    fun getApplicationPreference(@ApplicationContext context: Context): AppPreferenceStore {
        return AppPreferenceStore(context = context)
    }
}