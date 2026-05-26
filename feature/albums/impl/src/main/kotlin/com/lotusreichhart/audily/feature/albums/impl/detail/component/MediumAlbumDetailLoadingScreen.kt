package com.lotusreichhart.audily.feature.albums.impl.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.shimmer

/**
 * Màn hình shimmer loading cho Album Detail ở chế độ Medium.
 * Tương thích cấu trúc của [MediumAlbumDetailLayout].
 */
@Composable
internal fun MediumAlbumDetailLoadingScreen(
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(modifier = modifier.fillMaxSize()) {
        // TopBar Shimmer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    vertical = dimensions.paddingSmall,
                    horizontal = dimensions.paddingMedium
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
            )
        }

        // Header Info Shimmer (Row: Artwork + Text Column)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.paddingMedium)
                .padding(top = dimensions.paddingMedium, bottom = dimensions.paddingSmall),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .shimmer()
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(dimensions.paddingExtraSmall)
            ) {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer()
                )
            }
        }

        // Sticky Actions Shimmer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingSmall),
            horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(dimensions.buttonHeight)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .shimmer()
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(dimensions.buttonHeight)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .shimmer()
            )
        }

        // List Shimmer
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) {
            items(10) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensions.paddingMedium, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
                            .shimmer()
                    )

                    Spacer(modifier = Modifier.width(dimensions.paddingMedium))

                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmer()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmer()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmer()
                    )
                }
            }
        }
    }
}
