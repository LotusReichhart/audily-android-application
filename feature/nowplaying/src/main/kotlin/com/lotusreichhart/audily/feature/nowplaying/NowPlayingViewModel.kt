package com.lotusreichhart.audily.feature.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.playback.control.FastForwardUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.FastRewindUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PauseSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PlaySongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.ResumeSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.SeekToUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.SkipToNextUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.SkipToPreviousUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.settings.SetRepeatModeUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.settings.SetShuffleUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackStateUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongUseCase
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    observePlaybackStateUseCase: ObservePlaybackStateUseCase,
    private val getSongUseCase: GetSongUseCase,
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val playSongUseCase: PlaySongUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val skipToNextUseCase: SkipToNextUseCase,
    private val skipToPreviousUseCase: SkipToPreviousUseCase,
    private val seekToUseCase: SeekToUseCase,
    private val fastForwardUseCase: FastForwardUseCase,
    private val fastRewindUseCase: FastRewindUseCase,
    private val setShuffleUseCase: SetShuffleUseCase,
    private val setRepeatModeUseCase: SetRepeatModeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NowPlayingUiState())
    val uiState: StateFlow<NowPlayingUiState> = combine(
        observePlaybackStateUseCase(),
        getUserPreferencesUseCase()
    ) { playbackState, userPrefs ->
        _uiState.value.copy(
            playbackState = playbackState,
            jumpInterval = userPrefs.playbackSettings.jumpInterval
        )
    }.flatMapLatest { state ->
        val songId = state.playbackState.currentSongId
        if (songId != null) {
            getSongUseCase(songId).map { song ->
                state.copy(currentSong = song)
            }
        } else {
            flowOf(state.copy(currentSong = null))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NowPlayingUiState()
    )

    fun onEvent(event: NowPlayingUiEvent) {
        viewModelScope.launch {
            when (event) {
                NowPlayingUiEvent.OnPlayPauseToggle -> {
                    val state = uiState.value.playbackState.nowPlayingState
                    if (state == NowPlayingState.PLAYING) {
                        pauseSongUseCase()
                    } else if (state == NowPlayingState.PAUSED) {
                        resumeSongUseCase()
                    }
                }

                NowPlayingUiEvent.OnSkipNext -> skipToNextUseCase()
                NowPlayingUiEvent.OnSkipPrevious -> skipToPreviousUseCase()
                is NowPlayingUiEvent.OnSeekTo -> seekToUseCase(event.position)
                NowPlayingUiEvent.OnFastForward -> fastForwardUseCase()
                NowPlayingUiEvent.OnFastRewind -> fastRewindUseCase()
                NowPlayingUiEvent.OnShuffleToggle -> {
                    val currentShuffle = uiState.value.playbackState.isShuffleOn
                    setShuffleUseCase(!currentShuffle)
                }

                NowPlayingUiEvent.OnRepeatModeToggle -> {
                    val nextMode = when (uiState.value.playbackState.repeatMode) {
                        RepeatMode.OFF -> RepeatMode.ALL
                        RepeatMode.ALL -> RepeatMode.ONE
                        RepeatMode.ONE -> RepeatMode.OFF
                    }
                    setRepeatModeUseCase(nextMode)
                }

                NowPlayingUiEvent.OnToggleFavorite -> { /* TODO */
                }

                NowPlayingUiEvent.OnToggleLyrics -> { /* Handled in UI for now */
                }

                NowPlayingUiEvent.OnSetRingtone -> { /* TODO */
                }

                NowPlayingUiEvent.OnTimerClick -> { /* TODO */
                }

                NowPlayingUiEvent.OnOpenQueue -> { /* TODO */
                }

                NowPlayingUiEvent.OnNavigateBack -> { /* TODO: Close Full Player */
                }
            }
        }
    }
}
