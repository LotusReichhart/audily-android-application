package com.lotusreichhart.audily.core.data.di

import com.lotusreichhart.audily.core.data.repository.favorite.FavoritesRepositoryImpl
import com.lotusreichhart.audily.core.data.repository.playlist.PlaylistRepositoryImpl
import com.lotusreichhart.audily.core.data.repository.prefs.UserPreferencesRepositoryImpl
import com.lotusreichhart.audily.core.data.repository.song.SongRepositoryImpl
import com.lotusreichhart.audily.core.data.util.ConnectivityManagerNetworkMonitor
import com.lotusreichhart.audily.core.domain.repository.favorite.FavoritesRepository
import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.domain.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun bindsSongRepository(
        songRepository: SongRepositoryImpl,
    ): SongRepository

    @Binds
    internal abstract fun bindsPlaylistRepository(
        playlistRepository: PlaylistRepositoryImpl,
    ): PlaylistRepository

    @Binds
    internal abstract fun bindsFavoritesRepository(
        favoritesRepository: FavoritesRepositoryImpl,
    ): FavoritesRepository

    @Binds
    internal abstract fun bindsUserPreferencesRepository(
        userPreferencesRepository: UserPreferencesRepositoryImpl,
    ): UserPreferencesRepository
}