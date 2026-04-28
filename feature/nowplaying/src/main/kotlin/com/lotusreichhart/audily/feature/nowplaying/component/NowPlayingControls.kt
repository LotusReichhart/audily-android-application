package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import com.lotusreichhart.audily.feature.nowplaying.resource.NowPlayingIcons

@Composable
internal fun NowPlayingControls(
    isPlaying: Boolean,
    isShuffleOn: Boolean,
    repeatMode: RepeatMode,
    onPlayPauseClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onFastForwardClick: () -> Unit,
    onFastRewindClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Shuffle
        IconButton(onClick = onShuffleClick) {
            Icon(
                painter = painterResource(id = NowPlayingIcons.Shuffle),
                contentDescription = "Shuffle",
                modifier = Modifier.alpha(if (isShuffleOn) 1f else 0.3f)
            )
        }

        // Previous
        IconButton(onClick = onSkipPreviousClick) {
            Icon(
                painter = painterResource(id = NowPlayingIcons.Previous),
                contentDescription = "Previous"
            )
        }

        // Fast Rewind
        IconButton(onClick = onFastRewindClick) {
            Icon(
                painter = painterResource(id = NowPlayingIcons.FastRewind),
                contentDescription = "Fast Rewind"
            )
        }

        // Play/Pause
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { onPlayPauseClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPlaying) NowPlayingIcons.Pause else NowPlayingIcons.Play
                ),
                contentDescription = "Play/Pause",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Fast Forward
        IconButton(onClick = onFastForwardClick) {
            Icon(
                painter = painterResource(id = NowPlayingIcons.FastForward),
                contentDescription = "Fast Forward"
            )
        }

        // Next
        IconButton(onClick = onSkipNextClick) {
            Icon(
                painter = painterResource(id = NowPlayingIcons.Next),
                contentDescription = "Next"
            )
        }

        // Repeat
        IconButton(onClick = onRepeatClick) {
            Icon(
                painter = painterResource(
                    id = when (repeatMode) {
                        RepeatMode.OFF -> NowPlayingIcons.RepeatOff
                        RepeatMode.ONE -> NowPlayingIcons.RepeatOne
                        RepeatMode.ALL -> NowPlayingIcons.RepeatAll
                    }
                ),
                contentDescription = "Repeat"
            )
        }
    }
}

