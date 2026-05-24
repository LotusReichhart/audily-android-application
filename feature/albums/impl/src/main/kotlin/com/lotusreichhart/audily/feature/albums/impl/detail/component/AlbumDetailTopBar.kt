package com.lotusreichhart.audily.feature.albums.impl.detail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.lerp
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

@Composable
internal fun AlbumDetailTopBar(
    modifier: Modifier = Modifier,
    title: String,
    collapseFraction: Float,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    isLandscape: Boolean = false
) {
    val backgroundColor = if (isLandscape) {
        MaterialTheme.colorScheme.primary
    } else {
        lerp(
            start = Color.Transparent,
            stop = MaterialTheme.colorScheme.primary,
            fraction = collapseFraction
        )
    }

    val contentColor = if (isLandscape) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        lerp(
            start = Color.White,
            stop = MaterialTheme.colorScheme.onPrimary,
            fraction = collapseFraction
        )
    }

    val containerSize = if (isLandscape) {
        24.dp
    } else {
        lerp(
            start = 48.dp,
            stop = 24.dp,
            fraction = collapseFraction
        )
    }

    val buttonBackgroundColor = if (isLandscape) {
        Color.Transparent
    } else {
        lerp(
            start = Color.Black.copy(alpha = 0.2f),
            stop = Color.Transparent,
            fraction = collapseFraction
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
            .padding(
                vertical = LocalDimensions.current.paddingSmall,
                horizontal = LocalDimensions.current.paddingMedium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AudilyIconButton(
            onClick = onBackClick,
            painter = painterResource(id = AudilyIcons.ArrowLeft),
            contentDescription = "Back",
            containerSize = containerSize,
            iconSize = 24.dp,
            backgroundColor = buttonBackgroundColor,
            tint = contentColor
        )

        Spacer(Modifier.width(12.dp))

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLandscape) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                AnimatedVisibility(
                    visible = collapseFraction > 0.6f,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        AudilyIconButton(
            onClick = onMenuClick,
            painter = painterResource(id = AudilyIcons.VerticalDot),
            contentDescription = "Menu",
            modifier = Modifier.offset(x = 8.dp),
            containerSize = containerSize,
            iconSize = 24.dp,
            backgroundColor = buttonBackgroundColor,
            tint = contentColor
        )
    }
}
