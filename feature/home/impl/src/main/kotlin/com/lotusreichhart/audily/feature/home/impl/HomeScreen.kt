package com.lotusreichhart.audily.feature.home.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.ui.adaptive.AudilyAdaptiveLayout
import com.lotusreichhart.audily.feature.home.impl.component.CompactHome
import com.lotusreichhart.audily.feature.home.impl.component.ExpandedHome
import com.lotusreichhart.audily.feature.home.impl.component.LandscapeHome
import timber.log.Timber

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSongs: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onInitialLoadingFinished: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            onInitialLoadingFinished()
        }
    }


    HomeScreen(
        modifier = modifier,
        onNavigateToSongs = onNavigateToSongs,
        onNavigateToPlaylists = onNavigateToPlaylists,
        onNavigateToAlbums = onNavigateToAlbums
    )
}

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSongs: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit
) {
    AudilyAdaptiveLayout(
        compact = {
            CompactHome(
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums
            )
        },
        landscape = {
            LandscapeHome(
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums
            )
        },
        expanded = {
            // Logic: Tablet/Fold sẽ dùng Landscape cho đến khi có Two-Pane đặc thù
            ExpandedHome(
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums
            )
        }
    )
}
