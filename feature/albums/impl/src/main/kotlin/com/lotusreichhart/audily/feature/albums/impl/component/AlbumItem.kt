package com.lotusreichhart.audily.feature.albums.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.album.Album
import com.lotusreichhart.audily.feature.albums.impl.R

@Composable
internal fun AlbumItem(
    modifier: Modifier = Modifier,
    album: Album,
    gridSize: Int,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current
    val interactionSource = remember { MutableInteractionSource() }

    if (gridSize == 1) {
        // List Mode Layout
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = interactionSource
                )
                .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AudilyArtwork(
                artworkUri = album.albumArtUri,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
            )

            Spacer(modifier = Modifier.width(dimensions.paddingSmall))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(
                        id = R.string.feature_albums_impl_songs_count,
                        album.songCount
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    } else {
        // Grid Mode Layout
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = interactionSource
                )
                .padding(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                AudilyArtwork(
                    artworkUri = album.albumArtUri,
                    modifier = Modifier.fillMaxSize(),
                    isAspectRatio = false
                )
            }

            Spacer(modifier = Modifier.height(dimensions.paddingSmall))

            val titleStyle = when (gridSize) {
                2 -> MaterialTheme.typography.bodyLarge
                3 -> MaterialTheme.typography.bodyMedium
                else -> MaterialTheme.typography.bodySmall
            }

            val subtitleStyle = when (gridSize) {
                2 -> MaterialTheme.typography.bodyMedium
                3 -> MaterialTheme.typography.bodySmall
                else -> MaterialTheme.typography.labelSmall
            }

            Text(
                text = album.title,
                style = titleStyle,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = album.artist,
                style = subtitleStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = stringResource(
                    id = R.string.feature_albums_impl_songs_count,
                    album.songCount
                ),
                style = subtitleStyle.copy(fontWeight = FontWeight.Light),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
