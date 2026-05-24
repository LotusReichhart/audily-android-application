package com.lotusreichhart.audily.feature.settings.impl.librarystoreage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
internal fun LibraryStorageScreen(
    modifier: Modifier = Modifier,
    viewModel: LibraryStorageViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

}

@Composable
internal fun LibraryStorageScreen(
    modifier: Modifier = Modifier,
    uiState: LibraryStorageUiState,
    onEvent: (LibraryStorageUiEvent) -> Unit,
    onBack: () -> Unit
) {

}