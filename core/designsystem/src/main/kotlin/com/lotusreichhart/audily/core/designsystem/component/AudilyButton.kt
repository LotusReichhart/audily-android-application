package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

/**
 * Thành phần Button chuẩn chỉnh của hệ thống thiết kế Audily.
 *
 * @param text Nội dung hiển thị bên trong nút.
 * @param onClick Callback khi nút được bấm.
 * @param modifier Modifier tùy biến cho nút.
 * @param enabled Trạng thái kích hoạt của nút.
 * @param containerColor Màu nền của nút.
 * @param contentColor Màu vủa nội dung (text/icon).
 * @param shape Hình dạng bo góc của nút.
 */
@Composable
fun AudilyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape = MaterialTheme.shapes.medium
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(LocalDimensions.current.buttonHeight),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
