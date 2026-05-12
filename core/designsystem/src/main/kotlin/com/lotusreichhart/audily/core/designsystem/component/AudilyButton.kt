package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

/**
 * Thành phần Button chuẩn chỉnh của hệ thống thiết kế Audily.
 *
 * @param text Nội dung hiển thị bên trong nút.
 * @param onClick Callback khi nút được bấm.
 * @param modifier Modifier tùy biến cho nút.
 * @param leadingIcon Icon resource ID hiển thị bên trái Text (mặc định là null).
 * @param enabled Trạng thái kích hoạt của nút.
 * @param containerColor Màu nền của nút.
 * @param contentColor Màu vủa nội dung (text/icon).
 * @param shape Hình dạng bo góc của nút.
 */
@Composable
fun AudilyButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    leadingIcon: Int? = null,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape = MaterialTheme.shapes.medium
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(LocalDimensions.current.buttonHeight),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        if (leadingIcon != null) {
            Icon(
                painter = painterResource(id = leadingIcon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(LocalDimensions.current.paddingSmall))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
