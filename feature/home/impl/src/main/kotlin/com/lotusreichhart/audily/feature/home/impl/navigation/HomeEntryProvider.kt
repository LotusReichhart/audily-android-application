package com.lotusreichhart.audily.feature.home.impl.navigation

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lotusreichhart.audily.core.navigation.Navigator
import com.lotusreichhart.audily.feature.albums.api.navigation.AlbumsNavKey
import com.lotusreichhart.audily.feature.home.api.navigation.HomeNavKey
import com.lotusreichhart.audily.feature.home.impl.HomeScreen
import com.lotusreichhart.audily.feature.playlists.api.navigation.PlaylistsNavKey
import com.lotusreichhart.audily.feature.search.api.SearchNavKey
import com.lotusreichhart.audily.feature.search.api.SearchType
import com.lotusreichhart.audily.feature.songs.api.navigation.SongsNavKey

fun EntryProviderScope<NavKey>.homeEntry(
    navigator: Navigator,
    onInitialLoadingFinished: () -> Unit
) {
    entry<HomeNavKey> {
        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            onNavigateToSongs = { navigator.navigate(SongsNavKey) },
            onNavigateToPlaylists = { navigator.navigate(PlaylistsNavKey) },
            onNavigateToAlbums = { navigator.navigate(AlbumsNavKey) },
            onSearchClick = {
                navigator.navigate(SearchNavKey(type = SearchType.ALL))
            },
            onInitialLoadingFinished = onInitialLoadingFinished
        )
    }
}
