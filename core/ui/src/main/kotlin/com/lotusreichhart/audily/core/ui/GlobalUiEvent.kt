package com.lotusreichhart.audily.core.ui

import androidx.compose.material3.SnackbarDuration
import com.lotusreichhart.audily.core.ui.util.UiText

/**
 * Các sự kiện UI mang tính toàn cục (ví dụ: mở BottomSheet, hiển thị Toast...).
 */
sealed class GlobalUiEvent {
    data class OpenSheet(
        val key: String,
        val params: Any? = null,
        val isFullScreen: Boolean = false,
        val isShowDragHandle: Boolean = false
    ) : GlobalUiEvent()
    
    object HideSheet : GlobalUiEvent()
    
    data class ShowSnackbar(
        val message: UiText,
        val actionLabel: UiText? = null,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        val onAction: (() -> Unit)? = null
    ) : GlobalUiEvent()

    object OpenWriteSettingsPermission : GlobalUiEvent()

    data class RequestFilePermission(val intentSender: Any) : GlobalUiEvent()
}
