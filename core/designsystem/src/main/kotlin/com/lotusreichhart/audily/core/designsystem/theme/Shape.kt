package com.lotusreichhart.audily.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Hệ thống bo góc chuẩn của Audily
val AudilyShapes = Shapes(
    small = RoundedCornerShape(8.dp),   // Dùng cho Mini Player thumbnail, Tags
    medium = RoundedCornerShape(16.dp), // Dùng cho Card bài hát, Dialog
    large = RoundedCornerShape(24.dp),  // Dùng cho Now Playing Cover Art, Bottom Sheet
    extraLarge = RoundedCornerShape(32.dp)
)