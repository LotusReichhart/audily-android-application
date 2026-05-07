package com.lotusreichhart.audily.feature.home.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.home.GetHomeVibeUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.ResumeSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.PlayFromQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongIdsUseCase
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    getHomeVibeUseCase: GetHomeVibeUseCase,
    private val playFromQueueUseCase: PlayFromQueueUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val getSongIdsUseCase: GetSongIdsUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getHomeVibeUseCase()
        .onStart { delay(2000) }
        .map<_, HomeUiState> { homeVibe ->
            HomeUiState.Success(homeVibe)
        }
        .catch { emit(HomeUiState.Error(it.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )

    fun playSong(songId: Long, contextSongs: List<Song>) {
        viewModelScope.launch {
            playFromQueueUseCase(songId, contextSongs.map { it.id })
        }
    }

    fun shuffleAll() {
        viewModelScope.launch {
            val allIds = getSongIdsUseCase(sortOrder = SongSortOrder.TITLE).first()
            if (allIds.isNotEmpty()) {
                val shuffled = allIds.shuffled()
                playFromQueueUseCase(shuffled.first(), shuffled)
            }
        }
    }

    fun resumePlayback() {
        viewModelScope.launch {
            resumeSongUseCase()
        }
    }
}
