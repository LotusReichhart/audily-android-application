package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

/**
 * Thành phần nhập liệu chuẩn của Audily.
 * Hỗ trợ giới hạn ký tự, hiển thị bộ đếm và tùy chỉnh chiều cao khung nhập.
 *
 * @param value Giá trị hiện tại của text.
 * @param onValueChange Callback khi text thay đổi.
 * @param placeholder Văn bản hiển thị khi ô nhập trống.
 * @param modifier Modifier cho toàn bộ component (bao gồm cả counter).
 * @param inputBoxModifier Modifier riêng cho khung nhập liệu (dùng để chỉnh height, background...).
 * @param singleLine Có cho phép nhập nhiều dòng hay không.
 * @param maxLength Giới hạn số ký tự tối đa. Nếu null sẽ không giới hạn.
 */
@Composable
fun AudilyInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    inputBoxModifier: Modifier = Modifier,
    singleLine: Boolean = true,
    maxLength: Int? = null
) {
    val dimensions = LocalDimensions.current

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = inputBoxModifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(dimensions.cornerRadiusSmall)
                )
                .padding(dimensions.paddingMedium)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = {
                    if (maxLength == null || it.length <= maxLength) {
                        onValueChange(it)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = singleLine
            )
        }

        if (maxLength != null) {
            Text(
                text = "${value.length}/$maxLength",
                style = MaterialTheme.typography.labelSmall,
                color = if (value.length >= maxLength) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp, end = 4.dp)
            )
        }
    }
}
