package com.lotusreichhart.audily.core.ui

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bus điều phối các sự kiện UI toàn cục.
 * Cho phép cả UI và ViewModel có thể phát tín hiệu điều khiển.
 */
@Singleton
class GlobalUiEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<GlobalUiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun emit(event: GlobalUiEvent) {
        _events.tryEmit(event)
    }
}
