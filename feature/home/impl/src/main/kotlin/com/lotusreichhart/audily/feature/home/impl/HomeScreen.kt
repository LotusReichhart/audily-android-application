package com.lotusreichhart.audily.feature.home.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.ui.adaptive.AudilyAdaptiveLayout
import com.lotusreichhart.audily.feature.home.impl.layout.PortraitHomeLayout
import com.lotusreichhart.audily.feature.home.impl.layout.ExpandedHomeLayout
import com.lotusreichhart.audily.feature.home.impl.layout.LandscapeHomeLayout
import com.lotusreichhart.audily.feature.home.impl.layout.MediumHomeLayout

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSongs: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onSearchClick: () -> Unit,
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
        onSearchClick = onSearchClick,
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    onNavigateToSongs: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onSearchClick: () -> Unit,
    onEvent: (HomeUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AudilyAdaptiveLayout(
        portrait = {
            PortraitHomeLayout(
                uiState = uiState,
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onSearchClick = onSearchClick,
                onEvent = onEvent
            )
        },
        landscape = {
            LandscapeHomeLayout(
                uiState = uiState,
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onSearchClick = onSearchClick,
                onEvent = onEvent
            )
        },
        medium = {
            MediumHomeLayout(
                uiState = uiState,
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onSearchClick = onSearchClick,
                onEvent = onEvent
            )
        },
        expanded = {
            ExpandedHomeLayout(
                uiState = uiState,
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onSearchClick = onSearchClick,
                onEvent = onEvent
            )
        }
    )
}
