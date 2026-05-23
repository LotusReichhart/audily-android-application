package com.lotusreichhart.audily.feature.albums.impl.navigation

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lotusreichhart.audily.core.navigation.Navigator
import com.lotusreichhart.audily.feature.albums.api.navigation.AlbumDetailNavKey
import com.lotusreichhart.audily.feature.albums.api.navigation.AlbumsNavKey
import com.lotusreichhart.audily.feature.albums.impl.AlbumsScreen
import com.lotusreichhart.audily.feature.search.api.SearchNavKey
import com.lotusreichhart.audily.feature.search.api.SearchType

import com.lotusreichhart.audily.feature.albums.impl.detail.AlbumDetailScreen
import com.lotusreichhart.audily.feature.playlists.api.navigation.PlaylistDetailNavKey

fun EntryProviderScope<NavKey>.albumsEntry(
    navigator: Navigator
) {
    entry<AlbumsNavKey> {
        AlbumsScreen(
            modifier = Modifier.fillMaxSize(),
            onBack = { navigator.goBack() },
            onSearch = { navigator.navigate(SearchNavKey(type = SearchType.ALBUMS)) },
            onAlbumClick = { albumId -> navigator.navigate(AlbumDetailNavKey(id = albumId)) }
        )
    }

    entry<AlbumDetailNavKey> { key ->
        AlbumDetailScreen(
            albumId = key.id,
            onBack = { navigator.goBack() },
            onNavigateToPlaylist = { playlistId ->
                navigator.navigate(PlaylistDetailNavKey(id = playlistId))
            }
        )
    }
}
