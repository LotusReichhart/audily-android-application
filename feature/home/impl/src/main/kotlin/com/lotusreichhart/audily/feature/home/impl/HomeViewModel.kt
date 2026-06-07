package com.lotusreichhart.audily.feature.home.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.history.DeleteHistoryUseCase
import com.lotusreichhart.audily.core.domain.usecase.home.GetHomeVibeUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.ResumeSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.PlayFromQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackStateUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongIdsUseCase
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    getHomeVibeUseCase: GetHomeVibeUseCase,
    observePlaybackStateUseCase: ObservePlaybackStateUseCase,
    private val playFromQueueUseCase: PlayFromQueueUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val getSongIdsUseCase: GetSongIdsUseCase,
    private val deleteHistoryUseCase: DeleteHistoryUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        getHomeVibeUseCase(),
        observePlaybackStateUseCase()
    ) { homeVibe, playbackState ->
        HomeUiState.Success(homeVibe, playbackState) as HomeUiState
    }.catch { emit(HomeUiState.Error(it.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )

    fun onEvent(event: HomeUiEvent) {
        viewModelScope.launch {
            when (event) {
                is HomeUiEvent.OnSongClick -> {
                    playFromQueueUseCase(event.songId, event.contextSongs.map { it.id })
                }

                HomeUiEvent.OnShuffleAll -> {
                    val allIds = getSongIdsUseCase(sortOrder = SongSortOrder.TITLE).first()
                    if (allIds.isNotEmpty()) {
                        val shuffled = allIds.shuffled()
                        playFromQueueUseCase(shuffled.first(), shuffled)
                    }
                }

                HomeUiEvent.OnResume -> {
                    resumeSongUseCase()
                }

                is HomeUiEvent.OnDeleteFromHistory -> {
                    deleteHistoryUseCase(event.songId)
                }
            }
        }
    }
}
