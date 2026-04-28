package com.lotusreichhart.audily.feature.nowplaying

sealed interface NowPlayingUiEvent {
    data object OnPlayPauseToggle : NowPlayingUiEvent
    data object OnSkipNext : NowPlayingUiEvent
    data object OnSkipPrevious : NowPlayingUiEvent
    data class OnSeekTo(val position: Long) : NowPlayingUiEvent
    data object OnFastForward : NowPlayingUiEvent
    data object OnFastRewind : NowPlayingUiEvent
    data object OnShuffleToggle : NowPlayingUiEvent
    data object OnRepeatModeToggle : NowPlayingUiEvent
    data object OnToggleFavorite : NowPlayingUiEvent
    data object OnToggleLyrics : NowPlayingUiEvent
    data object OnSetRingtone : NowPlayingUiEvent
    data object OnTimerClick : NowPlayingUiEvent
    data object OnOpenQueue : NowPlayingUiEvent
    data object OnNavigateBack : NowPlayingUiEvent
}
