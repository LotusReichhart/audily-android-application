package com.lotusreichhart.audily.feature.playlists.impl.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.common.util.TimeUtils
import com.lotusreichhart.audily.core.designsystem.R
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.shimmer
import com.lotusreichhart.audily.core.model.song.SongsSummary


@Composable
internal fun PlaylistDetailTopBar(
    songsSummary: SongsSummary,
    isLoading: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(
                vertical = LocalDimensions.current.paddingSmall,
                horizontal = LocalDimensions.current.paddingMedium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AudilyIconButton(
                onClick = onBack,
                painter = painterResource(id = AudilyIcons.ArrowLeft),
                contentDescription = "Back",
                containerSize = 24.dp,
                iconSize = 24.dp,
                tint = MaterialTheme.colorScheme.onPrimary
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer(
                            primaryColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            secondaryColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
                        )
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${songsSummary.totalCount} ${stringResource(R.string.core_designsystem_songs)} - ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = TimeUtils.formatDuration(songsSummary.totalDuration),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AudilyIconButton(
                onClick = onEdit,
                painter = painterResource(AudilyIcons.Edit),
                contentDescription = "Playlist Search",
                iconSize = 24.dp,
                containerSize = 24.dp,
                tint = MaterialTheme.colorScheme.onPrimary,
            )

            AudilyIconButton(
                onClick = onDelete,
                painter = painterResource(AudilyIcons.Delete),
                contentDescription = "Playlist Sort",
                iconSize = 24.dp,
                containerSize = 24.dp,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}