package com.lotusreichhart.audily.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Khởi tạo các thành phần UI toàn cục như Registry cho BottomSheets.
 */
@Composable
fun GlobalUiInitializer() {
    LaunchedEffect(Unit) {
        GlobalSheetRegistry.register(GlobalSheetKey.QUEUE) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Queue Content Placeholder (Registered via Registry)",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
