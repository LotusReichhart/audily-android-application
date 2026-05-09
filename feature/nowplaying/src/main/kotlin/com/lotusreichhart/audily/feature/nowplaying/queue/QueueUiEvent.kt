package com.lotusreichhart.audily.feature.nowplaying.queue

sealed interface QueueUiEvent {
    data class OnMoveQueueItem(val fromIndex: Int, val toIndex: Int) : QueueUiEvent
    data class OnRemoveFromQueue(val songId: Long) : QueueUiEvent
    data class OnSongClicked(val index: Int, val songId: Long) : QueueUiEvent
    data class OnSkipToIndex(val index: Int) : QueueUiEvent
    data object OnStopQueue : QueueUiEvent
}