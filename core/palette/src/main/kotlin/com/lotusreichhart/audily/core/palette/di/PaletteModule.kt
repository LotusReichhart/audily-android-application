package com.lotusreichhart.audily.core.palette.di

import com.lotusreichhart.audily.core.domain.repository.playback.PaletteRepository
import com.lotusreichhart.audily.core.palette.PaletteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PaletteModule {

    @Binds
    @Singleton
    abstract fun bindPaletteRepository(
        paletteRepository: PaletteRepositoryImpl
    ): PaletteRepository
}