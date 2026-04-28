package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.feature.nowplaying.resource.NowPlayingIcons

@Composable
internal fun NowPlayingExtension(
    isLyricsVisible: Boolean,
    onQueueClick: () -> Unit,
    onLyricsClick: () -> Unit,
    onExtendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onQueueClick) {
                Icon(
                    painter = painterResource(id = AudilyIcons.QueueMusic),
                    contentDescription = "Queue"
                )
            }
            IconButton(onClick = onLyricsClick) {
                Icon(
                    painter = painterResource(
                        id = if (isLyricsVisible) NowPlayingIcons.LyricsFill else NowPlayingIcons.LyricsOutline
                    ),
                    contentDescription = "Toggle Lyrics"
                )
            }
        }
        
        IconButton(onClick = onExtendClick) {
            Icon(
                painter = painterResource(id = NowPlayingIcons.Extend),
                contentDescription = "Extend Menu"
            )
        }
    }
}
