package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lotusreichhart.audily.core.designsystem.R
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

data class ActionItem(
    val label: String,
    val icon: Int? = null,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false
)

@Composable
fun AudilyActionSheet(
    options: List<ActionItem>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    header: @Composable (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = LocalDimensions.current.paddingMedium)
            .padding(bottom = LocalDimensions.current.paddingMedium)
            .navigationBarsPadding()
    ) {
        // Khối tùy chọn (Options Card)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                if (header != null) {
                    header()
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                } else if (title != null) {
                    Text(
                        text = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = LocalDimensions.current.paddingMedium,
                                vertical = 14.dp
                            ),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }

                options.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                item.onClick()
                                onDismiss() // Tự động đóng sau khi click
                            }
                            .padding(
                                horizontal = LocalDimensions.current.paddingMedium,
                                vertical = 14.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.icon != null) {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null,
                                tint = if (item.isDestructive) Color.Red else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(LocalDimensions.current.paddingSmall))
                        }
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 17.sp
                            ),
                            color = if (item.isDestructive) Color.Red else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start
                        )
                    }
                    if (index < options.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 0.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(LocalDimensions.current.paddingSmall))

        // Nút Đóng tách biệt (Cancel Card)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDismiss() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = LocalDimensions.current.paddingMedium,
                        vertical = 14.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.core_designsystem_unknown_cancel),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
