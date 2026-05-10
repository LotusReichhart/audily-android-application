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

import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue

@Composable
internal fun NowPlayingExtension(
    modifier: Modifier = Modifier,
    isLyricsVisible: Boolean,
    sleepTimerActive: Boolean,
    onQueueClick: () -> Unit,
    onLyricsClick: () -> Unit,
    onTimerClick: () -> Unit,
    onExtendClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "timerFlashing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "timerAlpha"
    )

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
            Spacer(modifier = Modifier.width(12.dp))
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
            
            if (sleepTimerActive) {
                Spacer(modifier = Modifier.width(12.dp))
                AudilyIconButton(
                    onClick = onTimerClick,
                    painter = painterResource(id = NowPlayingIcons.TimerPauseFill),
                    contentDescription = "Sleep Timer",
                    containerSize = 28.dp,
                    iconSize = 24.dp,
                    tint = OnSurfaceDark.copy(alpha = alpha)
                )
            }
        }

        AudilyIconButton(
            onClick = onExtendClick,
            painter = painterResource(
                id = NowPlayingIcons.Extend
            ),
            contentDescription = "Extend Menu",
            containerSize = 24.dp,
            iconSize = 24.dp,
            tint = OnSurfaceDark
        )
    }
}
