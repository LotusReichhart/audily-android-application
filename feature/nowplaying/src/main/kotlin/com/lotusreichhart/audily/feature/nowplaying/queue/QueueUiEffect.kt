package com.lotusreichhart.audily.feature.nowplaying.queue

sealed interface QueueUiEffect {
    data class NavigateToPlaylist(val playlistId: Long) : QueueUiEffect
}
