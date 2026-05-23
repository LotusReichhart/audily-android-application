package com.lotusreichhart.audily.feature.playlists.impl.picker

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.ui.GlobalUiEvent
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.core.ui.LocalGlobalUiEventBus
import com.lotusreichhart.audily.core.ui.util.UiText
import com.lotusreichhart.audily.feature.playlists.impl.R
import com.lotusreichhart.audily.feature.playlists.impl.component.PlaylistsSortSheet
import com.lotusreichhart.audily.feature.playlists.impl.picker.component.PlaylistPickerItem
import com.lotusreichhart.audily.feature.playlists.impl.picker.component.PlaylistsPickerEmptyScreen
import com.lotusreichhart.audily.feature.playlists.impl.picker.component.PlaylistsPickerItemShimmer
import com.lotusreichhart.audily.feature.playlists.impl.picker.component.PlaylistsPickerNotFoundScreen
import com.lotusreichhart.audily.feature.playlists.impl.picker.component.PlaylistsPickerTopBar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.playlists.impl.component.PlaylistsAddOrUpdateSheet
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun PlaylistsPickerScreen(
    modifier: Modifier = Modifier,
    songId: Long,
    onBack: () -> Unit,
    viewModel: PlaylistsPickerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playlists = uiState.playlists
    val sheetController = LocalAudilySheetController.current
    val sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant
    val globalUiEventBus = LocalGlobalUiEventBus.current

    LaunchedEffect(songId) {
        viewModel.onEvent(PlaylistsPickerUiEvent.Init(songId))
    }

    LaunchedEffect(viewModel.uiEffect) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                PlaylistsPickerUiEffect.PlaylistsSelectedSaved -> {
                    globalUiEventBus.emit(
                        GlobalUiEvent.ShowSnackbar(
                            message = UiText.StringResource(R.string.feature_playlists_impl_add_song_to_playlists)
                        )
                    )
                    onBack()
                }

                PlaylistsPickerUiEffect.PlaylistCreated -> {
                    sheetController.hideSheet()
                }
            }
        }
    }

    PlaylistsPickerScreen(
        modifier = modifier,
        uiState = uiState,
        playlists = playlists,
        onBack = onBack,
        onEvent = viewModel::onEvent,
        onSortClick = {
            sheetController.showSheet(
                content = {
                    PlaylistsSortSheet(
                        initialSortOrder = uiState.sortOrder,
                        initialSortType = uiState.sortType,
                        onSave = { order, type ->
                            viewModel.onEvent(PlaylistsPickerUiEvent.SortOrderChanged(order))
                            viewModel.onEvent(PlaylistsPickerUiEvent.SortTypeChanged(type))
                            sheetController.hideSheet()
                        }
                    )
                },
                showDragHandle = true,
                containerColor = sheetContainerColor,
                skipPartiallyExpanded = true
            )
        },
        onAddClick = {
            sheetController.showSheet(
                content = {
                    PlaylistsAddOrUpdateSheet(
                        onDismiss = { sheetController.hideSheet() },
                        onSave = { name, description ->
                            viewModel.onEvent(
                                PlaylistsPickerUiEvent.CreatePlaylist(
                                    name,
                                    description
                                )
                            )
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PlaylistsPickerScreen(
    modifier: Modifier = Modifier,
    uiState: PlaylistsPickerUiState,
    playlists: List<Playlist>,
    onBack: () -> Unit,
    onEvent: (PlaylistsPickerUiEvent) -> Unit,
    onSortClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    AudilyScaffold(
        containerColor = Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            },
        topBar = {
            PlaylistsPickerTopBar(
                selectedCount = uiState.playlistsSelected.size,
                query = uiState.query,
                onQueryChange = { onEvent(PlaylistsPickerUiEvent.OnQueryChange(it)) },
                onBack = onBack,
                onClear = { onEvent(PlaylistsPickerUiEvent.ClearSelection) },
                onSave = { onEvent(PlaylistsPickerUiEvent.SaveClicked) },
                onSortClick = onSortClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    painter = painterResource(id = AudilyIcons.Add),
                    contentDescription = "Create Playlist"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Column(modifier = Modifier.fillMaxSize()) {
                    repeat(10) {
                        PlaylistsPickerItemShimmer()
                    }
                }
            } else if (playlists.isEmpty()) {
                if (uiState.query.isEmpty()) {
                    PlaylistsPickerEmptyScreen(modifier = Modifier.weight(1f))
                } else {
                    PlaylistsPickerNotFoundScreen(modifier = Modifier.weight(1f))
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
                    stickyHeader {
                        val allIds = playlists.map { it.id }.toSet()
                        val isAllSelected =
                            allIds.isNotEmpty() && uiState.playlistsSelected.containsAll(allIds)
                        val dimensions = LocalDimensions.current

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .clickable(
                                    onClick = {
                                        focusManager.clearFocus()
                                        onEvent(PlaylistsPickerUiEvent.SelectAll)
                                    },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Checkbox(
                                modifier = Modifier.padding(start = 2.dp),
                                checked = isAllSelected,
                                onCheckedChange = {
                                    focusManager.clearFocus()
                                    onEvent(PlaylistsPickerUiEvent.SelectAll)
                                }
                            )

                            Spacer(modifier = Modifier.width(dimensions.paddingSmall))

                            Text(
                                text = stringResource(R.string.feature_playlists_impl_picker_all),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    items(
                        items = playlists,
                        key = { it.id },
                    ) { item ->
                        PlaylistPickerItem(
                            playlist = item,
                            isSelected = uiState.playlistsSelected.contains(item.id),
                            onSelectedChange = {
                                focusManager.clearFocus()
                                onEvent(PlaylistsPickerUiEvent.PlaylistClicked(item.id))
                            }
                        )
                    }
                }
            }
        }
    }
}