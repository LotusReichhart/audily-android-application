package com.lotusreichhart.audily.feature.albums.impl

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.feature.albums.impl.component.AlbumGridSizeSheet
import com.lotusreichhart.audily.feature.albums.impl.component.AlbumItem
import com.lotusreichhart.audily.feature.albums.impl.component.AlbumsEmptyScreen
import com.lotusreichhart.audily.feature.albums.impl.component.AlbumsLoadingScreen
import com.lotusreichhart.audily.feature.albums.impl.component.AlbumsSortSheet
import com.lotusreichhart.audily.feature.albums.impl.component.AlbumsTopBar

@Composable
internal fun AlbumsScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onAlbumClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetController = LocalAudilySheetController.current
    val sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant

    AlbumsScreen(
        modifier = modifier,
        uiState = uiState,
        onBack = onBack,
        onGridView = {
            sheetController.showSheet(
                content = {
                    AlbumGridSizeSheet(
                        currentGridSize = uiState.gridSize,
                        onGridSizeSelected = { size ->
                            viewModel.onEvent(AlbumsUiEvent.GridSizeChanged(size))
                        },
                        onDismiss = { sheetController.hideSheet() }
                    )
                },
                showDragHandle = true,
                containerColor = sheetContainerColor,
                skipPartiallyExpanded = true
            )
        },
        onSearch = onSearch,
        onSort = {
            sheetController.showSheet(
                content = {
                    AlbumsSortSheet(
                        initialSortOrder = uiState.sortOrder,
                        initialSortType = uiState.sortType,
                        onSave = { order, type ->
                            viewModel.onEvent(AlbumsUiEvent.SortOrderChanged(order))
                            viewModel.onEvent(AlbumsUiEvent.SortTypeChanged(type))
                        },
                        onDismiss = { sheetController.hideSheet() }
                    )
                },
                showDragHandle = true,
                containerColor = sheetContainerColor,
                skipPartiallyExpanded = true
            )
        },
        onAlbumClick = onAlbumClick
    )
}

@Composable
internal fun AlbumsScreen(
    modifier: Modifier = Modifier,
    uiState: AlbumsUiState,
    onBack: () -> Unit,
    onGridView: () -> Unit,
    onSearch: () -> Unit,
    onSort: () -> Unit,
    onAlbumClick: (Long) -> Unit,
) {
    val dimensions = LocalDimensions.current

    AudilyScaffold(
        topBar = {
            AlbumsTopBar(
                onBack = onBack,
                onSearch = onSearch,
                onViewMode = onGridView,
                onSort = onSort
            )
        }
    ) { innerPadding ->
        Crossfade(
            targetState = uiState.isLoading,
            animationSpec = tween(durationMillis = 500),
            label = "AlbumsLoadingCrossfade"
        ) { loading ->
            if (loading) {
                AlbumsLoadingScreen(
                    innerPadding = innerPadding,
                    gridSize = uiState.gridSize
                )
            } else if (uiState.albums.isEmpty()) {
                AlbumsEmptyScreen(
                    innerPadding = innerPadding
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(uiState.gridSize),
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = dimensions.paddingMedium),
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding() + dimensions.paddingMedium,
                        bottom = innerPadding.calculateBottomPadding() + dimensions.paddingMedium
                    ),
                    horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall),
                    verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                ) {
                    items(
                        items = uiState.albums,
                        key = { it.id }
                    ) { album ->
                        AlbumItem(
                            album = album,
                            gridSize = uiState.gridSize,
                            onClick = { onAlbumClick(album.id) }
                        )
                    }
                }
            }
        }
    }
}