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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingLarge),
        userScrollEnabled = false
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

        item {
            SectionShimmer(titleWidth = 150.dp) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = LocalDimensions.current.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingMedium)
                ) {
                    items(5) {
                        HomeSongCardShimmer(size = 135.dp)
                    }
                }
            }
        }

        item {
            SectionShimmer(titleWidth = 120.dp) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = LocalDimensions.current.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingMedium)
                ) {
                    items(5) {
                        HomeSongCardShimmer(size = 80.dp)
                    }
                }
            }
        }

        item {
            DiscoverySectionShimmer()
        }

        item {
            SectionShimmer(titleWidth = 140.dp) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = LocalDimensions.current.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingMedium)
                ) {
                    items(3) {
                        Box(
                            modifier = Modifier
                                .width(220.dp)
                                .height(150.dp)
                                .clip(RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium))
                                .shimmer()
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(LocalDimensions.current.paddingMedium))
        }
    }
}

@Composable
private fun GreetingShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmer()
        )

        Spacer(modifier = Modifier.height(LocalDimensions.current.paddingMedium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingMedium)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium))
                    .shimmer()
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium))
                    .shimmer()
            )
        }
    }
}

@Composable
private fun SectionShimmer(
    titleWidth: Dp,
    content: @Composable () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .padding(horizontal = LocalDimensions.current.paddingMedium)
                .width(titleWidth)
                .height(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )

        Spacer(modifier = Modifier.height(LocalDimensions.current.paddingSmall))

        content()
    }
}

@Composable
private fun DiscoverySectionShimmer() {
    val dimensions = LocalDimensions.current
    Column {
        Box(
            modifier = Modifier
                .padding(horizontal = dimensions.paddingMedium)
                .width(180.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )

        Spacer(modifier = Modifier.height(dimensions.paddingSmall))

        repeat(2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
            ) {
                repeat(2) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(76.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmer(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nội dung đã được shimmer() bọc ngoài nên không cần chi tiết bên trong
                    }
                }
            }
            Spacer(modifier = Modifier.height(dimensions.paddingSmall))
        }
    }
}

@Composable
private fun HomeSongCardShimmer(size: Dp) {
    Column(modifier = Modifier.width(size)) {
        Box(
            modifier = Modifier
                .size(size)
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

@Preview
@Composable
private fun PreviewHomeShimmer(){
    HomeContentShimmer()
}
