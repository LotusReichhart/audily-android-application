package com.lotusreichhart.audily.core.ui.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyButton
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.ui.R

/**
 * Màn hình yêu cầu cấp quyền truy cập âm nhạc hoặc thông báo.
 */
@Composable
fun PermissionScreen(
    isNotification: Boolean,
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(LocalDimensions.current.paddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isNotification) Icons.Rounded.Notifications else Icons.Rounded.AudioFile,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        val titleKey = if (isNotification) {
            R.string.core_ui_notification_permission_title
        } else {
            R.string.core_ui_storage_permission_title
        }

        Text(
            text = stringResource(titleKey),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        val descriptionKey = if (isNotification) {
            if (shouldShowRationale) {
                R.string.core_ui_notification_permission_description_rationale
            } else {
                R.string.core_ui_notification_permission_description_denied
            }
        } else {
            if (shouldShowRationale) {
                R.string.core_ui_storage_permission_description_rationale
            } else {
                R.string.core_ui_storage_permission_description_denied
            }
        }

        Text(
            text = stringResource(descriptionKey),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        val buttonTextKey = if (isNotification) {
            R.string.core_ui_notification_permission_button_text
        } else {
            R.string.core_ui_storage_permission_button_text
        }

        AudilyButton(
            text = stringResource(buttonTextKey),
            onClick = onRequestPermission
        )
    }
}

