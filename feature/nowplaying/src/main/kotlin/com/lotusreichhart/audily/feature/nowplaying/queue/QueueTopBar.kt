package com.lotusreichhart.audily.feature.nowplaying.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.common.util.TimeUtils
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.OnSurfaceDark
import com.lotusreichhart.audily.core.model.song.SongsSummary
import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.feature.nowplaying.R

@Composable
internal fun QueueTopBar(
    isExpanded: Boolean = false,
    queueSummary: SongsSummary,
    onCloseClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isExpanded) Color.Transparent else MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(
                vertical = LocalDimensions.current.paddingSmall,
                horizontal = LocalDimensions.current.paddingMedium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingSmall)
        ) {
            if (!isExpanded) {
                AudilyIconButton(
                    onClick = onCloseClick,
                    painter = painterResource(id = AudilyIcons.ArrowDown),
                    contentDescription = "Close",
                    containerSize = 24.dp,
                    iconSize = 24.dp,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingExtraSmall)
            ) {
                Text(
                    text = stringResource(id = R.string.feature_nowplaying_queue_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isExpanded) OnSurfaceDark else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${queueSummary.totalCount} ${stringResource(coreR.string.core_designsystem_songs)} - ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isExpanded) OnSurfaceDark else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = TimeUtils.formatDuration(queueSummary.totalDuration),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isExpanded) OnSurfaceDark.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.9f
                        )
                    )
                }
            }
        }

        AudilyIconButton(
            onClick = onMenuClick,
            painter = painterResource(id = AudilyIcons.VerticalDot),
            contentDescription = "Queue Menu",
            modifier = Modifier.offset(x = 8.dp),
            containerSize = 24.dp,
            iconSize = 24.dp,
            tint = if (isExpanded) OnSurfaceDark else MaterialTheme.colorScheme.onSurface
        )
    }
}