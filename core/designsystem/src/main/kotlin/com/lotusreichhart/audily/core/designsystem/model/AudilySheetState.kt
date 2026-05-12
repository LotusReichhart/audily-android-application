package com.lotusreichhart.audily.core.designsystem.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class AudilySheetState(
    val content: (@Composable () -> Unit)? = null,
    val isFullScreen: Boolean = false,
    val showDragHandle: Boolean = false,
    val enableSwipeToDismiss: Boolean = true,
    val containerColor: Color = Color.Transparent,
    val skipPartiallyExpanded: Boolean = false
)
