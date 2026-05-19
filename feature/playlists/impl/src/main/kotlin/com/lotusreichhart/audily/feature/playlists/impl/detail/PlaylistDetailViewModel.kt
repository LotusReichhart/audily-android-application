package com.lotusreichhart.audily.feature.playlists.impl.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.playback.control.ResumeSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PauseSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackStateUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.PlayFromQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.DeletePlaylistUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.GetPlaylistWithSongsUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.RemoveSongFromPlaylistUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.UpdatePlaylistMetadataUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.UpdatePlaylistSongsOrderUseCase
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongsSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
internal class PlaylistDetailViewModel @Inject constructor(
    observePlaybackStateUseCase: ObservePlaybackStateUseCase,
    private val getPlaylistWithSongsUseCase: GetPlaylistWithSongsUseCase,
    private val updatePlaylistSongsOrderUseCase: UpdatePlaylistSongsOrderUseCase,
    private val updatePlaylistMetadataUseCase: UpdatePlaylistMetadataUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val removeSongFromPlaylistUseCase: RemoveSongFromPlaylistUseCase,
    private val playFromQueueUseCase: PlayFromQueueUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
) : ViewModel() {

    private val _playlistId = MutableStateFlow<Long?>(null)
    private val _isInitialLoading = MutableStateFlow(true)
    private val _isDataLoadingStarted = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            // Chờ animation trượt của BottomBar hoàn tất
            delay(500)
            _isDataLoadingStarted.value = true
            // Tổng thời gian hiện Shimmer
            delay(1500)
            _isInitialLoading.value = false
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _playlistAndSongs: Flow<Pair<Playlist, List<Song>>?> = combine(
        _playlistId,
        _isDataLoadingStarted
    ) { id, isStarted ->
        if (id != null && isStarted) id else null
    }.flatMapLatest { id ->
        if (id == null) flowOf(null) else getPlaylistWithSongsUseCase(id)
    }

    val uiState: StateFlow<PlaylistDetailUiState> = combine(
        _isInitialLoading,
        _playlistAndSongs,
        observePlaybackStateUseCase()
    ) { isInitialLoading, data, playback ->
        val songs = data?.second ?: emptyList()
        PlaylistDetailUiState(
            playlist = data?.first,
            songs = songs,
            songsSummary = SongsSummary(
                totalCount = songs.size,
                totalDuration = songs.sumOf { it.basic.duration }
            ),
            songIds = songs.map { it.id },
            isLoading = isInitialLoading,
            playbackState = playback
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlaylistDetailUiState(isLoading = true)
    )

    fun onEvent(event: PlaylistDetailUiEvent) {
        when (event) {
            is PlaylistDetailUiEvent.Init -> {
                if (_playlistId.value == null) {
                    _playlistId.value = event.playlistId
                }
            }

            is PlaylistDetailUiEvent.EditMetadata -> {
                val id = _playlistId.value ?: return
                viewModelScope.launch {
                    updatePlaylistMetadataUseCase(id, event.name, event.description)
                }
            }

            PlaylistDetailUiEvent.DeletePlaylist -> {
                val id = _playlistId.value ?: return
                viewModelScope.launch {
                    deletePlaylistUseCase(id)
                }
            }

            is PlaylistDetailUiEvent.RemoveSong -> {
                val id = _playlistId.value ?: return
                viewModelScope.launch {
                    removeSongFromPlaylistUseCase(id, event.songId)
                }
            }

            is PlaylistDetailUiEvent.ReorderSongs -> {
                val id = _playlistId.value ?: return
                viewModelScope.launch {
                    val currentSongs = uiState.value.songs
                    val newOrderIds = currentSongs.toMutableList().apply {
                        add(event.toIndex, removeAt(event.fromIndex))
                    }.map { song -> song.id }

                    updatePlaylistSongsOrderUseCase(id, newOrderIds)
                }
            }

            PlaylistDetailUiEvent.PlayAll -> {
                viewModelScope.launch {
                    val songIds = uiState.value.songs.map { it.id }
                    if (songIds.isNotEmpty()) {
                        playFromQueueUseCase(songId = songIds.first(), queueIds = songIds)
                    }
                }
            }

            is PlaylistDetailUiEvent.SongClicked -> {
                if (!event.isMissing) {
                    viewModelScope.launch {
                        val state = uiState.value
                        val currentPlayback = state.playbackState
                        if (currentPlayback.currentSongId == event.songId) {
                            if (currentPlayback.nowPlayingState == NowPlayingState.PLAYING) {
                                pauseSongUseCase()
                            } else {
                                resumeSongUseCase()
                            }
                        } else {
                            val songIds = state.songs.map { it.id }
                            playFromQueueUseCase(songId = event.songId, queueIds = songIds)
                        }
                    }
                }
            }
        }
    }
}
