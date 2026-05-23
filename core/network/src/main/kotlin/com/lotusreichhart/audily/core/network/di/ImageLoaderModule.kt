package com.lotusreichhart.audily.core.network.di

import android.content.Context
import coil3.ImageLoader
import com.lotusreichhart.audily.core.network.util.AudioCoverFetcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageLoaderModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(AudioCoverFetcher.Factory(context))
            }
            .build()
    }
}