package com.lotusreichhart.audily.core.ui

/**
 * Các sự kiện UI mang tính toàn cục (ví dụ: mở BottomSheet, hiển thị Toast...).
 */
sealed class GlobalUiEvent {
    data class OpenSheet(
        val key: String,
        val isFullScreen: Boolean = false
    ) : GlobalUiEvent()
    
    object HideSheet : GlobalUiEvent()
    
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null
    ) : GlobalUiEvent()
}
