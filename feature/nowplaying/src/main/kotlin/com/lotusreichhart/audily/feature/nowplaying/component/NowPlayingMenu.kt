package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.feature.nowplaying.resource.NowPlayingIcons

@Composable
internal fun NowPlayingMenu(
    onTimerClick: () -> Unit,
    onSpeedClick: () -> Unit,
    onJumpClick: () -> Unit,
    onRingtoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuItem(
            iconId = NowPlayingIcons.TimerPauseOutline,
            label = "Timer",
            onClick = onTimerClick
        )
        MenuItem(
            iconId = NowPlayingIcons.Speed,
            label = "Speed",
            onClick = onSpeedClick
        )
        MenuItem(
            iconId = NowPlayingIcons.JumpInterval,
            label = "Jump",
            onClick = onJumpClick
        )
        MenuItem(
            iconId = NowPlayingIcons.SetRingtone,
            label = "Ringtone",
            onClick = onRingtoneClick
        )
    }
}

@Composable
private fun MenuItem(
    iconId: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
