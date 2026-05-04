package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.R
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

/**
 * Trạng thái phát nhạc của bài hát để hiển thị hiệu ứng tương ứng.
 */
enum class SongPlaybackStatus {
    NONE,       // Không phải bài đang phát
    PLAYING,    // Đang phát
    PAUSED      // Đang tạm dừng
}

@Composable
fun SongItem(
    title: String,
    artist: String,
    albumArt: @Composable () -> Unit,
    onClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    isMissing: Boolean = false,
    isFavorite: Boolean = false,
    showDragHandle: Boolean = false,
    playbackStatus: SongPlaybackStatus = SongPlaybackStatus.NONE,
) {
    val isSelected = playbackStatus != SongPlaybackStatus.NONE
    val contentAlpha = if (isMissing) 0.5f else if (isSelected) 0.7f else 1f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = LocalDimensions.current.paddingSmall)
            .padding(horizontal = LocalDimensions.current.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showDragHandle) {
            DragHandleIcon()
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .alpha(contentAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SongArtwork(
                playbackStatus = playbackStatus,
                content = albumArt
            )

            Spacer(modifier = Modifier.width(LocalDimensions.current.paddingSmall))

            SongInformation(
                title = title,
                artist = artist,
                isSelected = isSelected,
                isMissing = isMissing,
                isFavorite = isFavorite,
                modifier = Modifier.weight(1f)
            )
        }

        // Section các nút chức năng (Drag Handle hiện tại và Menu Button) giữ độ sáng 1.0f
        SongMenuButton(onClick = onMenuClick)
    }
}

@Composable
private fun DragHandleIcon() {
    Icon(
        painter = painterResource(id = AudilyIcons.DragHandle),
        contentDescription = null,
        modifier = Modifier
            .size(24.dp)
            .padding(end = LocalDimensions.current.paddingExtraSmall),
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SongArtwork(
    playbackStatus: SongPlaybackStatus,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(LocalDimensions.current.cornerRadiusExtraSmall)),
        contentAlignment = Alignment.Center
    ) {
        content()

        if (playbackStatus != SongPlaybackStatus.NONE) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                PlaybackVisualizer(isPlaying = playbackStatus == SongPlaybackStatus.PLAYING)
            }
        }
    }
}

@Composable
private fun SongInformation(
    title: String,
    artist: String,
    isSelected: Boolean,
    isMissing: Boolean,
    isFavorite: Boolean,
    modifier: Modifier = Modifier
) {
    val displayTitle =
        if (isMissing) stringResource(id = R.string.core_designsystem_song_is_missing) else title
    val displayArtist =
        if (isMissing) stringResource(id = R.string.core_designsystem_unknown_artist) else artist

    val titleColor = when {
        isMissing -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingExtraSmall)
    ) {
        Text(
            text = displayTitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = if (isSelected) TextOverflow.Visible else TextOverflow.Ellipsis,
            color = titleColor,
            modifier = if (isSelected) {
                Modifier.basicMarquee(
                    iterations = Int.MAX_VALUE,
                    spacing = MarqueeSpacing.fractionOfContainer(0.5f),
                    repeatDelayMillis = 0
                )
            } else Modifier
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isFavorite) {
                Icon(
                    painter = painterResource(id = AudilyIcons.FavoriteFill),
                    contentDescription = null,
                    modifier = Modifier.size(LocalDimensions.current.iconSizeSmall),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = displayArtist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SongMenuButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            painter = painterResource(id = AudilyIcons.VerticalDot),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .offset(x = 10.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun PlaybackVisualizer(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "Visualizer")

    val bar1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Reverse),
        label = "Bar1"
    )
    val bar2 by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "Bar2"
    )
    val bar3 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(550, easing = LinearEasing), RepeatMode.Reverse),
        label = "Bar3"
    )

    Row(
        modifier = Modifier.size(width = 18.dp, height = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Bar 1 (Left)
        Box(
            modifier = Modifier
                .weight(1.05f)
                .fillMaxHeight(if (isPlaying) bar1 else 0.35f)
                .background(Color.White, RoundedCornerShape(1.dp))
        )
        // Bar 2 (Middle)
        Box(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight(if (isPlaying) bar2 else 0.6f)
                .background(Color.White, RoundedCornerShape(1.dp))
        )
        // Bar 3 (Right)
        Box(
            modifier = Modifier
                .weight(1.05f)
                .fillMaxHeight(if (isPlaying) bar3 else 0.45f)
                .background(Color.White, RoundedCornerShape(1.dp))
        )
    }
}
