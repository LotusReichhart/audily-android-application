package com.lotusreichhart.audily.feature.home.impl

import com.lotusreichhart.audily.core.model.home.HomeVibe
import com.lotusreichhart.audily.core.model.playback.PlaybackState

/**
 * Trạng thái giao diện của màn hình Home.
 */
internal sealed interface HomeUiState {
    data object Loading : HomeUiState
    
    data class Success(
        val homeVibe: HomeVibe,
        val playbackState: PlaybackState = PlaybackState.INITIAL
    ) : HomeUiState
    
    data class Error(val message: String? = null) : HomeUiState
}