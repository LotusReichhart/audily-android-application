package com.lotusreichhart.audily.core.data.di

import com.lotusreichhart.audily.core.data.repository.album.AlbumRepositoryImpl
import com.lotusreichhart.audily.core.data.repository.edittag.EditTagRepositoryImpl
import com.lotusreichhart.audily.core.data.repository.favorite.FavoritesRepositoryImpl
import com.lotusreichhart.audily.core.data.repository.history.HistoryRepositoryImpl
import com.lotusreichhart.audily.core.data.repository.playback.PlaybackStateListenerImpl
import com.lotusreichhart.audily.core.data.repository.playlist.PlaylistRepositoryImpl
import com.lotusreichhart.audily.core.data.repository.prefs.UserPreferencesRepositoryImpl
import com.lotusreichhart.audily.core.data.repository.song.SongRepositoryImpl
import com.lotusreichhart.audily.core.data.util.ConnectivityManagerNetworkMonitor
import com.lotusreichhart.audily.core.domain.repository.album.AlbumRepository
import com.lotusreichhart.audily.core.domain.repository.edittag.EditTagRepository
import com.lotusreichhart.audily.core.domain.repository.favorite.FavoritesRepository
import com.lotusreichhart.audily.core.domain.repository.history.HistoryRepository
import com.lotusreichhart.audily.core.domain.repository.playback.PlaybackStateListener
import com.lotusreichhart.audily.core.domain.repository.playlist.PlaylistRepository
import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.domain.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

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
    internal abstract fun bindsAlbumRepository(
        albumRepository: AlbumRepositoryImpl,
    ): AlbumRepository

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

    @Binds
    @IntoSet
    internal abstract fun bindsPlaybackStateListener(
        playbackStateListener: PlaybackStateListenerImpl,
    ): PlaybackStateListener

    @Binds
    internal abstract fun bindsHistoryRepository(
        historyRepository: HistoryRepositoryImpl,
    ): HistoryRepository

    @Binds
    internal abstract fun bindsEditTagRepository(
        editTagRepository: EditTagRepositoryImpl,
    ): EditTagRepository
}