package com.lotusreichhart.audily.feature.songs.impl.picker.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.component.SongItem
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.song.Song

@Composable
internal fun SongPickerItem(
    modifier: Modifier = Modifier,
    song: Song,
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
        SongItem(
            modifier = Modifier
                .weight(1f)
                .offset(x = -dimensions.paddingMedium),
            title = song.basic.title,
            artist = song.basic.artist,
            albumArt = {
                AudilyArtwork(
                    artworkUri = song.basic.artworkUri,
                    modifier = Modifier.fillMaxSize()
                )
            },
            onClick = onSelectedChange,
            onMenuClick = {},
            showMenu = false
        )
    }
}