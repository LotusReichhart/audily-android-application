package com.lotusreichhart.audily.feature.albums.impl.detail.component

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.shimmer

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
internal fun PortraitAlbumDetailLoadingScreen() {
    val dimensions = LocalDimensions.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val headerHeight = if (isLandscape) screenHeight / 2 else screenWidth

    Column(modifier = Modifier.fillMaxSize()) {
        // Giant header shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .shimmer()
        )

        // Control buttons shimmer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium),
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
