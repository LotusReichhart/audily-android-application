package com.lotusreichhart.audily.feature.playlists.impl.picker.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
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
import com.lotusreichhart.audily.core.designsystem.R
import com.lotusreichhart.audily.core.designsystem.component.PlaylistGridCover
import com.lotusreichhart.audily.core.designsystem.component.PlaylistItem
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.playlist.Playlist

@Composable
internal fun PlaylistPickerItem(
    modifier: Modifier = Modifier,
    playlist: Playlist,
    isSelected: Boolean,
    onSelectedChange: () -> Unit
) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            modifier = Modifier.padding(start = 2.dp),
            checked = isSelected,
            onCheckedChange = { onSelectedChange() },
        )

        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onSelectedChange,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                .padding(vertical = LocalDimensions.current.paddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlaylistGridCover(
                artworkUris = playlist.artworkUris,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
            )

            Spacer(modifier = Modifier.width(dimensions.paddingSmall))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${playlist.songCount} ${stringResource(R.string.core_designsystem_songs)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}