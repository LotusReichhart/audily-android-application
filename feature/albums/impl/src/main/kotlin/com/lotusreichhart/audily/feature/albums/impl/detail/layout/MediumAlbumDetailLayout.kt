package com.lotusreichhart.audily.feature.albums.impl.detail.layout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.common.util.TimeUtils
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.component.AudilyButton
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.component.SongItem
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.getSongPlaybackStatus
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.albums.impl.R
import com.lotusreichhart.audily.feature.albums.impl.detail.AlbumDetailUiEvent
import com.lotusreichhart.audily.feature.albums.impl.detail.AlbumDetailUiState

/**
 * Layout cho chế độ Medium (Tablet portrait, màn hình gập) của màn hình chi tiết Album.
 * Sử dụng bố cục dạng Row để tránh ảnh bìa bị phóng quá to.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MediumAlbumDetailLayout(
    uiState: AlbumDetailUiState,
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    onEvent: (AlbumDetailUiEvent) -> Unit,
    onSongMenuClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val lazyListState = rememberLazyListState()

    AudilyScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        vertical = dimensions.paddingSmall,
                        horizontal = dimensions.paddingMedium
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AudilyIconButton(
                    onClick = onBack,
                    painter = painterResource(id = AudilyIcons.ArrowLeft),
                    contentDescription = "Back",
                    containerSize = 24.dp,
                    iconSize = 24.dp,
                    tint = MaterialTheme.colorScheme.onBackground
                )

                AudilyIconButton(
                    onClick = onMenuClick,
                    painter = painterResource(id = AudilyIcons.VerticalDot),
                    contentDescription = "Menu",
                    containerSize = 24.dp,
                    iconSize = 24.dp,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                val album = uiState.album
                if (album != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensions.paddingMedium)
                            .padding(top = dimensions.paddingMedium, bottom = dimensions.paddingSmall),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                    ) {
                        AudilyArtwork(
                            artworkUri = album.albumArtUri,
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(dimensions.cornerRadiusMedium)),
                            isAspectRatio = false,
                            contentDescription = album.title
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(dimensions.paddingExtraSmall)
                        ) {
                            Text(
                                text = album.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = album.artist,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "${album.songCount} ${stringResource(com.lotusreichhart.audily.core.designsystem.R.string.core_designsystem_songs)} • ${TimeUtils.formatDuration(uiState.songsSummary.totalDuration)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = dimensions.paddingMedium)
                        .padding(vertical = dimensions.paddingSmall),
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
            }

            // Section Title: "Songs"
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = dimensions.paddingMedium)
                ) {
                    Spacer(modifier = Modifier.height(dimensions.paddingSmall))
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

            item {
                Spacer(modifier = Modifier.height(dimensions.paddingMedium))
            }
        }
    }
}
