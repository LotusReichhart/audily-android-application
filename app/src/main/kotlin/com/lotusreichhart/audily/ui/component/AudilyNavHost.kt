package com.lotusreichhart.audily.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.lotusreichhart.audily.core.designsystem.theme.LocalDynamicBottomPadding

@Composable
fun <T : Any> AudilyNavHost(
    entries: List<NavEntry<T>>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomPadding = LocalDynamicBottomPadding.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = bottomPadding)
    ) {
        NavDisplay(
            entries = entries,
            onBack = onBack
        )
    }
}
