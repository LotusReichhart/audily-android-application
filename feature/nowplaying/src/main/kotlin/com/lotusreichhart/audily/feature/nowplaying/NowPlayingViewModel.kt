package com.lotusreichhart.audily.feature.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.favorite.CheckSongFavoriteStatusUseCase
import com.lotusreichhart.audily.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PlaybackControlUseCases
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObserveNowPlayingUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdatePlaybackParametersUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackPositionUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.timer.ObserveSleepTimerUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.timer.SetSleepTimerUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateSkipDurationUseCase
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import com.lotusreichhart.audily.core.designsystem.model.toUiPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    observeNowPlaying: ObserveNowPlayingUseCase,
    observePlaybackPosition: ObservePlaybackPositionUseCase,
    observeSleepTimer: ObserveSleepTimerUseCase,
    private val controls: PlaybackControlUseCases,
    private val updatePlaybackParameters: UpdatePlaybackParametersUseCase,
    private val setSleepTimer: SetSleepTimerUseCase,
    private val updateSkipDuration: UpdateSkipDurationUseCase,
    private val checkSongFavoriteStatusUseCase: CheckSongFavoriteStatusUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _isLyricsVisible = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<NowPlayingUiState> = observeNowPlaying()
        .flatMapLatest { data ->
            val songId = data.song?.id ?: -1L
            val favoriteFlow = if (songId != -1L) {
                checkSongFavoriteStatusUseCase(songId)
            } else {
                flowOf(false)
            }

            combine(
                favoriteFlow,
                observePlaybackPosition(),
                observeSleepTimer(),
                _isLyricsVisible
            ) { isFavorite, position, timer, isLyricsVisible ->
                NowPlayingUiState(
                    playbackState = data.playbackState,
                    playbackPositionMs = position,
                    sleepTimerStatus = timer,
                    currentSong = data.song?.copy(isFavorite = isFavorite),
                    queue = data.queue,
                    currentIndex = data.currentIndex,
                    skipDuration = data.skipDuration,
                    paletteColors = data.colors?.toUiPalette(),
                    hasNext = data.hasNext,
                    hasPrevious = data.hasPrevious,
                    isLyricsVisible = isLyricsVisible
                )
            }
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

                NowPlayingUiEvent.OnToggleFavorite -> {
                    val songId = uiState.value.currentSong?.id
                    if (songId != null) {
                        toggleFavoriteUseCase(songId)
                    }
                }

                NowPlayingUiEvent.OnToggleLyrics -> {
                    _isLyricsVisible.value = !_isLyricsVisible.value
                }

                is NowPlayingUiEvent.OnSetSpeedAndPitch -> {
                    controls.setSpeedAndPitch(event.speed, event.pitch)
                }

                is NowPlayingUiEvent.OnSavePlaybackParameters -> {
                    updatePlaybackParameters(event.speed, event.pitch)
                }

                is NowPlayingUiEvent.OnSetSleepTimer -> {
                    val durationMs = event.minutes?.let { it * 60 * 1000L } ?: 0L
                    setSleepTimer(durationMs)
                }

                is NowPlayingUiEvent.OnSaveSkipDuration -> {
                    updateSkipDuration(event.durationSeconds)
                }
            }
        }
    }
}
