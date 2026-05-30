package com.lotusreichhart.audily.feature.search.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lotusreichhart.audily.core.domain.usecase.album.GetAlbumsUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.queue.PlayFromQueueUseCase
import com.lotusreichhart.audily.core.domain.usecase.playback.state.ObservePlaybackStateUseCase
import com.lotusreichhart.audily.core.domain.usecase.playlist.GetPlaylistsUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongIdsUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongsPagedUseCase
import com.lotusreichhart.audily.core.model.album.Album
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.search.api.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    observePlaybackStateUseCase: ObservePlaybackStateUseCase,
    private val getSongsPagedUseCase: GetSongsPagedUseCase,
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val getSongIdsUseCase: GetSongIdsUseCase,
    private val playFromQueueUseCase: PlayFromQueueUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _searchType = MutableStateFlow(SearchType.ALL)

    // Debounced query to avoid excessive database calls
    @OptIn(FlowPreview::class)
    private val _debouncedQuery = _query
        .debounce(300L)
        .distinctUntilChanged()

    private val _songs: Flow<PagingData<Song>> =
        combine(
            _debouncedQuery,
            _searchType
        ) { query, type ->
            query to type
        }.flatMapLatest { (query, type) ->
            if (query.isBlank()) {
                flowOf(PagingData.empty())
            } else if (type == SearchType.ALL || type == SearchType.SONGS) {
                getSongsPagedUseCase(searchQuery = query)
            } else {
                flowOf(PagingData.empty())
            }
        }.cachedIn(viewModelScope)

    private val _albums: Flow<List<Album>> = combine(
        _debouncedQuery,
        _searchType
    ) { query, type ->
        query to type
    }.flatMapLatest { (query, type) ->
        if (query.isBlank() || (type != SearchType.ALL && type != SearchType.ALBUMS)) {
            flowOf(emptyList())
        } else {
            getAlbumsUseCase(searchQuery = query)
        }
    }

    private val _playlists: Flow<List<Playlist>> =
        combine(_debouncedQuery, _searchType) { query, type ->
            query to type
        }.flatMapLatest { (query, type) ->
            if (query.isBlank() || (type != SearchType.ALL && type != SearchType.PLAYLISTS)) {
                flowOf(emptyList())
            } else {
                getPlaylistsUseCase(searchQuery = query)
            }
        }

    private val _allSongIds: Flow<List<Long>> = _debouncedQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            flowOf(emptyList())
        } else {
            getSongIdsUseCase(searchQuery = query)
        }
    }

    val uiState: StateFlow<SearchUiState> = combine(
        combine(_query, _searchType) { q, t -> q to t },
        _albums,
        _playlists,
        _allSongIds,
        observePlaybackStateUseCase()
    ) { (query, type), albums, playlists, allSongIds, playbackState ->
        SearchUiState(
            query = query,
            searchType = type,
            songs = _songs,
            playbackState = playbackState,
            albums = albums,
            playlists = playlists,
            allSongIds = allSongIds
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState()
    )

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.OnQueryChange -> {
                _query.value = event.query
            }

            is SearchUiEvent.OnSearchTypeChange -> {
                _searchType.value = event.type
            }

            is SearchUiEvent.OnSongClick -> {
                viewModelScope.launch {
                    val allIds = uiState.value.allSongIds
                    playFromQueueUseCase(
                        songId = event.songId,
                        queueIds = allIds
                    )
                }
            }
        }
    }
}
