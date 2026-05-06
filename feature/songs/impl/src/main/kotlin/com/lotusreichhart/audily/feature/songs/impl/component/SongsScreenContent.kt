package com.lotusreichhart.audily.feature.songs.impl.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.lotusreichhart.audily.core.designsystem.component.SongPlaybackStatus
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.core.model.playback.PlaybackState
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import com.lotusreichhart.audily.feature.songs.impl.SongsUiEvent
import com.lotusreichhart.audily.feature.songs.impl.rememberSongsScreenState
import com.lotusreichhart.audily.feature.songs.impl.util.labelResId

/**
 * Nội dung chính của màn hình danh sách bài hát (khi đã tải xong).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SongsScreenContent(
    songs: LazyPagingItems<Song>,
    summary: SongsSummary,
    sortOrder: SongSortOrder,
    sortType: SortOrderType,
    playbackState: PlaybackState,
    onEvent: (SongsUiEvent) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val screenState = rememberSongsScreenState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .nestedScroll(screenState.nestedScrollConnection)
    ) {
        val sortText =
            "${stringResource(sortOrder.labelResId())} - ${stringResource(sortType.labelResId())}"

        var headerHeightPx by remember { mutableStateOf(0f) }
        val headerHeightDp = with(LocalDensity.current) { headerHeightPx.toDp() }

        SongsHeader(
            songCount = summary.totalCount,
            totalDuration = TimeUtils.formatDuration(summary.totalDuration),
            sortText = sortText,
            onSortClick = { },
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
                    val playbackStatus = getPlaybackStatus(song.id, playbackState)

                    SongSwipeItem(
                        song = song,
                        playbackStatus = playbackStatus,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

private fun getPlaybackStatus(songId: Long, playbackState: PlaybackState): SongPlaybackStatus {
    val isCurrent = songId == playbackState.currentSongId
    return when {
        !isCurrent -> SongPlaybackStatus.NONE
        playbackState.nowPlayingState == NowPlayingState.PAUSED -> SongPlaybackStatus.PAUSED
        else -> SongPlaybackStatus.PLAYING
    }
}
