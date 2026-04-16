package com.lotusreichhart.audily.core.mediastore.di

import android.content.ContentResolver
import android.content.Context
import com.lotusreichhart.audily.core.common.coroutines.AudilyDispatchers
import com.lotusreichhart.audily.core.common.coroutines.Dispatcher
import com.lotusreichhart.audily.core.mediastore.datasource.MediaStoreDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaStoreModule {

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideMediaStoreDataSource(
        contentResolver: ContentResolver,
        @Dispatcher(AudilyDispatchers.IO) ioDispatcher: CoroutineDispatcher
    ): MediaStoreDataSource {
        return MediaStoreDataSource(contentResolver, ioDispatcher)
    }
}