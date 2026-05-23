package com.lotusreichhart.audily.feature.albums.impl.detail.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.component.AudilyButton
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.component.SongItem
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.getSongPlaybackStatus
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.albums.impl.R
import com.lotusreichhart.audily.feature.albums.impl.detail.AlbumDetailUiEvent
import com.lotusreichhart.audily.feature.albums.impl.detail.AlbumDetailUiState
import com.lotusreichhart.audily.feature.albums.impl.detail.component.AlbumDetailTopBar

@Composable
internal fun LandscapeAlbumDetailLayout(
    modifier: Modifier = Modifier,
    uiState: AlbumDetailUiState,
    onBack: () -> Unit,
    onMenuClick:()-> Unit,
    onEvent: (AlbumDetailUiEvent) -> Unit,
    onSongMenuClick: (Song) -> Unit,
) {
    val dimensions = LocalDimensions.current

    AudilyScaffold(
        modifier = modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AlbumDetailTopBar(
                title = uiState.album?.title ?: "",
                collapseFraction = 0f,
                onBackClick = onBack,
                onMenuClick = onMenuClick,
                isLandscape = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(2f)
            )
        },
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trái: Ảnh bìa Album
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .padding(all = dimensions.paddingMedium)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                val album = uiState.album
                if (album != null) {
                    AudilyArtwork(
                        artworkUri = album.albumArtUri,
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        isAspectRatio = false,
                        contentDescription = album.title
                    )
                }
            }

            // Phải: Điều khiển & Danh sách bài hát
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.background
                                )
                                .padding(horizontal = dimensions.paddingMedium)
                        ) {
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
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = com.lotusreichhart.audily.core.common.util.TimeUtils.formatDuration(
                                                song.basic.duration
                                            ),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
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
    }
}