package com.lotusreichhart.audily.feature.songs.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.lotusreichhart.audily.core.designsystem.R
import com.lotusreichhart.audily.core.designsystem.component.AudilySortButton
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

/**
 * Header hiển thị thông tin tóm tắt danh sách bài hát và nút sắp xếp.
 */
@Composable
internal fun SongsHeader(
    songCount: Int,
    totalDuration: String,
    sortText: String,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(horizontal = LocalDimensions.current.paddingMedium)
            .padding(bottom = LocalDimensions.current.paddingSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingExtraSmall),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "$songCount ${stringResource(R.string.core_designsystem_songs)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingExtraSmall)
            ) {
                Icon(
                    painter = painterResource(id = AudilyIcons.Timer),
                    contentDescription = "Timer",
                    modifier = Modifier.size(LocalDimensions.current.iconSizeSmall),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = totalDuration,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AudilySortButton(
            text = sortText,
            onClick = onSortClick
        )
    }
}
