package com.lotusreichhart.audily.feature.albums.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
internal fun AlbumsLoadingScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    gridSize: Int = 2
) {
    val dimensions = LocalDimensions.current

    if (gridSize == 1) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall),
            userScrollEnabled = false
        ) {
            item {
                AlbumListShimmer(
                    modifier = Modifier
                        .padding(top = dimensions.paddingMedium)
                        .padding(bottom = dimensions.paddingExtraSmall)
                )
            }
            items(12) {
                AlbumListShimmer()
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridSize),
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = dimensions.paddingMedium),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + dimensions.paddingMedium,
                bottom = innerPadding.calculateBottomPadding() + dimensions.paddingMedium
            ),
            horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall),
            verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall),
            userScrollEnabled = false
        ) {
            items(12) {
                AlbumGridShimmer()
            }
        }
    }
}

@Composable
private fun AlbumListShimmer(
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium)
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f))
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
                    .shimmer()
            )

            Spacer(modifier = Modifier.width(dimensions.paddingSmall))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensions.paddingExtraSmall)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.2f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )
            }
        }
    }
}

@Composable
private fun AlbumGridShimmer(
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                .shimmer()
        )

        Spacer(modifier = Modifier.height(dimensions.paddingSmall))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )
    }
}
