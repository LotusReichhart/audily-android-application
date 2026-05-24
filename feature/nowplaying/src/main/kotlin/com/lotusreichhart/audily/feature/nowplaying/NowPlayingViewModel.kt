package com.lotusreichhart.audily.feature.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.favorite.CheckSongFavoriteStatusUseCase
import com.lotusreichhart.audily.core.domain.usecase.favorite.ToggleFavoriteUseCase
import com.lotusreichhart.audily.core.domain.usecase.lyrics.ObserveLyricsUseCase
import com.lotusreichhart.audily.core.domain.usecase.lyrics.FetchAndSaveLyricsUseCase
import com.lotusreichhart.audily.core.domain.util.NetworkMonitor
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PlaybackControlUseCases
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObserveNowPlayingUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdatePlaybackParametersUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackPositionUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.timer.ObserveSleepTimerUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.timer.SetSleepTimerUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.UpdateSkipDurationUseCase
import com.lotusreichhart.audily.core.domain.usecase.prefs.GetUserPreferencesUseCase
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import timber.log.Timber
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
    private val observeLyricsUseCase: ObserveLyricsUseCase,
    private val fetchAndSaveLyricsUseCase: FetchAndSaveLyricsUseCase,
    private val networkMonitor: NetworkMonitor,
    private val getUserPreferences: GetUserPreferencesUseCase,
) : ViewModel() {

    private val _isLyricsVisible = MutableStateFlow(false)
    private val _isLyricsLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            observeNowPlaying()
                .map { it.song }
                .distinctUntilChanged { old, new -> old?.id == new?.id }
                .collect { song ->
                    if (song != null) {
                        val currentLyrics = observeLyricsUseCase(song.id).first()
                        if (currentLyrics == null) {
                            if (networkMonitor.isOnline.first()) {
                                _isLyricsLoading.value = true
                                try {
                                    fetchAndSaveLyricsUseCase(
                                        songId = song.id,
                                        title = song.basic.title,
                                        artist = song.basic.artist,
                                        durationMs = song.basic.duration
                                    )
                                } catch (e: Exception) {
                                    Timber.e(e, "Error fetching lyrics")
                                } finally {
                                    _isLyricsLoading.value = false
                                }
                            }
                        }
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<NowPlayingUiState> = observeNowPlaying()
        .flatMapLatest { data ->
            val songId = data.song?.id ?: -1L
            val favoriteFlow = if (songId != -1L) {
                checkSongFavoriteStatusUseCase(songId)
            } else {
                flowOf(false)
            }

            val lyricsFlow = if (songId != -1L) {
                observeLyricsUseCase(songId)
            } else {
                flowOf(null)
            }

            val lyricsStateFlow = combine(
                _isLyricsVisible,
                lyricsFlow,
                _isLyricsLoading
            ) { isVisible, lyrics, isLoading ->
                Triple(isVisible, lyrics, isLoading)
            }

            val useGlassmorphismFlow = getUserPreferences()
                .map { it.uiSettings.useGlassmorphism }
                .distinctUntilChanged()

            combine(
                favoriteFlow,
                observePlaybackPosition(),
                observeSleepTimer(),
                lyricsStateFlow,
                useGlassmorphismFlow
            ) { isFavorite, position, timer, lyricsState, useGlassmorphism ->
                val (isLyricsVisible, lyrics, isLyricsLoading) = lyricsState
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
                    isLyricsVisible = isLyricsVisible,
                    lyrics = lyrics,
                    isLyricsLoading = isLyricsLoading,
                    useGlassmorphism = useGlassmorphism
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
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
