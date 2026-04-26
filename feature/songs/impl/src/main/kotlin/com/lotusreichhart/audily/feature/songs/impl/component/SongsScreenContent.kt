package com.lotusreichhart.audily.feature.songs.impl.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.lotusreichhart.audily.core.common.util.TimeUtils
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.component.SongItem
import com.lotusreichhart.audily.core.designsystem.component.SongPlaybackStatus
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.LocalDynamicBottomPadding
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import com.lotusreichhart.audily.feature.songs.impl.SongsScreenConstants
import com.lotusreichhart.audily.feature.songs.impl.SongsUiEvent
import com.lotusreichhart.audily.feature.songs.impl.rememberSongsScreenState
import com.lotusreichhart.audily.feature.songs.impl.util.labelResId

/**
 * Nội dung chính của màn hình danh sách bài hát (khi đã tải xong).
 */
@Composable
internal fun SongsScreenContent(
    songs: LazyPagingItems<Song>,
    summary: SongsSummary,
    sortOrder: SongSortOrder,
    sortType: SortOrderType,
    playingSongId: Long?,
    isPaused: Boolean,
    onEvent: (SongsUiEvent) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val bottomPadding = LocalDynamicBottomPadding.current
    val screenState = rememberSongsScreenState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .nestedScroll(screenState.nestedScrollConnection)
    ) {
        val sortText = "${stringResource(sortOrder.labelResId())} - ${stringResource(sortType.labelResId())}"

        SongsHeader(
            songCount = summary.totalCount,
            totalDuration = TimeUtils.formatDuration(summary.totalDuration),
            sortText = sortText,
            onSortClick = { },
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth()
                .height(SongsScreenConstants.HeaderHeight)
                .graphicsLayer {
                    alpha = screenState.headerAlpha
                    translationY = screenState.headerOffset
                }
        )

        LazyColumn(
            state = screenState.lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = SongsScreenConstants.HeaderHeight,
                bottom = bottomPadding + LocalDimensions.current.paddingSmall,
            )
        ) {
            items(
                count = songs.itemCount,
                key = songs.itemKey { it.id },
                contentType = songs.itemContentType { "song" }
            ) { index ->
                val song = songs[index]
                if (song != null) {
                    val isCurrent = song.id == playingSongId
                    val playbackStatus = when {
                        !isCurrent -> SongPlaybackStatus.NONE
                        isPaused -> SongPlaybackStatus.PAUSED
                        else -> SongPlaybackStatus.PLAYING
                    }
                    
                    SongItem(
                        title = song.basic.title,
                        artist = song.basic.artist,
                        albumArt = {
                            AudilyArtwork(
                                artworkUri = song.basic.artworkUri,
                                modifier = Modifier.fillMaxSize()
                            )
                        },
                        isMissing = song.isMissing,
                        playbackStatus = playbackStatus,
                        onClick = { onEvent(SongsUiEvent.SongClicked(song.id)) },
                        onMenuClick = { }
                    )
                }
            }
        }
    }
}
