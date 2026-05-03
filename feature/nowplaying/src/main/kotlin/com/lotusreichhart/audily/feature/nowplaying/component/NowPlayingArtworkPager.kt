package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.playback.NowPlayingState
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingUiEvent
import com.lotusreichhart.audily.feature.nowplaying.NowPlayingUiState
import com.lotusreichhart.audily.feature.nowplaying.constants.SharedContentStateConstants.KEY_ARTWORK

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun NowPlayingArtworkPager(
    modifier: Modifier = Modifier,
    artworkModifier: Modifier = Modifier,
    uiState: NowPlayingUiState,
    onEvent: (NowPlayingUiEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.currentIndex.coerceAtLeast(0),
        pageCount = { uiState.queue.size }
    )

    var isProgrammaticScroll by remember { mutableStateOf(false) }
    var isLockingUserScroll by remember { mutableStateOf(false) }

    // 1. Hệ thống điều khiển Pager (Sync System -> Pager)
    LaunchedEffect(uiState.currentIndex) {
        if (uiState.currentIndex != -1) {
            if (uiState.currentIndex != pagerState.currentPage) {
                isProgrammaticScroll = true
                pagerState.animateScrollToPage(uiState.currentIndex)
            }
            // Mở khóa khi hệ thống đã cập nhật bài hát mới xong
            isLockingUserScroll = false
        }
    }

    // 2. Pager điều khiển hệ thống (Sync Pager -> System)
    LaunchedEffect(pagerState.settledPage) {
        if (isProgrammaticScroll) {
            isProgrammaticScroll = false
        } else {
            if (uiState.currentIndex != -1 && pagerState.settledPage != uiState.currentIndex) {
                isLockingUserScroll = true // Khóa tương tác ngay khi gạt xong bài đầu tiên
                onEvent(NowPlayingUiEvent.OnSkipTo(pagerState.settledPage))
            }
        }
    }

    val canScroll = !isLockingUserScroll
            && uiState.playbackState.nowPlayingState != NowPlayingState.BUFFERING
            && uiState.playbackState.nowPlayingState != NowPlayingState.IDLE

    val flingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(1)
    )

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        pageSpacing = LocalDimensions.current.paddingMedium,
        flingBehavior = flingBehavior,
        userScrollEnabled = canScroll,
        verticalAlignment = Alignment.CenterVertically
    ) { page ->
        val song = uiState.queue.getOrNull(page)
        val isCurrentPage = page == uiState.currentIndex

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            with(sharedTransitionScope) {
                AudilyArtwork(
                    artworkUri = song?.basic?.artworkUri,
                    modifier = Modifier
                        .then(
                            if (isCurrentPage && animatedVisibilityScope != null) {
                                Modifier.sharedElement(
                                    rememberSharedContentState(key = KEY_ARTWORK),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            } else Modifier
                        )
                        .then(artworkModifier)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}
