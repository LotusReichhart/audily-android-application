package com.lotusreichhart.audily.feature.search.impl

import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.component.SongItem
import com.lotusreichhart.audily.core.designsystem.component.SongPlaybackStatus
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.ui.GlobalMenuCaller
import com.lotusreichhart.audily.core.ui.GlobalParams
import com.lotusreichhart.audily.core.ui.GlobalSheetKey
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.LocalGlobalUiEventBus
import com.lotusreichhart.audily.feature.search.api.SearchType
import com.lotusreichhart.audily.feature.search.impl.component.SearchAlbumItem
import com.lotusreichhart.audily.feature.search.impl.component.SearchPlaylistItem
import com.lotusreichhart.audily.feature.search.impl.component.SearchQueryEmpty
import com.lotusreichhart.audily.feature.search.impl.component.SearchResultEmpty
import com.lotusreichhart.audily.feature.search.impl.component.SearchTopBar

import com.lotusreichhart.audily.feature.songs.api.R as songsR
import com.lotusreichhart.audily.feature.playlists.api.R as playlistsR
import com.lotusreichhart.audily.feature.albums.api.R as albumsR

@Composable
internal fun SearchScreen(
    type: SearchType,
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val songs = uiState.songs.collectAsLazyPagingItems()
    val globalUiEventBus = LocalGlobalUiEventBus.current

    LaunchedEffect(Unit) {
        viewModel.onEvent(SearchUiEvent.OnSearchTypeChange(type))
    }

    SearchScreen(
        uiState = uiState,
        songs = songs,
        onBack = onBack,
        onEvent = viewModel::onEvent,
        onMenuClick = { song ->
            globalUiEventBus.emit(
                GlobalUiEvent.OpenSheet(
                    key = GlobalSheetKey.SONG_MENU,
                    params = mapOf(
                        GlobalParams.PARAM_SONG to song,
                        GlobalParams.PARAM_CALLER to GlobalMenuCaller.LIST_SCREEN,
                        GlobalParams.PARAM_QUEUE_IDS to uiState.allSongIds
                    ),
                    isShowDragHandle = false
                )
            )
        }
    )
}

@Composable
internal fun SearchScreen(
    uiState: SearchUiState,
    songs: LazyPagingItems<Song>,
    onBack: () -> Unit,
    onEvent: (SearchUiEvent) -> Unit,
    onMenuClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    val isEmptyQuery = uiState.query.isBlank()
    val isNoResults = !isEmptyQuery &&
            songs.itemCount == 0 &&
            uiState.albums.isEmpty() &&
            uiState.playlists.isEmpty()

    AudilyScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SearchTopBar(
                query = uiState.query,
                type = uiState.searchType,
                onQueryChange = { onEvent(SearchUiEvent.OnQueryChange(it)) },
                onBack = onBack,
                modifier = Modifier.focusRequester(focusRequester)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isEmptyQuery -> {
                    SearchQueryEmpty(type = uiState.searchType)
                }

                isNoResults -> {
                    SearchResultEmpty(type = uiState.searchType)
                }

                uiState.searchType == SearchType.ALL -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        // Albums Section
                        if (uiState.albums.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(albumsR.string.feature_albums_api_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(
                                        vertical = dimensions.paddingSmall,
                                        horizontal = dimensions.paddingMedium
                                    )
                                )
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = dimensions.paddingSmall),
                                    horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                                ) {
                                    items(uiState.albums) { album ->
                                        SearchAlbumItem(
                                            album = album,
                                            onClick = { onEvent(SearchUiEvent.OnAlbumClick(it.id)) },
                                            isVertical = true
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                            }
                        }

                        // Playlists Section
                        if (uiState.playlists.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(playlistsR.string.feature_playlists_api_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(
                                        vertical = dimensions.paddingSmall,
                                        horizontal = dimensions.paddingMedium
                                    )
                                )
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = dimensions.paddingSmall),
                                    horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                                ) {
                                    items(uiState.playlists) { playlist ->
                                        SearchPlaylistItem(
                                            playlist = playlist,
                                            onClick = { onEvent(SearchUiEvent.OnPlaylistClick(it.id)) },
                                            isVertical = true
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                            }
                        }

                        // Songs Section
                        if (songs.itemCount > 0) {
                            item {
                                Text(
                                    text = stringResource(songsR.string.feature_songs_api_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(
                                        vertical = dimensions.paddingSmall,
                                        horizontal = dimensions.paddingMedium
                                    )
                                )
                            }
                            items(songs.itemCount) { index ->
                                val song = songs[index]
                                if (song != null) {
                                    SongItem(
                                        title = song.basic.title,
                                        artist = song.basic.artist,
                                        albumArt = {
                                            AudilyArtwork(
                                                artworkUri = song.basic.artworkUri,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        },
                                        onClick = { onEvent(SearchUiEvent.OnSongClick(song.id)) },
                                        onMenuClick = { onMenuClick(song) },
                                        isMissing = song.isMissing,
                                        isFavorite = song.isFavorite,
                                        playbackStatus = SongPlaybackStatus.NONE
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Specific search types (SONGS, ALBUMS, PLAYLISTS)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (uiState.searchType) {
                            SearchType.SONGS -> {
                                items(songs.itemCount) { index ->
                                    val song = songs[index]
                                    if (song != null) {
                                        SongItem(
                                            title = song.basic.title,
                                            artist = song.basic.artist,
                                            albumArt = {
                                                AudilyArtwork(
                                                    artworkUri = song.basic.artworkUri,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            },
                                            onClick = { onEvent(SearchUiEvent.OnSongClick(song.id)) },
                                            onMenuClick = { onMenuClick(song) },
                                            isMissing = song.isMissing,
                                            isFavorite = song.isFavorite,
                                            playbackStatus = SongPlaybackStatus.NONE
                                        )
                                    }
                                }
                            }

                            SearchType.ALBUMS -> {
                                items(uiState.albums) { album ->
                                    SearchAlbumItem(
                                        album = album,
                                        onClick = { onEvent(SearchUiEvent.OnAlbumClick(it.id)) },
                                        isVertical = false
                                    )
                                }
                            }

                            SearchType.PLAYLISTS -> {
                                items(uiState.playlists) { playlist ->
                                    SearchPlaylistItem(
                                        playlist = playlist,
                                        onClick = { onEvent(SearchUiEvent.OnPlaylistClick(it.id)) },
                                        isVertical = false
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
