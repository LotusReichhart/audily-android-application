package com.lotusreichhart.audily.feature.albums.impl.detail.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.designsystem.component.AudilyButton
import com.lotusreichhart.audily.core.designsystem.component.AudilyInputField
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.albums.impl.R

@Composable
internal fun AlbumSavePlaylistSheet(
    initialName: String,
    initialDescription: String,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }

    val dimensions = LocalDimensions.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = dimensions.paddingMedium)
            .padding(bottom = dimensions.paddingMedium)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensions.paddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.feature_albums_impl_detail_create_playlist),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Icon(
                painter = painterResource(id = AudilyIcons.Close),
                contentDescription = "Close",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        onClick = onDismiss,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(dimensions.paddingSmall))

        // Playlist Name
        Text(
            text = stringResource(id = R.string.feature_albums_impl_detail_playlist_name),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(dimensions.paddingSmall))

        AudilyInputField(
            value = name,
            onValueChange = { name = it },
            placeholder = stringResource(id = R.string.feature_albums_impl_detail_playlist_name_placeholder),
            maxLength = 30
        )

        Spacer(modifier = Modifier.height(dimensions.paddingSmall))

        // Description
        Text(
            text = stringResource(id = R.string.feature_albums_impl_detail_playlist_description_optional),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(dimensions.paddingSmall))

        AudilyInputField(
            value = description,
            onValueChange = { description = it },
            placeholder = stringResource(id = R.string.feature_albums_impl_detail_playlist_description_placeholder),
            singleLine = false,
            inputBoxModifier = Modifier.height(100.dp),
            maxLength = 50
        )

        Spacer(modifier = Modifier.height(dimensions.paddingLarge))

        // Save Button
        AudilyButton(
            text = stringResource(id = coreR.string.core_designsystem_done),
            onClick = {
                if (name.isNotBlank()) {
                    onSave(name, description.ifBlank { null })
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank()
        )
    }
}
