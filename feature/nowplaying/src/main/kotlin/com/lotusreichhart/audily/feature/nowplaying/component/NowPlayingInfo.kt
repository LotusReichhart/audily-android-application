package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.OnSurfaceDark

@Composable
internal fun NowPlayingInfo(
    title: String,
    artist: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                modifier = Modifier.basicMarquee(
                    iterations = Int.MAX_VALUE,
                    spacing = MarqueeSpacing.fractionOfContainer(0.5f),
                    repeatDelayMillis = 0
                ),
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = OnSurfaceDark,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Visible,
            )
            Spacer(modifier = Modifier.width(LocalDimensions.current.paddingExtraSmall))
            Text(
                text = artist,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceDark.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(LocalDimensions.current.paddingMedium))
        AudilyIconButton(
            onClick = onFavoriteClick,
            painter = painterResource(
                id = if (isFavorite) AudilyIcons.FavoriteFill else AudilyIcons.FavoriteOutline
            ),
            contentDescription = "Favorite",
            containerSize = 32.dp,
            iconSize = 24.dp,
            tint = if (isFavorite) Color.Red else OnSurfaceDark
        )
    }
}
