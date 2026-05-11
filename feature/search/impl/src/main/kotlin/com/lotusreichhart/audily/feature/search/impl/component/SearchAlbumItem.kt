package com.lotusreichhart.audily.feature.search.impl.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.album.Album

@Composable
internal fun SearchAlbumItem(
    album: Album,
    onClick: (Album) -> Unit,
    modifier: Modifier = Modifier,
    isVertical: Boolean = true
) {
    val dimensions = LocalDimensions.current

    if (isVertical) {
        Column(
            modifier = modifier
                .width(80.dp)
                .clickable(
                    onClick = { onClick(album) },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AudilyArtwork(
                artworkUri = album.albumArtUri,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                isAspectRatio = true
            )

            Spacer(modifier = Modifier.padding(top = dimensions.paddingSmall))

            Text(
                text = album.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick(album) }
                .padding(vertical = dimensions.paddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AudilyArtwork(
                artworkUri = album.albumArtUri,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                isAspectRatio = true
            )

            Spacer(modifier = Modifier.width(dimensions.paddingMedium))

            Column {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
