package com.lotusreichhart.audily.feature.home.impl.navigation

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lotusreichhart.audily.core.navigation.Navigator
import com.lotusreichhart.audily.feature.home.api.navigation.HomeNavKey
import com.lotusreichhart.audily.feature.home.impl.HomeScreen

fun EntryProviderScope<NavKey>.homeEntry(
    navigator: Navigator
) {
    entry<HomeNavKey> {
        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            onNavigateToPlaylists = { /* Sẽ xử lý điều hướng sau */ },
            onNavigateToAlbums = { /* Sẽ xử lý điều hướng sau */ }
        )
    }
}
