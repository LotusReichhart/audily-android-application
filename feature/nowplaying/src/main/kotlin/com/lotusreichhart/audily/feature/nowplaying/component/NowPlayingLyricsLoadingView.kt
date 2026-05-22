package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.OnSurfaceDark
import com.lotusreichhart.audily.core.designsystem.util.shimmer

@Composable
internal fun NowPlayingLyricsLoadingView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.padding(top = LocalDimensions.current.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer(
                        primaryColor = OnSurfaceDark.copy(alpha = 0.4f),
                        secondaryColor = OnSurfaceDark.copy(alpha = 0.2f),
                    )
            )
        }
    }
}
