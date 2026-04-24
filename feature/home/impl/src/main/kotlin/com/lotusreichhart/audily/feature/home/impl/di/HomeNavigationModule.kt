package com.lotusreichhart.audily.feature.home.impl.di

import com.lotusreichhart.audily.feature.home.api.HomeEntry
import com.lotusreichhart.audily.feature.home.impl.HomeEntryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class HomeImplModule {
    @Binds
    @Singleton
    abstract fun bindHomeEntry(impl: HomeEntryImpl): HomeEntry
}
