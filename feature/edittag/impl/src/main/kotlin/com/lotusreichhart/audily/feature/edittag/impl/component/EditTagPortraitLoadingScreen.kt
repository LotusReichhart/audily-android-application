package com.lotusreichhart.audily.feature.edittag.impl.component

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.util.shimmer

@Composable
internal fun EditTagPortraitLoadingScreen() {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimensions.paddingMedium)
            .padding(bottom = dimensions.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // Artwork Shimmer (simulating the 200.dp cover)
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .shimmer()
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Form Fields Column Shimmer
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
        ) {
            // Title
            ShimmerField(labelWidth = 80.dp)

            // Artist
            ShimmerField(labelWidth = 100.dp)

            // Album & Year (Side-by-Side)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
            ) {
                Box(modifier = Modifier.weight(0.65f)) {
                    ShimmerField(labelWidth = 70.dp)
                }
                Box(modifier = Modifier.weight(0.35f)) {
                    ShimmerField(labelWidth = 50.dp)
                }
            }

            // Track Number & Genre (Side-by-Side)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
            ) {
                Box(modifier = Modifier.weight(0.35f)) {
                    ShimmerField(labelWidth = 90.dp)
                }
                Box(modifier = Modifier.weight(0.65f)) {
                    ShimmerField(labelWidth = 80.dp)
                }
            }

            // Composer
            ShimmerField(labelWidth = 120.dp)
        }
    }
}

@Composable
private fun ShimmerField(
    labelWidth: Dp
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .width(labelWidth)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmer()
        )
    }
}