package com.lotusreichhart.audily.feature.nowplaying.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.lotusreichhart.audily.core.designsystem.model.UiPalette
import com.lotusreichhart.audily.core.designsystem.theme.SurfaceDark

/**
 * Modifier vẽ nền gradient động dựa trên Palette màu của bài hát.
 */
@Composable
fun Modifier.nowPlayingBackground(
    paletteColors: UiPalette?,
    useGlassmorphism: Boolean
): Modifier {
    if (!useGlassmorphism) {
        return this.background(SurfaceDark)
    }
    val defaultColor = SurfaceDark
    val adaptiveColor = paletteColors?.dominant ?: defaultColor
    val vibrantColor = paletteColors?.vibrant ?: adaptiveColor

    // Hiệu ứng chuyển màu mượt mà khi đổi bài
    val animatedDominantColor by animateColorAsState(
        targetValue = adaptiveColor,
        animationSpec = tween(1000),
        label = "DominantColorAnimation"
    )
    val animatedVibrantColor by animateColorAsState(
        targetValue = vibrantColor,
        animationSpec = tween(1000),
        label = "VibrantColorAnimation"
    )

    // Hiệu ứng chuyển động gradient vô hạn
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundAnimation")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GradientOffset"
    )

    return this
        .background(SurfaceDark)
        .clipToBounds()
        .drawBehind {
            val canvasSize = size
            val baseRadius = canvasSize.maxDimension * 1.2f

            // Circle 1: Vibrant
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(animatedVibrantColor.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(
                        x = canvasSize.width * (0.1f + 0.3f * gradientOffset),
                        y = canvasSize.height * (0.2f + 0.2f * (1f - gradientOffset))
                    ),
                    radius = baseRadius
                ),
                radius = baseRadius,
                center = Offset(
                    x = canvasSize.width * (0.1f + 0.3f * gradientOffset),
                    y = canvasSize.height * (0.2f + 0.2f * (1f - gradientOffset))
                )
            )

            // Circle 2: Dominant (Bottom Right)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        animatedDominantColor.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(
                        x = canvasSize.width * (0.9f - 0.3f * gradientOffset),
                        y = canvasSize.height * (0.8f - 0.2f * (1f - gradientOffset))
                    ),
                    radius = baseRadius * 1.1f
                ),
                radius = baseRadius * 1.1f,
                center = Offset(
                    x = canvasSize.width * (0.9f - 0.3f * gradientOffset),
                    y = canvasSize.height * (0.8f - 0.2f * (1f - gradientOffset))
                )
            )

            // Circle 3: Dominant (Center)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        animatedDominantColor.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(
                        x = canvasSize.width * (0.5f + 0.2f * (gradientOffset * 2f - 1f)),
                        y = canvasSize.height * 0.5f
                    ),
                    radius = baseRadius * 1.4f
                ),
                radius = baseRadius * 1.4f,
                center = Offset(
                    x = canvasSize.width * (0.5f + 0.2f * (gradientOffset * 2f - 1f)),
                    y = canvasSize.height * 0.5f
                )
            )
        }
}
