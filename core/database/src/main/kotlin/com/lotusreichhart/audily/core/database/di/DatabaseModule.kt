package com.lotusreichhart.audily.core.database.di

import android.content.Context
import androidx.room.Room
import com.lotusreichhart.audily.core.database.AudilyDatabase
import com.lotusreichhart.audily.core.database.dao.FavoritesDao
import com.lotusreichhart.audily.core.database.dao.PlaylistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAudilyDatabase(
        @ApplicationContext context: Context,
    ): AudilyDatabase = Room.databaseBuilder(
        context,
        AudilyDatabase::class.java,
        "audily-database",
    ).build()

    @Provides
    fun provideFavoritesDao(
        database: AudilyDatabase,
    ): FavoritesDao = database.favoritesDao()

    @Provides
    fun providePlaylistDao(
        database: AudilyDatabase,
    ): PlaylistDao = database.playlistDao()
}