package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Một IconButton tùy chỉnh cho Audily, sử dụng Box và Icon để kiểm soát tốt hơn.
 *
 * @param onClick Callback khi nhấn vào nút.
 * @param painter Biểu tượng sẽ hiển thị.
 * @param contentDescription Mô tả cho accessibility.
 * @param modifier Modifier cho container bên ngoài.
 * @param iconSize Kích thước của icon bên trong.
 * @param containerSize Kích thước vùng bấm.
 * @param tint Màu sắc của icon.
 * @param backgroundColor Màu nền của nút.
 * @param shape Hình dạng của nút (mặc định là CircleShape).
 */
@Composable
fun AudilyIconButton(
    onClick: () -> Unit,
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    containerSize: Dp = 40.dp,
    tint: Color = LocalContentColor.current,
    backgroundColor: Color = Color.Transparent,
    shape: Shape = CircleShape
) {
    Box(
        modifier = modifier
            .size(containerSize)
            .clip(shape)
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = containerSize / 2)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = tint
        )
    }
}
