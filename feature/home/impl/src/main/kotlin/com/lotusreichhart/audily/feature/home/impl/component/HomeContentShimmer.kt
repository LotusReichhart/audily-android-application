package com.lotusreichhart.audily.feature.home.impl.component

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.shimmer

@Composable
internal fun HomeContentShimmer(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingLarge)
    ) {
        item {
            GreetingShimmer(
                modifier = Modifier.padding(
                    top = LocalDimensions.current.paddingMedium,
                    start = LocalDimensions.current.paddingMedium,
                    end = LocalDimensions.current.paddingMedium
                )
            )
        }

        repeat(3) {
            item {
                HorizontalSectionShimmer()
            }
        }
    }
}

@Composable
private fun GreetingShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmer()
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmer()
            )
        }

        Spacer(modifier = Modifier.height(LocalDimensions.current.paddingMedium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingMedium)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium))
                    .shimmer()
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium))
                    .shimmer()
            )
        }
    }
}

@Composable
private fun HorizontalSectionShimmer() {
    Column {
        Box(
            modifier = Modifier
                .padding(horizontal = LocalDimensions.current.paddingMedium)
                .width(250.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )

        Spacer(modifier = Modifier.height(LocalDimensions.current.paddingSmall))

        LazyRow(
            contentPadding = PaddingValues(horizontal = LocalDimensions.current.paddingMedium),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingMedium)
        ) {
            items(5) {
                HomeSongCardShimmer()
            }
        }
    }
}

@Composable
private fun HomeSongCardShimmer() {
    Column(modifier = Modifier.width(80.dp)) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium))
                .shimmer()
        )

        Spacer(modifier = Modifier.height(LocalDimensions.current.paddingSmall))

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
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )
    }
}
