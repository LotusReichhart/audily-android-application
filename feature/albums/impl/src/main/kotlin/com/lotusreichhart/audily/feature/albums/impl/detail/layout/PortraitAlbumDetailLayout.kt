package com.lotusreichhart.audily.feature.albums.impl.detail.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.component.AudilyButton
import com.lotusreichhart.audily.core.designsystem.component.SongItem
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.getSongPlaybackStatus
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.albums.impl.R
import com.lotusreichhart.audily.feature.albums.impl.detail.AlbumDetailUiEvent
import com.lotusreichhart.audily.feature.albums.impl.detail.AlbumDetailUiState
import com.lotusreichhart.audily.feature.albums.impl.detail.component.AlbumDetailTopBar

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PortraitAlbumDetailLayout(
    uiState: AlbumDetailUiState,
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    onEvent: (AlbumDetailUiEvent) -> Unit,
    onSongMenuClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val lazyListState = rememberLazyListState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val density = LocalDensity.current
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val minHeaderHeight = 40.dp + statusBarPadding

    val songsCount = uiState.songs.size
    val songItemHeight = 64.dp
    val stickyActionsHeight = 80.dp
    val sectionTitleHeight = 40.dp
    val contentBelowHeaderHeight = stickyActionsHeight + sectionTitleHeight + (songItemHeight * songsCount)

    val bottomSpacerHeight = remember(screenHeight, minHeaderHeight, contentBelowHeaderHeight) {
        val requiredHeight = screenHeight - minHeaderHeight
        if (contentBelowHeaderHeight < requiredHeight) {
            requiredHeight - contentBelowHeaderHeight
        } else {
            0.dp
        }
    }

    val headerHeightPx = remember(density, screenWidth) { with(density) { screenWidth.toPx() } }
    val minHeaderHeightPx = remember(density) { with(density) { minHeaderHeight.toPx() } }
    val scrollRangePx = headerHeightPx - minHeaderHeightPx

    val collapseFraction by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex > 0) {
                1f
            } else {
                (lazyListState.firstVisibleItemScrollOffset.toFloat() / scrollRangePx).coerceIn(
                    0f,
                    1f
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Top Bar
        AlbumDetailTopBar(
            title = uiState.album?.title ?: "",
            collapseFraction = collapseFraction,
            onBackClick = onBack,
            onMenuClick = onMenuClick,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(2f)
        )

        // Album Artwork Collapsible Header
        val album = uiState.album
        if (album != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenWidth)
                    .graphicsLayer {
                        // Parallax scrolling translation
                        val offset = if (lazyListState.firstVisibleItemIndex == 0) {
                            lazyListState.firstVisibleItemScrollOffset.toFloat()
                        } else {
                            scrollRangePx
                        }
                        translationY = -offset * 0.5f
                        scaleY = 1f - (collapseFraction * 0.1f)
                        alpha = 1f - collapseFraction
                    }
                    .zIndex(0f)
            ) {
                AudilyArtwork(
                    artworkUri = album.albumArtUri,
                    modifier = Modifier.fillMaxSize(),
                    isAspectRatio = false,
                    contentDescription = album.title
                )

                // Gradient overlay at the bottom for readability of texts
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Text Details overlaying the artwork
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(dimensions.paddingMedium)
                ) {
                    Text(
                        text = album.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = album.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Scrollable Content
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            // Header spacer pushing down the content
            item {
                Spacer(modifier = Modifier.height(screenWidth))
            }

            // Sticky actions container (Play & Shuffle buttons)
            stickyHeader {
                val stickySpacerHeight = minHeaderHeight * collapseFraction
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.background
                        )
                        .padding(horizontal = dimensions.paddingMedium)
                ) {
                    Spacer(modifier = Modifier.height(stickySpacerHeight))
                    Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AudilyButton(
                            text = stringResource(R.string.feature_albums_impl_detail_play),
                            onClick = { onEvent(AlbumDetailUiEvent.PlayAll) },
                            modifier = Modifier.weight(1f),
                            leadingIcon = AudilyIcons.Resume
                        )

                        AudilyButton(
                            text = stringResource(R.string.feature_albums_impl_detail_shuffle),
                            onClick = { onEvent(AlbumDetailUiEvent.Shuffle) },
                            modifier = Modifier.weight(1f),
                            leadingIcon = AudilyIcons.Shuffle,
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                    Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                }
            }

            // Section Title: "Songs"
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = dimensions.paddingMedium)
                ) {
                    Text(
                        text = stringResource(R.string.feature_albums_impl_detail_songs),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(dimensions.paddingSmall))
                }
            }

            // Song List
            itemsIndexed(uiState.songs) { _, song ->
                val playbackStatus = getSongPlaybackStatus(song.id, uiState.playbackState)
                Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    SongItem(
                        title = song.basic.title,
                        artist = song.basic.artist,
                        albumArt = {
                            AudilyArtwork(
                                artworkUri = song.basic.artworkUri,
                                modifier = Modifier.fillMaxSize()
                            )
                        },
                        onClick = {
                            onEvent(AlbumDetailUiEvent.SongClicked(song.id, song.isMissing))
                        },
                        onMenuClick = {
                            onSongMenuClick(song)
                        },
                        playbackStatus = playbackStatus,
                        isMissing = song.isMissing
                    )
                }
            }

            if (bottomSpacerHeight > 0.dp) {
                item {
                    Spacer(modifier = Modifier.height(bottomSpacerHeight))
                }
            }

            item {
                Spacer(modifier = Modifier.height(dimensions.paddingMedium))
            }
        }
    }
}
