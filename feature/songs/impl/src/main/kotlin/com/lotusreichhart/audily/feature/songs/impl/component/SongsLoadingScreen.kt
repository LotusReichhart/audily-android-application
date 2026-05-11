package com.lotusreichhart.audily.feature.songs.impl.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lotusreichhart.audily.core.designsystem.component.SongItemShimmer
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

/**
 * Màn hình hiển thị trạng thái đang tải dữ liệu cho danh sách bài hát.
 */
@Composable
internal fun SongsLoadingScreen(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        SongsHeaderShimmer()
        repeat(20) {
            SongItemShimmer()
        }
    }
}
