package com.lotusreichhart.audily.feature.settings.impl.audioplayback

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
internal fun AudioPlaybackScreen(
    modifier: Modifier = Modifier,
    viewModel: AudioPlaybackViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
}

@Composable
internal fun AudioPlaybackScreen(
    modifier: Modifier = Modifier,
    uiState: AudioPlaybackUiState,
    onEvent: (AudioPlaybackUiEvent) -> Unit,
    onBack: () -> Unit
) {
}