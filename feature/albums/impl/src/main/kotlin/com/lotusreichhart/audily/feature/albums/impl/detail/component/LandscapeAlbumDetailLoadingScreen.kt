package com.lotusreichhart.audily.feature.albums.impl.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.zIndex
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.shimmer

@Composable
internal fun LandscapeAlbumDetailLoadingScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    AudilyScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AlbumDetailTopBar(
                title = "",
                collapseFraction = 0f,
                onBackClick = onBack,
                onMenuClick = {},
                isLandscape = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(2f)
            )
        },
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trái: Ảnh bìa Album shimmer
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .padding(all = dimensions.paddingMedium)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmer()
                )
            }

            // Phải: Điều khiển & Danh sách bài hát shimmer
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false
                ) {
                    item {
                        Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                    }

                    // Play & Shuffle buttons shimmer
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimensions.paddingMedium)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                            Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                        }
                    }

                    // Section Title "Songs" shimmer
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimensions.paddingMedium)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmer()
                            )
                            Spacer(modifier = Modifier.height(dimensions.paddingSmall))
                        }
                    }

                    // Songs list shimmer
                    items(10) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimensions.paddingMedium, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: Duration shimmer
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmer()
                            )

                            Spacer(modifier = Modifier.width(dimensions.paddingMedium))

                            // Middle: Title & Artist shimmer
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

                            // Right: Menu button shimmer
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
    }
}