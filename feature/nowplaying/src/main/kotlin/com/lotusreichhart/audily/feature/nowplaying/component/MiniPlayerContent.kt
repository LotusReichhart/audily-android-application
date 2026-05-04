package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.feature.nowplaying.resource.NowPlayingIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.OnSurfaceDark
import com.lotusreichhart.audily.core.designsystem.theme.SurfaceDark

@Composable
internal fun MiniPlayerContent(
    modifier: Modifier = Modifier,
    backgroundBrush: Brush? = null,
    title: String,
    artist: String,
    artworkUri: String?,
    isPlaying: Boolean,
    progress: Float,
    onResumePauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onQueueClick: () -> Unit,
    onClick: () -> Unit,
    hasNext: Boolean = true
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier.then(
                if (backgroundBrush != null) Modifier.background(backgroundBrush)
                else Modifier.background(SurfaceDark)
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = LocalDimensions.current.paddingMedium,
                        vertical = LocalDimensions.current.paddingSmall
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AudilyArtwork(
                    artworkUri = artworkUri,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(LocalDimensions.current.cornerRadiusSmall))
                )
                Spacer(modifier = Modifier.width(LocalDimensions.current.paddingSmall))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.basicMarquee(
                            iterations = Int.MAX_VALUE,
                            spacing = MarqueeSpacing.fractionOfContainer(0.5f),
                            repeatDelayMillis = 0
                        ),
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceDark,
                        maxLines = 1
                    )
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceDark.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(LocalDimensions.current.paddingExtraSmall))
                AudilyIconButton(
                    onClick = onQueueClick,
                    painter = painterResource(id = AudilyIcons.QueueMusic),
                    contentDescription = "Queue",
                    tint = OnSurfaceDark
                )

                AudilyIconButton(
                    onClick = { if (hasNext) onNextClick() },
                    painter = painterResource(id = NowPlayingIcons.Next),
                    contentDescription = "Next",
                    modifier = Modifier.alpha(if (hasNext) 1f else 0.5f),
                    tint = OnSurfaceDark
                )

                // Progress Circle Play/Pause Button
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = OnSurfaceDark.copy(alpha = 0.6f)
                    )
                    AudilyIconButton(
                        onClick = onResumePauseClick,
                        painter = painterResource(
                            id = if (isPlaying) NowPlayingIcons.Pause else NowPlayingIcons.Resume
                        ),
                        contentDescription = "Play/Pause",
                        iconSize = 24.dp,
                        containerSize = 40.dp,
                        tint = OnSurfaceDark
                    )
                }
            }
        }
    }
}
