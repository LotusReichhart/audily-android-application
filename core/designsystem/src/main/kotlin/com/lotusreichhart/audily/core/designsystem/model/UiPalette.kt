package com.lotusreichhart.audily.core.designsystem.model

import androidx.compose.ui.graphics.Color
import com.lotusreichhart.audily.core.model.playback.PaletteColors

data class UiPalette(
    val vibrant: Color = Color.Gray,
    val vibrantDark: Color = Color.Black,
    val dominant: Color = Color.DarkGray
)

fun PaletteColors.toUiPalette() = UiPalette(
    vibrant = Color(vibrant),
    vibrantDark = Color(vibrantDark),
    dominant = Color(dominant)
)
