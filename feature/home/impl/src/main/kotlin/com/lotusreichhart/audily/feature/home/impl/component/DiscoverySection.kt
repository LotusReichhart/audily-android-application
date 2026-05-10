package com.lotusreichhart.audily.feature.home.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.home.impl.R

@Composable
internal fun DiscoverySection(
    songs: List<Song>,
    onSongClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.feature_home_impl_section_discovery),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = LocalDimensions.current.paddingMedium),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(LocalDimensions.current.paddingSmall))

        val dimensions = LocalDimensions.current

        songs.chunked(2).forEachIndexed { index, rowSongs ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
            ) {
                rowSongs.forEach { song ->
                    DiscoverySongCard(
                        modifier = Modifier.weight(1f),
                        title = song.basic.title,
                        artist = song.basic.artist,
                        albumArt = {
                            AudilyArtwork(
                                artworkUri = song.basic.artworkUri,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
                            )
                        },
                        onClick = { onSongClick(song.id) }
                    )
                }

                if (rowSongs.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            if (index < (songs.size + 1) / 2 - 1) {
                Spacer(modifier = Modifier.height(dimensions.paddingSmall))
            }
        }
    }
}

@Composable
private fun DiscoverySongCard(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    albumArt: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(all = LocalDimensions.current.paddingSmall)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingSmall)
    ) {
        albumArt()

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(LocalDimensions.current.paddingExtraSmall))
            Text(
                text = artist,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}