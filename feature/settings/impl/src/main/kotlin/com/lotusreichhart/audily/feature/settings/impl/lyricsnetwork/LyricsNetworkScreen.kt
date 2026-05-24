package com.lotusreichhart.audily.feature.settings.impl.lyricsnetwork

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
internal fun LyricsNetworkScreen(
    modifier: Modifier = Modifier,
    viewModel: LyricsNetworkViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

}

@Composable
internal fun LyricsNetworkScreen(
    modifier: Modifier = Modifier,
    uiState: LyricsNetworkUiState,
    onEvent: (LyricsNetworkUiEvent) -> Unit,
    onBack: () -> Unit
) {

}