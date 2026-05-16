package com.lotusreichhart.audily.feature.playlists.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.shimmer

@Composable
internal fun PlaylistsLoadingScreen(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall),
        userScrollEnabled = false
    ) {
        item {
            PlaylistItemShimmer(
                modifier = Modifier
                    .padding(top = dimensions.paddingMedium)
                    .padding(bottom = dimensions.paddingSmall)
            )
        }

        item {
            PlaylistItemShimmer(
                modifier = Modifier.padding(bottom = dimensions.paddingSmall)
            )
        }

        items(15) {
            PlaylistItemShimmer()
        }
    }
}

@Composable
private fun PlaylistItemShimmer(
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    // Card Container Shimmer
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium)
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artwork Shimmer (56dp for Playlist)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
                    .shimmer()
            )

            Spacer(modifier = Modifier.width(LocalDimensions.current.paddingSmall))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensions.paddingExtraSmall)
            ) {
                // Name Shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )

                // Song count Shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )
            }
        }
    }
}