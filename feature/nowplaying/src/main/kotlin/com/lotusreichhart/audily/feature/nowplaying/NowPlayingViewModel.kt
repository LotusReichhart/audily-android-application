package com.lotusreichhart.audily.feature.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PlaybackControlUseCases
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObserveNowPlayingUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackPositionUseCase
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import com.lotusreichhart.audily.core.designsystem.model.toUiPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    observeNowPlaying: ObserveNowPlayingUseCase,
    observePlaybackPosition: ObservePlaybackPositionUseCase,
    private val controls: PlaybackControlUseCases
) : ViewModel() {

    private val _isLyricsVisible = MutableStateFlow(false)

    init {
        // Khôi phục phiên phát nhạc đã lưu khi ViewModel khởi tạo
        viewModelScope.launch {
            controls.restoreSession()
        }
    }

    val uiState: StateFlow<NowPlayingUiState> = combine(
        observeNowPlaying(),
        observePlaybackPosition(),
        _isLyricsVisible
    ) { data, position, isLyricsVisible ->
        NowPlayingUiState(
            playbackState = data.playbackState,
            playbackPositionMs = position,
            currentSong = data.song,
            queue = data.queue,
            currentIndex = data.currentIndex,
            skipDuration = data.skipDuration,
            paletteColors = data.colors?.toUiPalette(),
            hasNext = data.hasNext,
            hasPrevious = data.hasPrevious,
            isLyricsVisible = isLyricsVisible
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NowPlayingUiState()
    )

    fun onEvent(event: NowPlayingUiEvent) {
        viewModelScope.launch {
            when (event) {
                NowPlayingUiEvent.OnResumePauseToggle -> {
                    val state = uiState.value.playbackState.nowPlayingState
                    if (state == NowPlayingState.PLAYING) {
                        controls.pause()
                    } else {
                        controls.resume()
                    }
                }

                NowPlayingUiEvent.OnSkipNext -> controls.skipToNext()
                NowPlayingUiEvent.OnSkipPrevious -> controls.skipToPrevious()
                is NowPlayingUiEvent.OnSkipTo -> controls.skipToIndex(event.index)
                is NowPlayingUiEvent.OnSeekTo -> controls.seekTo(event.position)
                NowPlayingUiEvent.OnFastForward -> controls.fastForward()
                NowPlayingUiEvent.OnFastRewind -> controls.fastRewind()

                NowPlayingUiEvent.OnShuffleToggle -> {
                    controls.setShuffle(!uiState.value.playbackState.isShuffleOn)
                }

                NowPlayingUiEvent.OnRepeatModeToggle -> {
                    val nextMode = when (uiState.value.playbackState.repeatMode) {
                        RepeatMode.OFF -> RepeatMode.ALL
                        RepeatMode.ALL -> RepeatMode.ONE
                        RepeatMode.ONE -> RepeatMode.OFF
                    }
                    controls.setRepeatMode(nextMode)
                }

                NowPlayingUiEvent.OnToggleFavorite -> { /* TODO */
                }

                NowPlayingUiEvent.OnToggleLyrics -> {
                    _isLyricsVisible.value = !_isLyricsVisible.value
                }
            }
        }
    }
}
