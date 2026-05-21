package com.lotusreichhart.audily.feature.playlists.impl.picker.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.shimmer

@Composable
internal fun PlaylistsPickerItemShimmer(
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.paddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox Shimmer
        Box(
            modifier = Modifier
                .padding(start = dimensions.paddingMedium)
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Artwork Shimmer
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(dimensions.cornerRadiusExtraSmall))
                .shimmer()
        )

        Spacer(modifier = Modifier.width(dimensions.paddingSmall))

        // Information Shimmer
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(dimensions.paddingExtraSmall)
        ) {
            // Title Shimmer
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
            )

            // Artist Shimmer
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
            )
        }
    }
}