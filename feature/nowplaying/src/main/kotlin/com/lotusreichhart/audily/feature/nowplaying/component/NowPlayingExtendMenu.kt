package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.nowplaying.R
import com.lotusreichhart.audily.feature.nowplaying.resource.NowPlayingIcons


@Composable
internal fun NowPlayingMenu(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    onTimerClick: () -> Unit,
    onSpeedPitchClick: () -> Unit,
    onSkipDurationClick: () -> Unit,
) {
    Column(
        modifier = modifier.padding(bottom = LocalDimensions.current.paddingMedium),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingMedium)
    ) {
        MenuFloatingItem(
            isVisible = isVisible,
            iconId = NowPlayingIcons.TimerPauseOutline,
            label = stringResource(R.string.feature_nowplaying_menu_timber),
            onClick = onTimerClick
        )
        MenuFloatingItem(
            isVisible = isVisible,
            iconId = NowPlayingIcons.Speed,
            label = stringResource(R.string.feature_nowplaying_menu_speed_pitch),
            onClick = onSpeedPitchClick
        )
        MenuFloatingItem(
            isVisible = isVisible,
            iconId = NowPlayingIcons.SkipDuration,
            label = stringResource(R.string.feature_nowplaying_menu_skip_duration),
            onClick = onSkipDurationClick
        )
    }
}

@Composable
private fun MenuFloatingItem(
    isVisible: Boolean,
    iconId: Int,
    label: String,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.shadow(2.dp, RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(LocalDimensions.current.paddingSmall))

            AudilyIconButton(
                onClick = onClick,
                painter = painterResource(id = iconId),
                contentDescription = label,
                containerSize = 40.dp,
                iconSize = 24.dp,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
