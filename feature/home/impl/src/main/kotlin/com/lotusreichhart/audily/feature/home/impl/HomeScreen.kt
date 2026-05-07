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
import com.lotusreichhart.audily.core.model.song.Song

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
        onPlaySong = viewModel::playSong,
        onShuffleAll = viewModel::shuffleAll,
        onResumePlayback = viewModel::resumePlayback
    )
}

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    onNavigateToSongs: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onPlaySong: (Long, List<Song>) -> Unit,
    onShuffleAll: () -> Unit,
    onResumePlayback: () -> Unit,
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
                onPlaySong = onPlaySong,
                onShuffleAll = onShuffleAll,
                onResumePlayback = onResumePlayback
            )
        },
        landscape = {
            LandscapeHome(
                uiState = uiState,
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onPlaySong = onPlaySong,
                onShuffleAll = onShuffleAll,
                onResumePlayback = onResumePlayback
            )
        },
        expanded = {
            ExpandedHome(
                uiState = uiState,
                modifier = modifier,
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onPlaySong = onPlaySong,
                onShuffleAll = onShuffleAll,
                onResumePlayback = onResumePlayback
            )
        }
    )
}
