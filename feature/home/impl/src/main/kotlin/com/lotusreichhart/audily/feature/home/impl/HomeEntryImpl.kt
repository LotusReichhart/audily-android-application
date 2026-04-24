package com.lotusreichhart.audily.feature.home.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lotusreichhart.audily.feature.home.api.HomeEntry
import javax.inject.Inject

/**
 * Triển khai cụ thể của [HomeEntry].
 */
internal class HomeEntryImpl @Inject constructor() : HomeEntry {
    @Composable
    override fun Render(modifier: Modifier) {
        HomeScreen(
            modifier = modifier,
            onNavigateToPlaylists = {},
            onNavigateToAlbums = {}
        )
    }

    override fun createRoute(
        onNavigateToPlaylists: () -> Unit,
        onNavigateToAlbums: () -> Unit
    ): @Composable (Modifier) -> Unit = { modifier ->
        HomeScreen(
            modifier = modifier,
            onNavigateToPlaylists = onNavigateToPlaylists,
            onNavigateToAlbums = onNavigateToAlbums
        )
    }
}