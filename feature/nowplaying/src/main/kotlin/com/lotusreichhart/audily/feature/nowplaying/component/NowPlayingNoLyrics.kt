package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.OnSurfaceDark
import com.lotusreichhart.audily.feature.nowplaying.R
import com.lotusreichhart.audily.feature.nowplaying.resource.NowPlayingIcons

@Composable
internal fun NowPlayingNoLyrics(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(LocalDimensions.current.iconSizeLarge),
            painter =
                painterResource(NowPlayingIcons.LyricsFill),
            contentDescription = "No lyrics",
            tint = OnSurfaceDark.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.paddingSmall))
        Text(
            text = stringResource(R.string.feature_nowplaying_no_lyrics),
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurfaceDark.copy(
                alpha = 0.8f
            )
        )
    }
}