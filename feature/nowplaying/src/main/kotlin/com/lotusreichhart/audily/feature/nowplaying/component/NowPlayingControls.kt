package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.theme.OnSurfaceDark
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import com.lotusreichhart.audily.feature.nowplaying.resource.NowPlayingIcons

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer

@Composable
internal fun NowPlayingControls(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isShuffleOn: Boolean,
    repeatMode: RepeatMode,
    onResumePauseClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onFastForwardClick: () -> Unit,
    onFastRewindClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    hasNext: Boolean = true,
    hasPrevious: Boolean = true
) {
    var repeatRotation by remember { mutableFloatStateOf(0f) }
    val animatedRepeatRotation by animateFloatAsState(
        targetValue = repeatRotation,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "repeatRotation"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Shuffle
        AudilyIconButton(
            onClick = onShuffleClick,
            painter = painterResource(id = NowPlayingIcons.Shuffle),
            contentDescription = "Shuffle",
            containerSize = 28.dp,
            iconSize = 24.dp,
            modifier = Modifier.alpha(if (isShuffleOn) 1f else 0.5f),
            tint = OnSurfaceDark
        )
        // Previous
        AudilyIconButton(
            onClick = { if (hasPrevious) onSkipPreviousClick() },
            painter = painterResource(id = NowPlayingIcons.Previous),
            contentDescription = "Previous",
            containerSize = 28.dp,
            iconSize = 24.dp,
            modifier = Modifier.alpha(if (hasPrevious) 1f else 0.5f),
            tint = OnSurfaceDark
        )
        // Fast Rewind
        AudilyIconButton(
            onClick = onFastRewindClick,
            painter = painterResource(id = NowPlayingIcons.FastRewind),
            contentDescription = "Fast Rewind",
            containerSize = 34.dp,
            iconSize = 28.dp,
            tint = OnSurfaceDark
        )
        // Play/Pause
        AudilyIconButton(
            onClick = onResumePauseClick,
            painter = painterResource(
                id = if (isPlaying) NowPlayingIcons.PauseCircle else NowPlayingIcons.ResumeCircle
            ),
            contentDescription = "Play/Pause",
            iconSize = 60.dp,
            containerSize = 60.dp,
            tint = OnSurfaceDark
        )
        // Fast Forward
        AudilyIconButton(
            onClick = onFastForwardClick,
            painter = painterResource(id = NowPlayingIcons.FastForward),
            contentDescription = "Fast Forward",
            containerSize = 34.dp,
            iconSize = 28.dp,
            tint = OnSurfaceDark
        )
        // Next
        AudilyIconButton(
            onClick = { if (hasNext) onSkipNextClick() },
            painter = painterResource(id = NowPlayingIcons.Next),
            contentDescription = "Next",
            containerSize = 28.dp,
            iconSize = 24.dp,
            modifier = Modifier.alpha(if (hasNext) 1f else 0.5f),
            tint = OnSurfaceDark
        )
        // Repeat
        AudilyIconButton(
            onClick = {
                repeatRotation += 360f
                onRepeatClick()
            },
            painter = painterResource(
                id = when (repeatMode) {
                    RepeatMode.OFF -> NowPlayingIcons.RepeatAll
                    RepeatMode.ONE -> NowPlayingIcons.RepeatOne
                    RepeatMode.ALL -> NowPlayingIcons.RepeatAll
                }
            ),
            contentDescription = "Repeat",
            containerSize = 28.dp,
            iconSize = 24.dp,
            modifier = Modifier
                .alpha(
                    when (repeatMode) {
                        RepeatMode.OFF -> 0.5f
                        RepeatMode.ONE -> 1f
                        RepeatMode.ALL -> 1f
                    }
                )
                .graphicsLayer { rotationZ = animatedRepeatRotation },
            tint = OnSurfaceDark
        )
    }
}

