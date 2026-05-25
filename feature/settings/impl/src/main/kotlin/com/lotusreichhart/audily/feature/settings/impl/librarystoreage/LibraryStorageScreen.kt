package com.lotusreichhart.audily.feature.settings.impl.librarystoreage

import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.settings.impl.R
import com.lotusreichhart.audily.feature.settings.impl.librarystoreage.component.LibraryStorageTopBar

@Composable
internal fun LibraryStorageScreen(
    modifier: Modifier = Modifier,
    viewModel: LibraryStorageViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LibraryStorageScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@Composable
internal fun LibraryStorageScreen(
    modifier: Modifier = Modifier,
    uiState: LibraryStorageUiState,
    onEvent: (LibraryStorageUiEvent) -> Unit,
    onBack: () -> Unit
) {
    val dimensions = LocalDimensions.current
    var showClearDialog by remember { mutableStateOf(false) }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            val path = getPathFromUri(it)
            if (path != null) {
                onEvent(LibraryStorageUiEvent.AddExcludedFolder(path))
            }
        }
    }

    AudilyScaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LibraryStorageTopBar(
                onBack = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Choose Folders to Exclude
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.feature_settings_impl_library_folders_section).uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = stringResource(R.string.feature_settings_impl_library_folders_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
                            )
                    ) {
                        if (uiState.excludedFolders.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensions.paddingMedium),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.feature_settings_impl_library_no_excluded_folders),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            uiState.excludedFolders.forEachIndexed { index, folder ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = dimensions.paddingMedium,
                                            vertical = 12.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = folder,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .clickable {
                                                onEvent(
                                                    LibraryStorageUiEvent.RemoveExcludedFolder(
                                                        folder
                                                    )
                                                )
                                            }
                                            .padding(6.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = AudilyIcons.Close),
                                            contentDescription = "Remove",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                if (index < uiState.excludedFolders.lastIndex) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(horizontal = dimensions.paddingMedium)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                            thickness = 1.dp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { folderPickerLauncher.launch(null) }
                                .padding(dimensions.paddingMedium),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = AudilyIcons.Add),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = stringResource(R.string.feature_settings_impl_library_add_folder),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // 2. Rescan MediaStore
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_settings_impl_library_actions_section).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(dimensions.cornerRadiusLarge))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            )
                            .clickable(enabled = !uiState.isScanning) {
                                onEvent(LibraryStorageUiEvent.RescanMediaStore)
                            }
                            .padding(dimensions.paddingMedium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isScanning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = AudilyIcons.Refresh),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.feature_settings_impl_library_rescan_btn),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            // 3. Clear History
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_settings_impl_library_data_section).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.08f))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
                            )
                            .clickable {
                                showClearDialog = true
                            }
                            .padding(dimensions.paddingMedium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = AudilyIcons.Delete),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )

                        Text(
                            text = stringResource(R.string.feature_settings_impl_library_clear_history_btn),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = {
                    Text(
                        text = stringResource(R.string.feature_settings_impl_library_clear_dialog_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.feature_settings_impl_library_clear_dialog_msg),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearDialog = false
                            onEvent(LibraryStorageUiEvent.ClearAllHistory)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.feature_settings_impl_library_clear_dialog_confirm),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showClearDialog = false }
                    ) {
                        Text(
                            text = stringResource(R.string.feature_settings_impl_library_clear_dialog_cancel),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getPathFromUri(uri: Uri): String? {
    if ("com.android.externalstorage.documents" == uri.authority) {
        val docId = DocumentsContract.getTreeDocumentId(uri)
        val split = docId.split(":")
        val type = split.getOrNull(0)
        val relativePath = split.getOrNull(1) ?: ""
        return if ("primary".equals(type, ignoreCase = true)) {
            "/storage/emulated/0/$relativePath"
        } else {
            "/storage/$type/$relativePath"
        }
    }
    return uri.path
}