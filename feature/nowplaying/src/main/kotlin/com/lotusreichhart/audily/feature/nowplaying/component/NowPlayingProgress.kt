package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lotusreichhart.audily.core.designsystem.util.formatDuration

@Composable
internal fun NowPlayingProgress(
    progressMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = if (durationMs > 0) progressMs.toFloat() / durationMs else 0f,
            onValueChange = { onSeek((it * durationMs).toLong()) },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = progressMs.formatDuration(),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = durationMs.formatDuration(),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
