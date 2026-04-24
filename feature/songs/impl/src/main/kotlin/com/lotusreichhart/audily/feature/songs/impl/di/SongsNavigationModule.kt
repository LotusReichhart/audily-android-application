package com.lotusreichhart.audily.feature.songs.impl.di

import com.lotusreichhart.audily.feature.songs.api.SongsEntry
import com.lotusreichhart.audily.feature.songs.impl.SongsEntryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SongsImplModule {
    @Binds
    @Singleton
    abstract fun bindSongsEntry(impl: SongsEntryImpl): SongsEntry
}
