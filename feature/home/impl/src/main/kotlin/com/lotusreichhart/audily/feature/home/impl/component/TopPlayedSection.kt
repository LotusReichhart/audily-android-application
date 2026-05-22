package com.lotusreichhart.audily.feature.home.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.home.impl.R

@Composable
internal fun TopPlayedSection(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    onSongLongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.feature_home_impl_section_top_played),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = LocalDimensions.current.paddingMedium),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(LocalDimensions.current.paddingSmall))

        LazyRow(
            contentPadding = PaddingValues(horizontal = LocalDimensions.current.paddingMedium),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingMedium)
        ) {
            items(songs, key = { it.id }) { song ->
                HomeSongCard(
                    modifier = Modifier.width(135.dp),
                    title = song.basic.title,
                    artist = song.basic.artist,
                    isMissing = song.isMissing,
                    albumArt = {
                        AudilyArtwork(
                            artworkUri = song.basic.artworkUri,
                            modifier = Modifier
                                .size(135.dp)
                                .clip(RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium))
                        )
                    },
                    onClick = { onSongClick(song) },
                    onLongClick = { onSongLongClick(song) }
                )
            }
        }
    }
}