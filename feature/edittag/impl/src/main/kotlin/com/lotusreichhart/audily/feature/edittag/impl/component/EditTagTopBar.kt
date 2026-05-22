package com.lotusreichhart.audily.feature.edittag.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.feature.edittag.api.R

@Composable
internal fun EditTagTopBar(
    onBack: () -> Unit,
    onSaveEnabled: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Cancel Button (Left)
        Text(
            text = stringResource(id = coreR.string.core_designsystem_cancel).uppercase(),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(
                    onClick = onBack,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(vertical = 8.dp, horizontal = 4.dp)
        )

        // Title (Center)
        Text(
            text = stringResource(id = R.string.feature_edittag_api_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )

        // Save Button (Right)
        Text(
            text = stringResource(id = coreR.string.core_designsystem_save).uppercase(),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (onSaveEnabled) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable(
                    enabled = onSaveEnabled,
                    onClick = onSave,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(vertical = 8.dp, horizontal = 4.dp)
        )
    }
}