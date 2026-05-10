package com.lotusreichhart.audily.feature.songs.impl.menu

sealed class SongMenuUiEvent {
    data class OnActionClick(val action: SongMenuAction) : SongMenuUiEvent()
}