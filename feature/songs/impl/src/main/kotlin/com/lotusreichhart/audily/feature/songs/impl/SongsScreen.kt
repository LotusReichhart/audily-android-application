package com.lotusreichhart.audily.feature.songs.impl

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.feature.songs.impl.component.SongsLoadingScreen
import com.lotusreichhart.audily.feature.songs.impl.component.SongsScreenContent

/**
 * Điểm vào chính cho màn hình danh sách bài hát.
 * Chịu trách nhiệm quản lý trạng thái UI (Loading/Success) và chuyển đổi giao diện mượt mà.
 */
@Composable
internal fun SongsScreen(
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playingSongId by viewModel.playingSongId.collectAsStateWithLifecycle()
    val isPaused by viewModel.isPaused.collectAsStateWithLifecycle()

    val songs = uiState.songs.collectAsLazyPagingItems()

    AudilyScaffold(
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Crossfade(
            targetState = uiState.isLoading,
            animationSpec = tween(durationMillis = 500),
            label = "SongsLoadingCrossfade"
        ) { isLoading ->
            if (isLoading) {
                SongsLoadingScreen(innerPadding = innerPadding)
            } else {
                SongsScreenContent(
                    songs = songs,
                    summary = uiState.summary,
                    playingSongId = playingSongId,
                    isPaused = isPaused,
                    onEvent = { viewModel.onEvent(it) },
                    innerPadding = innerPadding
                )
            }
        }
    }
}
