package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.OnSurfaceDark
import com.lotusreichhart.audily.core.designsystem.util.formatDuration
import kotlin.math.abs

@Composable
internal fun NowPlayingProgress(
    modifier: Modifier = Modifier,
    progressMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    songId: Long? = null
) {
    val safeDuration = durationMs.coerceAtLeast(0L)

    // Trạng thái tạm thời khi đang kéo (Reset khi chuyển bài)
    var draggingProgress by remember { mutableStateOf<Float?>(null) }

    // Vị trí vừa thực hiện seek xong (Reset khi chuyển bài)
    var lastSeekPosition by remember { mutableStateOf<Long?>(null) }

    // Reset chủ động khi bài hát thay đổi
    LaunchedEffect(songId) {
        draggingProgress = null
        lastSeekPosition = null
    }

    // Giải phóng lastSeekPosition khi vị trí thực tế của hệ thống đã đuổi kịp (sai số < 2s)
    LaunchedEffect(progressMs) {
        lastSeekPosition?.let { seeked ->
            // Reset nếu đã đuổi kịp HOẶC nếu hệ thống nhảy về 0 (dấu hiệu đổi bài)
            if (abs(progressMs - seeked) < 2000 || progressMs < 1000) {
                lastSeekPosition = null
            }
        }
    }

    // Giá trị hiển thị ưu tiên theo thứ tự: Đang kéo > Vừa seek xong > Dữ liệu hệ thống
    val displayProgressMs = when {
        draggingProgress != null -> (draggingProgress!! * safeDuration).toLong()
        lastSeekPosition != null -> lastSeekPosition!!
        else -> progressMs
    }.coerceIn(0, safeDuration)

    val displayFraction = if (safeDuration > 0) {
        displayProgressMs.coerceIn(0, safeDuration).toFloat() / safeDuration
    } else 0f

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayProgressMs.formatDuration(),
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceDark,
        )
        Spacer(modifier = Modifier.width(LocalDimensions.current.paddingSmall))
        AudilySlider(
            modifier = Modifier.weight(1f),
            value = displayFraction,
            onValueChange = { draggingProgress = it },
            onValueChangeFinished = {
                draggingProgress?.let {
                    val targetPos = (it * safeDuration).toLong()
                    lastSeekPosition = targetPos
                    onSeek(targetPos)
                }
                draggingProgress = null
            }
        )
        Spacer(modifier = Modifier.width(LocalDimensions.current.paddingSmall))
        Text(
            text = safeDuration.formatDuration(),
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceDark,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudilySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = OnSurfaceDark.copy(alpha = 0.1f),
    thumbColor: Color = Color.White
) {
    Slider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        thumb = {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(thumbColor, CircleShape)
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier.height(6.dp),
                thumbTrackGapSize = 0.dp,
                colors = SliderDefaults.colors(
                    activeTrackColor = activeColor,
                    inactiveTrackColor = inactiveColor
                )
            )
        }
    )
}
