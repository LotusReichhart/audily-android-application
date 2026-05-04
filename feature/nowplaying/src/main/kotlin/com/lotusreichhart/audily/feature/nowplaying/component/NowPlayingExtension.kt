package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.feature.nowplaying.resource.NowPlayingIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.OnSurfaceDark

@Composable
internal fun NowPlayingExtension(
    modifier: Modifier = Modifier,
    isLyricsVisible: Boolean,
    isMenuVisible: Boolean,
    onQueueClick: () -> Unit,
    onLyricsClick: () -> Unit,
    onExtendClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AudilyIconButton(
                onClick = onQueueClick,
                painter = painterResource(id = AudilyIcons.QueueMusic),
                contentDescription = "Queue",
                containerSize = 28.dp,
                iconSize = 28.dp,
                tint = OnSurfaceDark
            )
            Spacer(modifier = Modifier.width(LocalDimensions.current.paddingSmall))
            AudilyIconButton(
                onClick = onLyricsClick,
                painter = painterResource(
                    id = if (isLyricsVisible) NowPlayingIcons.LyricsFill else NowPlayingIcons.LyricsOutline
                ),
                contentDescription = "Toggle Lyrics",
                containerSize = 28.dp,
                iconSize = 24.dp,
                tint = OnSurfaceDark
            )
        }

        AudilyIconButton(
            onClick = onExtendClick,
            painter = painterResource(
                id = if (isMenuVisible) NowPlayingIcons.ExtendOn else NowPlayingIcons.ExtendOff
            ),
            contentDescription = "Extend Menu",
            containerSize = 24.dp,
            iconSize = 24.dp,
            tint = OnSurfaceDark
        )
    }
}
