package com.lotusreichhart.audily.feature.songs.impl

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.max

@Composable
internal fun rememberSongsScreenState(
    lazyListState: LazyListState = rememberLazyListState()
): SongsScreenState {
    val density = LocalDensity.current
    val headerHeightPx = with(density) { SongsScreenConstants.HeaderHeight.toPx() }
    
    return remember(lazyListState, headerHeightPx) {
        SongsScreenState(lazyListState, headerHeightPx)
    }
}

internal class SongsScreenState(
    val lazyListState: LazyListState,
    private val headerHeightPx: Float
) {
    // Offset hiện tại của Header (0 là hiển thị đầy đủ, âm là đang trượt lên)
    private var _headerOffset by mutableFloatStateOf(0f)
    val headerOffset: Float get() = _headerOffset

    // Alpha của Header dựa trên offset (mờ dần nhanh hơn trượt)
    val headerAlpha by derivedStateOf {
        max(0f, 1f + (_headerOffset / (headerHeightPx * 0.8f)))
    }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y
            
            // Cập nhật offset của header dựa trên delta cuộn
            val newOffset = _headerOffset + delta
            _headerOffset = newOffset.coerceIn(-headerHeightPx, 0f)

            return Offset.Zero
        }
    }
}
