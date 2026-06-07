package com.lotusreichhart.audily.feature.songs.impl.picker

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.feature.songs.impl.component.SongsSortSheet
import com.lotusreichhart.audily.feature.songs.impl.picker.component.SongPickerItem
import com.lotusreichhart.audily.feature.songs.impl.picker.component.SongPickerItemShimmer
import com.lotusreichhart.audily.feature.songs.impl.picker.component.SongsPickerEmptyScreen
import com.lotusreichhart.audily.feature.songs.impl.picker.component.SongsPickerNotFoundScreen
import com.lotusreichhart.audily.feature.songs.impl.picker.component.SongsPickerTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SongsPickerScreen(
    modifier: Modifier = Modifier,
    playlistId: Long,
    onBack: () -> Unit,
    viewModel: SongsPickerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val songs = uiState.songs.collectAsLazyPagingItems()
    val sheetController = LocalAudilySheetController.current
    val sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant

    LaunchedEffect(playlistId) {
        viewModel.onEvent(SongsPickerUiEvent.Init(playlistId))
    }

    LaunchedEffect(viewModel.uiEffect) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                SongsPickerUiEffect.SongsSelectedSaved -> {
                    onBack()
                }
            }
        }
    }

    SongsPickerScreen(
        modifier = modifier,
        uiState = uiState,
        songs = songs,
        onBack = onBack,
        onEvent = viewModel::onEvent,
        onSortClick = {
            sheetController.showSheet(
                content = {
                    SongsSortSheet(
                        initialSortOrder = uiState.sortOrder,
                        initialSortType = uiState.sortType,
                        onSave = { order, type ->
                            viewModel.onEvent(SongsPickerUiEvent.SortOrderChanged(order))
                            viewModel.onEvent(SongsPickerUiEvent.SortTypeChanged(type))
                            sheetController.hideSheet()
                        }
                    )
                },
                showDragHandle = true,
                containerColor = sheetContainerColor,
                skipPartiallyExpanded = true
            )
        }
    )
}

@Composable
internal fun SongsPickerScreen(
    modifier: Modifier = Modifier,
    uiState: SongsPickerUiState,
    songs: LazyPagingItems<Song>,
    onBack: () -> Unit,
    onEvent: (SongsPickerUiEvent) -> Unit,
    onSortClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    AudilyScaffold(
        topBar = {
            SongsPickerTopBar(
                selectedCount = uiState.songsSelected.size,
                query = uiState.query,
                onQueryChange = { onEvent(SongsPickerUiEvent.OnQueryChange(it)) },
                onBack = onBack,
                onClear = { onEvent(SongsPickerUiEvent.ClearSelection) },
                onSave = { onEvent(SongsPickerUiEvent.SaveClicked) },
                onSortClick = onSortClick
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (songs.loadState.refresh is androidx.paging.LoadState.Loading) {
                Column(modifier = Modifier.fillMaxSize()) {
                    repeat(10) {
                        SongPickerItemShimmer()
                    }
                }
            } else if (songs.itemCount == 0) {
                if (uiState.query.isEmpty()) {
                    SongsPickerEmptyScreen(modifier = Modifier.weight(1f))
                } else {
                    SongsPickerNotFoundScreen(modifier = Modifier.weight(1f))
                }
            } else {
                val lazyListState = rememberLazyListState()
                val isScrolling = lazyListState.isScrollInProgress
                LaunchedEffect(isScrolling) {
                    if (isScrolling) {
                        focusManager.clearFocus()
                    }
                }

                LaunchedEffect(uiState.sortOrder, uiState.sortType) {
                    lazyListState.scrollToItem(0)
                }

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(
                        count = songs.itemCount,
                        key = songs.itemKey { it.id },
                        contentType = songs.itemContentType { "song" }
                    ) { index ->
                        val song = songs[index]
                        if (song != null) {
                            SongPickerItem(
                                song = song,
                                isSelected = uiState.songsSelected.contains(song.id),
                                onSelectedChange = {
                                    focusManager.clearFocus()
                                    onEvent(SongsPickerUiEvent.SongClicked(song.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}