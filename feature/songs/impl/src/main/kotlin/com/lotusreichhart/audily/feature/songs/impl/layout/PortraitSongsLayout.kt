package com.lotusreichhart.audily.feature.songs.impl.layout

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.lotusreichhart.audily.core.common.util.TimeUtils
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import com.lotusreichhart.audily.feature.songs.impl.SongsScreenState
import com.lotusreichhart.audily.feature.songs.impl.SongsUiEvent
import com.lotusreichhart.audily.feature.songs.impl.component.HeaderSticky
import com.lotusreichhart.audily.feature.songs.impl.component.SongSwipeItem
import com.lotusreichhart.audily.feature.songs.impl.component.SongsEmptyScreen
import com.lotusreichhart.audily.feature.songs.impl.component.SongsLoadingScreen
import com.lotusreichhart.audily.feature.songs.impl.component.SongsNotFoundScreen
import com.lotusreichhart.audily.feature.songs.impl.component.SongsTopBar
import com.lotusreichhart.audily.core.designsystem.util.getSongPlaybackStatus
import com.lotusreichhart.audily.feature.songs.impl.util.labelResId

@Composable
internal fun PortraitSongsLayout(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    isRefreshing: Boolean = false,
    wasRefreshed: Boolean = false,
    songs: LazyPagingItems<Song>,
    summary: SongsSummary,
    sortOrder: SongSortOrder,
    sortType: SortOrderType,
    playbackState: PlaybackState,
    screenState: SongsScreenState,
    onEvent: (SongsUiEvent) -> Unit,
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    onMenuClick: (song: Song) -> Unit
) {
    AudilyScaffold(
        topBar = {
            SongsTopBar(onBack = onBack, onSearch = onSearchClick)
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Crossfade(
            targetState = isLoading || isRefreshing,
            animationSpec = tween(durationMillis = 500),
            label = "SongsLoadingCrossfade"
        ) { loading ->
            if (loading) {
                SongsLoadingScreen(innerPadding = innerPadding)
            } else if (songs.itemCount == 0) {
                if (wasRefreshed) {
                    SongsNotFoundScreen(
                        innerPadding = innerPadding,
                        onRefreshClick = { onEvent(SongsUiEvent.Refresh) }
                    )
                } else {
                    SongsEmptyScreen(
                        innerPadding = innerPadding,
                        onScanClick = { onEvent(SongsUiEvent.Refresh) }
                    )
                }
            } else {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .nestedScroll(screenState.nestedScrollConnection)
                ) {
                    val sortText =
                        "${stringResource(sortOrder.labelResId())} - ${stringResource(sortType.labelResId())}"

                    var headerHeightPx by remember { mutableFloatStateOf(0f) }
                    val headerHeightDp = with(LocalDensity.current) { headerHeightPx.toDp() }

                    HeaderSticky(
                        songCount = summary.totalCount,
                        totalDuration = TimeUtils.formatDuration(summary.totalDuration),
                        sortText = sortText,
                        onSortClick = onSortClick,
                        modifier = Modifier
                            .zIndex(1f)
                            .fillMaxWidth()
                            .onSizeChanged { headerHeightPx = it.height.toFloat() }
                            .graphicsLayer {
                                alpha = screenState.headerAlpha
                                translationY = screenState.headerOffset
                            }
                    )

                    LazyColumn(
                        state = screenState.lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = headerHeightDp,
                        )
                    ) {
                        items(
                            count = songs.itemCount,
                            key = songs.itemKey { it.id },
                            contentType = songs.itemContentType { "song" }
                        ) { index ->
                            val song = songs[index]
                            if (song != null) {
                                val playbackStatus = getSongPlaybackStatus(song.id, playbackState)

                                SongSwipeItem(
                                    song = song,
                                    playbackStatus = playbackStatus,
                                    onEvent = onEvent,
                                    onMenuClick = onMenuClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}