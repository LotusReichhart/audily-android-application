package com.lotusreichhart.audily.feature.home.impl.layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.feature.home.impl.HomeUiState
import com.lotusreichhart.audily.feature.home.impl.HomeUiEvent
import com.lotusreichhart.audily.feature.home.impl.component.HomeContent
import com.lotusreichhart.audily.feature.home.impl.component.HomeContentShimmer
import com.lotusreichhart.audily.feature.home.impl.component.HomeTopBar

@Composable
internal fun ExpandedHomeLayout(
    uiState: HomeUiState,
    onNavigateToSongs: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onSearchClick: () -> Unit,
    onEvent: (HomeUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AudilyScaffold(
        topBar = {
            HomeTopBar(
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onSearchClick = onSearchClick
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when (uiState) {
            HomeUiState.Loading -> {
                HomeContentShimmer(contentPadding = innerPadding)
            }
            is HomeUiState.Success -> {
                HomeContent(
                    homeVibe = uiState.homeVibe,
                    onNavigateToSongs = onNavigateToSongs,
                    onEvent = onEvent,
                    contentPadding = innerPadding
                )
            }
            is HomeUiState.Error -> {

            }
        }
    }
}