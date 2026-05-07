package com.lotusreichhart.audily.feature.home.impl

import com.lotusreichhart.audily.core.model.home.HomeVibe

/**
 * Trạng thái giao diện của màn hình Home.
 */
internal sealed interface HomeUiState {
    data object Loading : HomeUiState
    
    data class Success(
        val homeVibe: HomeVibe
    ) : HomeUiState
    
    data class Error(val message: String? = null) : HomeUiState
}