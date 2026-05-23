package com.lotusreichhart.audily.feature.nowplaying.queue

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.designsystem.model.toUiPalette
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PauseSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.ResumeSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.GetRemainingQueueSummaryUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.MoveQueueItemUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.RemoveFromQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.SkipToIndexUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.StopQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObserveNowPlayingUseCase
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import com.lotusreichhart.audily.core.domain.usecase.playlist.CreatePlaylistUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.AddSongsToPlaylistUseCase
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.GlobalUiEventBus
import com.lotusreichhart.audily.core.ui.util.UiText
import com.lotusreichhart.audily.feature.nowplaying.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class QueueViewModel @Inject constructor(
    observeNowPlayingUseCase: ObserveNowPlayingUseCase,
    getRemainingQueueSummaryUseCase: GetRemainingQueueSummaryUseCase,
    private val moveQueueItemUseCase: MoveQueueItemUseCase,
    private val removeFromQueueUseCase: RemoveFromQueueUseCase,
    private val skipToIndexUseCase: SkipToIndexUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
    private val stopQueueUseCase: StopQueueUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val addSongsToPlaylistUseCase: AddSongsToPlaylistUseCase,
    private val globalUiEventBus: GlobalUiEventBus
) : ViewModel() {
    private val _uiState = mutableStateOf(QueueUiState())
    val uiState: State<QueueUiState> = _uiState

    private val _uiEffect = MutableSharedFlow<QueueUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        combine(
            observeNowPlayingUseCase(),
            getRemainingQueueSummaryUseCase()
        ) { nowPlaying, summary ->
            QueueUiState(
                queue = nowPlaying.queue,
                queueSummary = summary,
                playbackState = nowPlaying.playbackState,
                paletteColors = nowPlaying.colors?.toUiPalette(),
                currentIndex = nowPlaying.currentIndex
            )
        }
            .onEach { newState ->
                _uiState.value = newState
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: QueueUiEvent) {
        viewModelScope.launch {
            when (event) {
                is QueueUiEvent.OnMoveQueueItem -> moveQueueItemUseCase(
                    from = event.fromIndex,
                    to = event.toIndex
                )

                is QueueUiEvent.OnRemoveFromQueue -> removeFromQueueUseCase(event.songId)
                is QueueUiEvent.OnSongClicked -> handleSongClicked(event.index, event.songId)
                is QueueUiEvent.OnSkipToIndex -> skipToIndexUseCase(event.index)
                QueueUiEvent.OnStopQueue -> stopQueueUseCase()
                is QueueUiEvent.OnSaveQueueAsPlaylist -> {
                    val songIds = uiState.value.queue.map { it.id }
                    if (songIds.isEmpty()) return@launch
                    createPlaylistUseCase(event.name, event.description)
                        .onSuccess { playlistId ->
                            if (playlistId > 0) {
                                addSongsToPlaylistUseCase(playlistId, songIds)
                                globalUiEventBus.emit(
                                    GlobalUiEvent.ShowSnackbar(
                                        message = UiText.StringResource(R.string.feature_nowplaying_playlist_created_success)
                                    )
                                )
                                _uiEffect.emit(QueueUiEffect.NavigateToPlaylist(playlistId))
                            } else {
                                globalUiEventBus.emit(
                                    GlobalUiEvent.ShowSnackbar(
                                        message = UiText.StringResource(R.string.feature_nowplaying_playlist_created_failed)
                                    )
                                )
                            }
                        }
                        .onFailure {
                            globalUiEventBus.emit(
                                GlobalUiEvent.ShowSnackbar(
                                    message = UiText.StringResource(R.string.feature_nowplaying_playlist_created_failed)
                                )
                            )
                        }
                }
            }
        }
    }

    private suspend fun handleSongClicked(index: Int, songId: Long) {
        val state = uiState.value
        val currentPlayback = state.playbackState
        Timber.d("Current Playback: $currentPlayback")
        if (currentPlayback.currentSongId == songId) {
            if (currentPlayback.nowPlayingState == NowPlayingState.PLAYING) {
                pauseSongUseCase()
            } else {
                resumeSongUseCase()
            }
        } else {
            Timber.d("Skip index")
            skipToIndexUseCase(index)
        }
    }
}