package com.lotusreichhart.audily.feature.search.impl

import androidx.paging.PagingData
import com.lotusreichhart.audily.core.model.album.Album
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.search.api.SearchType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Trạng thái giao diện của màn hình Tìm kiếm.
 */
data class SearchUiState(
    val query: String = "",
    val searchType: SearchType = SearchType.ALL,
    val songs: Flow<PagingData<Song>> = flowOf(PagingData.empty()),
    val playbackState: PlaybackState = PlaybackState.INITIAL,
    val albums: List<Album> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val allSongIds: List<Long> = emptyList(),
    val isLoading: Boolean = false
)
