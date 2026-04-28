package com.lotusreichhart.audily.feature.nowplaying

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import com.lotusreichhart.audily.core.designsystem.theme.AudilyTheme
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import com.lotusreichhart.audily.core.model.song.BasicSongMetadata
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingControls
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingExtension
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingHeader
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingInfo
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingMenu
import com.lotusreichhart.audily.feature.nowplaying.component.NowPlayingProgress

@Composable
fun NowPlayingScreen(
    modifier: Modifier = Modifier,
    viewModel: NowPlayingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NowPlayingScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
internal fun NowPlayingScreenContent(
    uiState: NowPlayingUiState,
    onEvent: (NowPlayingUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var isLyricsVisible by remember { mutableStateOf(false) }
    var isMenuVisible by remember { mutableStateOf(false) }

    val defaultColor = MaterialTheme.colorScheme.background
    var adaptiveColor by remember { mutableStateOf(defaultColor) }

    // Reset adaptive color when song changes
    LaunchedEffect(uiState.currentSong?.id) {
        adaptiveColor = defaultColor
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = adaptiveColor
    ) {
        if (isLandscape) {
            NowPlayingScreenLandscape(
                uiState = uiState,
                isLyricsVisible = isLyricsVisible,
                onLyricsToggle = { isLyricsVisible = !isLyricsVisible },
                isMenuVisible = isMenuVisible,
                onMenuToggle = { isMenuVisible = !isMenuVisible },
                onEvent = onEvent,
                onColorExtracted = { adaptiveColor = it }
            )
        } else {
            NowPlayingScreenPortrait(
                uiState = uiState,
                isLyricsVisible = isLyricsVisible,
                onLyricsToggle = { isLyricsVisible = !isLyricsVisible },
                isMenuVisible = isMenuVisible,
                onMenuToggle = { isMenuVisible = !isMenuVisible },
                onEvent = onEvent,
                onColorExtracted = { adaptiveColor = it }
            )
        }
    }
}

@Composable
private fun NowPlayingScreenPortrait(
    uiState: NowPlayingUiState,
    isLyricsVisible: Boolean,
    onLyricsToggle: () -> Unit,
    isMenuVisible: Boolean,
    onMenuToggle: () -> Unit,
    onEvent: (NowPlayingUiEvent) -> Unit,
    onColorExtracted: (Color) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        NowPlayingHeader(
            onCloseClick = { onEvent(NowPlayingUiEvent.OnNavigateBack) },
            onMenuClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(
            targetState = isLyricsVisible,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            modifier = Modifier.weight(1f)
        ) { targetIsLyricsVisible ->
            if (targetIsLyricsVisible) {
                // TODO: Lyrics Content
            } else {
                AudilyArtwork(
                    artworkUri = uiState.currentSong?.basic?.artworkUri,
                    modifier = Modifier.fillMaxSize(),
                    onColorExtracted = onColorExtracted
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        NowPlayingInfo(
            title = uiState.currentSong?.basic?.title ?: "Unknown Title",
            artist = uiState.currentSong?.basic?.artist ?: "Unknown Artist",
            isFavorite = uiState.currentSong?.isFavorite ?: false,
            onFavoriteClick = { onEvent(NowPlayingUiEvent.OnToggleFavorite) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        NowPlayingProgress(
            progressMs = uiState.playbackState.playbackPosition,
            durationMs = uiState.playbackState.duration,
            onSeek = { onEvent(NowPlayingUiEvent.OnSeekTo(it)) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        NowPlayingControls(
            isPlaying = uiState.playbackState.nowPlayingState == NowPlayingState.PLAYING,
            isShuffleOn = uiState.playbackState.isShuffleOn,
            repeatMode = uiState.playbackState.repeatMode,
            onPlayPauseClick = { onEvent(NowPlayingUiEvent.OnPlayPauseToggle) },
            onSkipNextClick = { onEvent(NowPlayingUiEvent.OnSkipNext) },
            onSkipPreviousClick = { onEvent(NowPlayingUiEvent.OnSkipPrevious) },
            onFastForwardClick = { onEvent(NowPlayingUiEvent.OnFastForward) },
            onFastRewindClick = { onEvent(NowPlayingUiEvent.OnFastRewind) },
            onShuffleClick = { onEvent(NowPlayingUiEvent.OnShuffleToggle) },
            onRepeatClick = { onEvent(NowPlayingUiEvent.OnRepeatModeToggle) }
        )

        AnimatedVisibility(
            visible = isMenuVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            NowPlayingMenu(
                onTimerClick = { /* TODO */ },
                onSpeedClick = { /* TODO */ },
                onJumpClick = { /* TODO */ },
                onRingtoneClick = { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        NowPlayingExtension(
            isLyricsVisible = isLyricsVisible,
            onQueueClick = { onEvent(NowPlayingUiEvent.OnOpenQueue) },
            onLyricsClick = onLyricsToggle,
            onExtendClick = onMenuToggle
        )
    }
}

@Composable
private fun NowPlayingScreenLandscape(
    uiState: NowPlayingUiState,
    isLyricsVisible: Boolean,
    onLyricsToggle: () -> Unit,
    isMenuVisible: Boolean,
    onMenuToggle: () -> Unit,
    onEvent: (NowPlayingUiEvent) -> Unit,
    onColorExtracted: (Color) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Left Side: Artwork/Lyrics (2/5)
        Box(modifier = Modifier.weight(2f)) {
            AnimatedContent(
                targetState = isLyricsVisible,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(
                        animationSpec = tween(
                            500
                        )
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) { targetIsLyricsVisible ->
                if (targetIsLyricsVisible) {
                    // TODO: Lyrics Content
                } else {
                    AudilyArtwork(
                        artworkUri = uiState.currentSong?.basic?.artworkUri,
                        modifier = Modifier.fillMaxSize(),
                        onColorExtracted = onColorExtracted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Right Side: Info & Controls (3/5)
        Column(modifier = Modifier.weight(3f)) {
            NowPlayingHeader(
                onCloseClick = { onEvent(NowPlayingUiEvent.OnNavigateBack) },
                onMenuClick = { /* TODO */ }
            )

            Spacer(modifier = Modifier.weight(1f))

            NowPlayingInfo(
                title = uiState.currentSong?.basic?.title ?: "Unknown Title",
                artist = uiState.currentSong?.basic?.artist ?: "Unknown Artist",
                isFavorite = uiState.currentSong?.isFavorite ?: false,
                onFavoriteClick = { onEvent(NowPlayingUiEvent.OnToggleFavorite) },
                maxTitleChars = 40, // Increased for landscape
                maxArtistChars = 40
            )

            Spacer(modifier = Modifier.height(16.dp))

            NowPlayingProgress(
                progressMs = uiState.playbackState.playbackPosition,
                durationMs = uiState.playbackState.duration,
                onSeek = { onEvent(NowPlayingUiEvent.OnSeekTo(it)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            NowPlayingControls(
                isPlaying = uiState.playbackState.nowPlayingState == NowPlayingState.PLAYING,
                isShuffleOn = uiState.playbackState.isShuffleOn,
                repeatMode = uiState.playbackState.repeatMode,
                onPlayPauseClick = { onEvent(NowPlayingUiEvent.OnPlayPauseToggle) },
                onSkipNextClick = { onEvent(NowPlayingUiEvent.OnSkipNext) },
                onSkipPreviousClick = { onEvent(NowPlayingUiEvent.OnSkipPrevious) },
                onFastForwardClick = { onEvent(NowPlayingUiEvent.OnFastForward) },
                onFastRewindClick = { onEvent(NowPlayingUiEvent.OnFastRewind) },
                onShuffleClick = { onEvent(NowPlayingUiEvent.OnShuffleToggle) },
                onRepeatClick = { onEvent(NowPlayingUiEvent.OnRepeatModeToggle) }
            )

            AnimatedVisibility(
                visible = isMenuVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                NowPlayingMenu(
                    onTimerClick = { /* TODO */ },
                    onSpeedClick = { /* TODO */ },
                    onJumpClick = { /* TODO */ },
                    onRingtoneClick = { /* TODO */ }
                )
            }

            NowPlayingExtension(
                isLyricsVisible = isLyricsVisible,
                onQueueClick = { onEvent(NowPlayingUiEvent.OnOpenQueue) },
                onLyricsClick = onLyricsToggle,
                onExtendClick = onMenuToggle
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

private val MockUiState = NowPlayingUiState(
    playbackState = PlaybackState(
        nowPlayingState = NowPlayingState.PLAYING,
        currentSongId = 1L,
        duration = 180000,
        playbackPosition = 60000,
        isShuffleOn = true,
        repeatMode = RepeatMode.ALL
    ),
    currentSong = Song(
        id = 1L,
        basic = BasicSongMetadata(
            title = "Sunflower",
            artist = "Post Malone, Swae Lee",
            album = "Spider-Man: Into the Spider-Verse",
            albumId = 101L,
            duration = 180000,
            path = "/sdcard/music/sunflower.mp3",
            artworkUri = null
        ),
        isFavorite = true
    )
)

@Preview(showBackground = true)
@Composable
private fun NowPlayingScreenPortraitPreview() {
    AudilyTheme {
        Surface {
            NowPlayingScreenContent(
                uiState = MockUiState,
                onEvent = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
private fun NowPlayingScreenLandscapePreview() {
    // Để preview landscape, ta cần cung cấp một LocalConfiguration giả lập hoặc dùng tham số Preview
    // Ở đây ta mô phỏng bằng cách gọi trực tiếp NowPlayingScreenLandscape nếu cần,
    // hoặc tin tưởng vào hệ thống Preview phối hợp với logic Configuration.ORIENTATION_LANDSCAPE.
    AudilyTheme {
        Surface {
            NowPlayingScreenContent(
                uiState = MockUiState,
                onEvent = {}
            )
        }
    }
}