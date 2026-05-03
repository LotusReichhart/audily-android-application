package com.lotusreichhart.audily.feature.nowplaying.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
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

@Composable
private fun AudilySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = OnSurfaceDark.copy(alpha = 0.5f),
    thumbColor: Color = Color.White
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val thumbRadius = 6.dp.toPx()
                    val trackWidth = size.width - 2 * thumbRadius
                    val newValue = ((offset.x - thumbRadius) / trackWidth).coerceIn(0f, 1f)
                    currentOnValueChange(newValue)
                    currentOnValueChangeFinished()
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { currentOnValueChangeFinished() },
                    onDragCancel = { currentOnValueChangeFinished() },
                    onDrag = { change, _ ->
                        val thumbRadius = 6.dp.toPx()
                        val trackWidth = size.width - 2 * thumbRadius
                        val newValue =
                            ((change.position.x - thumbRadius) / trackWidth).coerceIn(0f, 1f)
                        currentOnValueChange(newValue)
                    }
                )
            }
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val centerY = height / 2

        Canvas(modifier = Modifier.fillMaxSize()) {
            val trackHeight = 4.dp.toPx()
            val thumbRadius = 6.dp.toPx()

            // Inactive Track
            drawLine(
                color = inactiveColor,
                start = Offset(thumbRadius, centerY),
                end = Offset(width - thumbRadius, centerY),
                strokeWidth = trackHeight,
                cap = StrokeCap.Round
            )

            // Active Track
            val activeEnd = thumbRadius + (width - 2 * thumbRadius) * value
            drawLine(
                color = activeColor,
                start = Offset(thumbRadius, centerY),
                end = Offset(activeEnd, centerY),
                strokeWidth = trackHeight,
                cap = StrokeCap.Round
            )

            // Thumb
            drawCircle(
                color = thumbColor,
                radius = thumbRadius,
                center = Offset(activeEnd, centerY)
            )
        }
    }
}
