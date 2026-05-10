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

    LaunchedEffect(uiState) {
        if (uiState !is HomeUiState.Loading) {
            onInitialLoadingFinished()
        }
    }

    HomeScreen(
        uiState = uiState,
        modifier = modifier,
        onNavigateToSongs = onNavigateToSongs,
        onNavigateToPlaylists = onNavigateToPlaylists,
        onNavigateToAlbums = onNavigateToAlbums,
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    onNavigateToSongs: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onEvent: (HomeUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AudilyAdaptiveLayout(
        compact = {
            CompactHome(
                uiState = uiState,
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onEvent = onEvent
            )
        },
        landscape = {
            LandscapeHome(
                uiState = uiState,
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onEvent = onEvent
            )
        },
        expanded = {
            ExpandedHome(
                uiState = uiState,
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onEvent = onEvent
            )
        }
    )
}
