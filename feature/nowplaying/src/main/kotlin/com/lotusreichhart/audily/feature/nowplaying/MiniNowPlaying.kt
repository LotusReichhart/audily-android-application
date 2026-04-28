package com.lotusreichhart.audily.feature.nowplaying

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview
import com.lotusreichhart.audily.core.designsystem.theme.AudilyTheme
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.feature.nowplaying.component.MiniPlayerContent

@Composable
fun MiniNowPlaying(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val defaultColor = MaterialTheme.colorScheme.surfaceVariant
    var adaptiveColor by remember { mutableStateOf(defaultColor) }

    // Reset adaptive color when song changes
    LaunchedEffect(uiState.currentSong?.id) {
        adaptiveColor = defaultColor
    }

    val progress = if (uiState.playbackState.duration > 0) {
        uiState.playbackState.playbackPosition.toFloat() / uiState.playbackState.duration
    } else 0f

    MiniPlayerContent(
        title = uiState.currentSong?.basic?.title ?: "Unknown Title",
        artist = uiState.currentSong?.basic?.artist ?: "Unknown Artist",
        artworkUri = uiState.currentSong?.basic?.artworkUri,
        isPlaying = uiState.playbackState.nowPlayingState == NowPlayingState.PLAYING,
        progress = progress,
        onPlayPauseClick = { viewModel.onEvent(NowPlayingUiEvent.OnPlayPauseToggle) },
        onNextClick = { viewModel.onEvent(NowPlayingUiEvent.OnSkipNext) },
        onQueueClick = { viewModel.onEvent(NowPlayingUiEvent.OnOpenQueue) },
        onClick = onClick,
        modifier = modifier,
        backgroundColor = adaptiveColor,
        onColorExtracted = { adaptiveColor = it }
    )
}

@Preview(showBackground = true)
@Composable
private fun MiniNowPlayingPreview() {
    AudilyTheme {
        Surface {
            MiniPlayerContent(
                title = "Sunflower",
                artist = "Post Malone, Swae Lee",
                artworkUri = null,
                isPlaying = true,
                progress = 0.4f,
                onPlayPauseClick = {},
                onNextClick = {},
                onQueueClick = {},
                onClick = {}
            )
        }
    }
}
