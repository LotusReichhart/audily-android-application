package com.lotusreichhart.audily.feature.songs.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lotusreichhart.audily.feature.songs.api.SongsEntry
import javax.inject.Inject

/**
 * Triển khai cụ thể của [SongsEntry].
 */
internal class SongsEntryImpl @Inject constructor() : SongsEntry {
    @Composable
    override fun Render(modifier: Modifier) {
        SongsScreen(modifier = modifier)
    }
}