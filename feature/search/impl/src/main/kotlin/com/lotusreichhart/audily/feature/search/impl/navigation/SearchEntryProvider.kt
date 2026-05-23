package com.lotusreichhart.audily.feature.search.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lotusreichhart.audily.core.navigation.Navigator
import com.lotusreichhart.audily.feature.albums.api.navigation.AlbumDetailNavKey
import com.lotusreichhart.audily.feature.playlists.api.navigation.PlaylistDetailNavKey
import com.lotusreichhart.audily.feature.search.api.SearchNavKey
import com.lotusreichhart.audily.feature.search.impl.SearchScreen

/**
 * Định nghĩa Entry cho tính năng Search trong hệ thống Navigation3.
 * Tuân thủ pattern EntryProvider để dễ dàng tích hợp vào NavHost.
 */
fun EntryProviderScope<NavKey>.searchEntry(
    navigator: Navigator
) {
    entry<SearchNavKey> { key ->
        SearchScreen(
            type = key.type,
            onBack = { navigator.goBack() },
            onAlbumItemClick = { albumId ->
                navigator.navigate(AlbumDetailNavKey(albumId))
            },
            onPlaylistItemClick = { playlistId ->
                navigator.navigate(PlaylistDetailNavKey(playlistId))
            }
        )
    }
}
