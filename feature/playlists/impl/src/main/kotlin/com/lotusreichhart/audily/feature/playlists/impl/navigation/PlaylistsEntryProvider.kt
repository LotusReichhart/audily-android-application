package com.lotusreichhart.audily.feature.playlists.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lotusreichhart.audily.core.navigation.Navigator
import com.lotusreichhart.audily.feature.favorites.api.navigation.FavoritesNavKey
import com.lotusreichhart.audily.feature.playlists.api.navigation.PlaylistDetailNavKey
import com.lotusreichhart.audily.feature.playlists.api.navigation.PlaylistsNavKey
import com.lotusreichhart.audily.feature.playlists.api.navigation.PlaylistsPickerNavKey
import com.lotusreichhart.audily.feature.playlists.impl.PlaylistsScreen
import com.lotusreichhart.audily.feature.playlists.impl.detail.PlaylistDetailScreen
import com.lotusreichhart.audily.feature.playlists.impl.picker.PlaylistsPickerScreen
import com.lotusreichhart.audily.feature.search.api.SearchNavKey
import com.lotusreichhart.audily.feature.search.api.SearchType

import com.lotusreichhart.audily.feature.songs.api.navigation.SongsPickerNavKey

fun EntryProviderScope<NavKey>.playlistsEntry(
    navigator: Navigator
) {
    entry<PlaylistsNavKey> {
        PlaylistsScreen(
            onBack = {
                navigator.goBack()
            },
            onSearch = {
                navigator.navigate(SearchNavKey(type = SearchType.PLAYLISTS))
            },
            onPlaylistClick = { playlistId ->
                navigator.navigate(PlaylistDetailNavKey(id = playlistId))
            },
            onFavoriteClick = {
                navigator.navigate(FavoritesNavKey)
            }
        )
    }

    entry<PlaylistDetailNavKey> { key ->
        PlaylistDetailScreen(
            playlistId = key.id,
            onBack = {
                navigator.goBack()
            },
            onNavigateToAddSongs = { playlistId ->
                navigator.navigate(SongsPickerNavKey(playlistId = playlistId))
            }
        )
    }

    entry<PlaylistsPickerNavKey> { key ->
        PlaylistsPickerScreen(
            songId = key.songId,
            onBack = {
                navigator.goBack()
            }
        )
    }
}