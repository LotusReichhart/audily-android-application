package com.lotusreichhart.audily.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Interface cơ sở cho mọi Feature Entry trong ứng dụng.
 * Giúp các module Feature có thể cung cấp nội dung UI của mình cho bên ngoài
 */
interface FeatureEntry {
    @Composable
    fun Render(modifier: Modifier)
}
