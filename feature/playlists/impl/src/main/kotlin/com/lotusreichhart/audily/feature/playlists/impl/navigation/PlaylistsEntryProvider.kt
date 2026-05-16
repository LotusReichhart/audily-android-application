package com.lotusreichhart.audily.feature.playlists.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lotusreichhart.audily.core.navigation.Navigator
import com.lotusreichhart.audily.feature.playlists.api.navigation.PlaylistsNavKey
import com.lotusreichhart.audily.feature.playlists.impl.PlaylistsScreen
import com.lotusreichhart.audily.feature.search.api.SearchNavKey
import com.lotusreichhart.audily.feature.search.api.SearchType

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
            }
        )
    }
}