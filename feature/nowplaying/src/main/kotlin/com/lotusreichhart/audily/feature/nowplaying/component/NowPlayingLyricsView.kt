package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.OnSurfaceDark
import com.lotusreichhart.audily.core.model.lyrics.Lyrics

@Composable
internal fun NowPlayingLyricsView(
    lyrics: Lyrics,
    currentPositionMs: Long,
    onLineClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        when (lyrics) {
            is Lyrics.Plain -> {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = LocalDimensions.current.paddingMedium,
                            vertical = LocalDimensions.current.paddingSmall
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = lyrics.plainLyrics.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            is Lyrics.Synced -> {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val segments = lyrics.syncedLyrics.segments
                    val activeIndex = remember(segments, currentPositionMs) {
                        val targetPosition = currentPositionMs + 1000L
                        val index = segments.indexOfLast { it.startTimeMillis <= targetPosition }
                        if (index == -1) 0 else index
                    }

                    val listState = rememberLazyListState()

                    LaunchedEffect(activeIndex) {
                        if (segments.isNotEmpty()) {
                            // Nếu item không nằm trong danh sách các items hiển thị, scroll tức thời tới item đó
                            val isVisible =
                                listState.layoutInfo.visibleItemsInfo.any { it.index == activeIndex }
                            if (!isVisible) {
                                listState.scrollToItem(activeIndex)
                            }

                            // Chờ layout pass hoàn tất và lấy thông tin item để căn giữa chính xác
                            val itemInfo = snapshotFlow {
                                listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == activeIndex }
                            }
                                .filterNotNull()
                                .first()

                            val viewportHeight = listState.layoutInfo.viewportSize.height
                            val viewportCenter = viewportHeight / 2
                            val childCenter = itemInfo.offset + itemInfo.size / 2

                            listState.animateScrollBy((childCenter - viewportCenter).toFloat())
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            bottom = maxHeight / 2
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        itemsIndexed(segments) { index, segment ->
                            val isActive = index == activeIndex
                            val color = if (isActive) {
                                MaterialTheme.colorScheme.primary
                            } else if (index < activeIndex) {
                                OnSurfaceDark.copy(alpha = 0.5f)
                            } else {
                                OnSurfaceDark
                            }
                            val fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal

                            Text(
                                text = segment.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = fontWeight,
                                    textAlign = TextAlign.Center
                                ),
                                color = color,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        onClick = { onLineClick(segment.startTimeMillis) },
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    )
                                    .padding(
                                        horizontal = LocalDimensions.current.paddingMedium,
                                        vertical = LocalDimensions.current.paddingSmall
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
