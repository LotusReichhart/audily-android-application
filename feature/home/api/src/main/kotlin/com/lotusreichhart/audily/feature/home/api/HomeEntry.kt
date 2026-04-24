package com.lotusreichhart.audily.feature.home.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lotusreichhart.audily.core.navigation.FeatureEntry

interface HomeEntry : FeatureEntry {
    fun createRoute(
        onNavigateToPlaylists: () -> Unit,
        onNavigateToAlbums: () -> Unit
    ): @Composable (Modifier) -> Unit
}
