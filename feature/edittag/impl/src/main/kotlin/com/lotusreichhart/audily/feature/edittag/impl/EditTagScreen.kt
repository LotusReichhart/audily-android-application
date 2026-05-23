package com.lotusreichhart.audily.feature.edittag.impl

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.component.ActionItem
import com.lotusreichhart.audily.core.designsystem.component.AudilyActionSheet
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.core.ui.adaptive.AudilyAdaptiveLayout
import com.lotusreichhart.audily.feature.edittag.impl.layout.LandscapeEditTagLayout
import com.lotusreichhart.audily.feature.edittag.impl.layout.PortraitEditTagLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
internal fun EditTagScreen(
    modifier: Modifier = Modifier,
    songId: Long,
    viewModel: EditTagViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetController = LocalAudilySheetController.current

    LaunchedEffect(songId) {
        viewModel.onEvent(EditTagUiEvent.Init(songId))
    }

    LaunchedEffect(viewModel.uiEffect) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                EditTagUiEffect.EditTagSaved -> onBack()
            }
        }
    }

    // Permission launcher for Android 10+ Scoped Storage (RecoverableSecurityException)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onEvent(EditTagUiEvent.PermissionGranted)
        }
    }

    LaunchedEffect(uiState.permissionIntentSender) {
        uiState.permissionIntentSender?.let { intentSender ->
            val request = IntentSenderRequest.Builder(intentSender).build()
            permissionLauncher.launch(request)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (bytes != null) {
                        viewModel.onEvent(EditTagUiEvent.ArtworkChanged(bytes))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    EditTagScreen(
        modifier = modifier,
        uiState = uiState,
        onBack = onBack,
        onArtworkClick = {
            sheetController.showSheet(
                content = {
                    AudilyActionSheet(
                        options = listOf(
                            ActionItem(
                                label = stringResource(R.string.feature_edittag_impl_artwork_change),
                                onClick = {
                                    galleryLauncher.launch("image/*")
                                    sheetController.hideSheet()
                                }
                            ),
                            ActionItem(
                                label = stringResource(R.string.feature_edittag_impl_artwork_remove),
                                onClick = {
                                    viewModel.onEvent(EditTagUiEvent.RemoveArtwork)
                                    sheetController.hideSheet()
                                },
                                isDestructive = true
                            )
                        ),
                        onDismiss = { sheetController.hideSheet() }
                    )
                }
            )
        },
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun EditTagScreen(
    modifier: Modifier = Modifier,
    uiState: EditTagUiState,
    onBack: () -> Unit,
    onArtworkClick: () -> Unit,
    onEvent: (EditTagUiEvent) -> Unit
) {
    AudilyAdaptiveLayout(
        portrait = {
            PortraitEditTagLayout(
                modifier = modifier,
                uiState = uiState,
                onBack = onBack,
                onArtworkClick = onArtworkClick,
                onEvent = onEvent
            )
        },
        landscape = {
            LandscapeEditTagLayout(
                modifier = modifier,
                uiState = uiState,
                onBack = onBack,
                onArtworkClick = onArtworkClick,
                onEvent = onEvent
            )
        },
        medium = {
            PortraitEditTagLayout(
                modifier = modifier,
                uiState = uiState,
                onBack = onBack,
                onArtworkClick = onArtworkClick,
                onEvent = onEvent
            )
        },
        expanded = {
            PortraitEditTagLayout(
                modifier = modifier,
                uiState = uiState,
                onBack = onBack,
                onArtworkClick = onArtworkClick,
                onEvent = onEvent
            )
        }
    )
}

