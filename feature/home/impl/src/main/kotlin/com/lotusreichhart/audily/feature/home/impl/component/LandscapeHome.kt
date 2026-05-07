package com.lotusreichhart.audily.feature.home.impl.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.feature.home.impl.HomeUiState
import com.lotusreichhart.audily.core.model.song.Song

@Composable
internal fun LandscapeHome(
    uiState: HomeUiState,
    onNavigateToSongs: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onPlaySong: (Long, List<Song>) -> Unit,
    onShuffleAll: () -> Unit,
    onResumePlayback: () -> Unit,
    modifier: Modifier = Modifier
) {
    AudilyScaffold(
        topBar = {
            HomeTopBar(
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onSearchClick = { /* TODO */ }
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when (uiState) {
            HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is HomeUiState.Success -> {
                HomeContent(
                    homeVibe = uiState.homeVibe,
                    onSongClick = { songId ->
                        onPlaySong(songId, uiState.homeVibe.discovery) // Simplified for now
                    },
                    onShuffleAllClick = onShuffleAll,
                    onResumeClick = onResumePlayback,
                    contentPadding = innerPadding
                )
            }
            is HomeUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message ?: "Error", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}