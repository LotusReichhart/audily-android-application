package com.lotusreichhart.audily.core.ui

import androidx.compose.runtime.staticCompositionLocalOf

val LocalGlobalUiEventBus = staticCompositionLocalOf<GlobalUiEventBus> {
    error("No GlobalUiEventBus provided")
}