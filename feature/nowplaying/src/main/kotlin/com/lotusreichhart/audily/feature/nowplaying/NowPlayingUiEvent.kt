package com.lotusreichhart.audily.feature.nowplaying

sealed interface NowPlayingUiEvent {
    data object OnResumePauseToggle : NowPlayingUiEvent
    data object OnSkipNext : NowPlayingUiEvent
    data object OnSkipPrevious : NowPlayingUiEvent
    data class OnSkipTo(val index: Int) : NowPlayingUiEvent
    data class OnSeekTo(val position: Long) : NowPlayingUiEvent
    data object OnFastForward : NowPlayingUiEvent
    data object OnFastRewind : NowPlayingUiEvent
    data object OnShuffleToggle : NowPlayingUiEvent
    data object OnRepeatModeToggle : NowPlayingUiEvent
    data object OnToggleFavorite : NowPlayingUiEvent
    data object OnToggleLyrics : NowPlayingUiEvent
}
