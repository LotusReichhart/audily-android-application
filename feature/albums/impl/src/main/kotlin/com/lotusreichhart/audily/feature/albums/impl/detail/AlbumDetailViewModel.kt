package com.lotusreichhart.audily.feature.albums.impl.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lotusreichhart.audily.core.domain.usecase.album.GetAlbumSongsUseCase
import com.lotusreichhart.audily.core.domain.usecase.album.GetAlbumUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.PauseSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.control.ResumeSongUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.PlayFromQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackStateUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.AddSongsToPlaylistUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.CreatePlaylistUseCase
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.song.SongsSummary
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.GlobalUiEventBus
import com.lotusreichhart.audily.core.ui.util.UiText
import com.lotusreichhart.audily.feature.albums.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import com.lotusreichhart.audily.core.common.result.Result
import com.lotusreichhart.audily.core.common.result.asResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AlbumDetailViewModel @Inject constructor(
    observePlaybackStateUseCase: ObservePlaybackStateUseCase,
    private val getAlbumUseCase: GetAlbumUseCase,
    private val getAlbumSongsUseCase: GetAlbumSongsUseCase,
    private val playFromQueueUseCase: PlayFromQueueUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val addSongsToPlaylistUseCase: AddSongsToPlaylistUseCase,
    private val globalUiEventBus: GlobalUiEventBus,
) : ViewModel() {

    private val _albumId = MutableStateFlow<Long?>(null)
    private val _effects = MutableSharedFlow<AlbumDetailUiEffect>()
    val effects: SharedFlow<AlbumDetailUiEffect> = _effects.asSharedFlow()

    private val _albumResult = _albumId.flatMapLatest { id ->
        if (id == null) flowOf(Result.Success(null))
        else getAlbumUseCase(id).asResult()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Result.Loading
    )

    private val _songsResult = _albumId.flatMapLatest { id ->
        if (id == null) flowOf(Result.Success(emptyList()))
        else getAlbumSongsUseCase(id).asResult()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Result.Loading
    )

    val uiState: StateFlow<AlbumDetailUiState> = combine(
        _albumResult,
        _songsResult,
        observePlaybackStateUseCase()
    ) { albumResult, songsResult, playback ->
        val album = if (albumResult is Result.Success) albumResult.data else null
        val songs = if (songsResult is Result.Success) songsResult.data else emptyList()
        AlbumDetailUiState(
            album = album,
            songs = songs,
            songsSummary = SongsSummary(
                totalCount = songs.size,
                totalDuration = songs.sumOf { it.basic.duration }
            ),
            songIds = songs.map { it.id },
            isLoading = albumResult is Result.Loading || songsResult is Result.Loading,
            playbackState = playback
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AlbumDetailUiState(isLoading = true)
    )

    fun onEvent(event: AlbumDetailUiEvent) {
        when (event) {
            is AlbumDetailUiEvent.Init -> {
                if (_albumId.value == null) {
                    _albumId.value = event.albumId
                }
            }

            AlbumDetailUiEvent.PlayAll -> {
                viewModelScope.launch {
                    val songIds = uiState.value.songs.map { it.id }
                    if (songIds.isNotEmpty()) {
                        playFromQueueUseCase(songId = songIds.first(), queueIds = songIds)
                    }
                }
            }

            AlbumDetailUiEvent.Shuffle -> {
                viewModelScope.launch {
                    val songIds = uiState.value.songs.map { it.id }.shuffled()
                    if (songIds.isNotEmpty()) {
                        playFromQueueUseCase(songId = songIds.first(), queueIds = songIds)
                    }
                }
            }

            is AlbumDetailUiEvent.OnSaveAsPlaylist -> {
                val songIds = uiState.value.songs.map { it.id }
                if (songIds.isEmpty()) return
                viewModelScope.launch {
                    createPlaylistUseCase(name = event.name, description = event.description)
                        .onSuccess { playlistId ->
                            if (playlistId > 0) {
                                addSongsToPlaylistUseCase(playlistId, songIds)
                                globalUiEventBus.emit(
                                    GlobalUiEvent.ShowSnackbar(
                                        message = UiText.StringResource(R.string.feature_albums_impl_detail_playlist_created_success)
                                    )
                                )
                                _effects.emit(AlbumDetailUiEffect.NavigateToPlaylist(playlistId))
                            } else {
                                globalUiEventBus.emit(
                                    GlobalUiEvent.ShowSnackbar(
                                        message = UiText.StringResource(R.string.feature_albums_impl_detail_playlist_created_failed)
                                    )
                                )
                            }
                        }
                        .onFailure {
                            globalUiEventBus.emit(
                                GlobalUiEvent.ShowSnackbar(
                                    message = UiText.StringResource(R.string.feature_albums_impl_detail_playlist_created_failed)
                                )
                            )
                        }
                }
            }

            is AlbumDetailUiEvent.SongClicked -> {
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