package com.lotusreichhart.audily.feature.settings.impl.language

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
internal fun LanguageScreen(
    modifier: Modifier = Modifier,
    viewModel: LanguageViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

}

@Composable
internal fun LanguageScreen(
    modifier: Modifier = Modifier,
    uiState: LanguageUiState,
    onEvent: (LanguageUiEvent) -> Unit,
    onBack: () -> Unit
) {

}