package com.lotusreichhart.audily.feature.home.impl

internal sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val selectedTab: HomeTab) : HomeUiState
}