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
    private val stopQueueUseCase: StopQueueUseCase
) : ViewModel() {
    private val _uiState = mutableStateOf(QueueUiState())
    val uiState: State<QueueUiState> = _uiState

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