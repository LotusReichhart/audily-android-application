package com.lotusreichhart.audily.feature.playlists.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.component.PlaylistItem
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.playlist.Playlist
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.feature.playlists.impl.component.PlaylistsAddOrUpdateSheet
import com.lotusreichhart.audily.feature.playlists.impl.component.PlaylistsEmptyScreen
import com.lotusreichhart.audily.feature.playlists.impl.component.PlaylistsLoadingScreen
import com.lotusreichhart.audily.feature.playlists.impl.component.PlaylistsSortSheet
import com.lotusreichhart.audily.feature.playlists.impl.component.PlaylistsTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun PlaylistsScreen(
    modifier: Modifier = Modifier,
    viewModel: PlaylistsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    onFavoriteClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isLoading = uiState.isLoading
    val playlists = uiState.playlists
    val favoriteCount = uiState.favoriteCount
    val favoriteArtworkUris = uiState.favoriteArtworkUris
    val sortOrder = uiState.sortOrder
    val sortType = uiState.sortType

    val sheetController = LocalAudilySheetController.current
    val sheetContainerColor = MaterialTheme.colorScheme.surfaceVariant

    LaunchedEffect(viewModel.uiEffect) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                PlaylistsUiEffect.PlaylistCreated -> {
                    sheetController.hideSheet()
                }
            }
        }
    }

    PlaylistsScreen(
        modifier = modifier,
        isLoading = isLoading,
        playlists = playlists,
        favoriteCount = favoriteCount,
        favoriteArtworkUris = favoriteArtworkUris,
        onBack = onBack,
        onSearch = onSearch,
        onSortClick = {
            sheetController.showSheet(
                content = {
                    PlaylistsSortSheet(
                        initialSortOrder = sortOrder,
                        initialSortType = sortType,
                        onSave = { order, type ->
                            viewModel.onEvent(PlaylistsUiEvent.SortOrderChanged(order))
                            viewModel.onEvent(PlaylistsUiEvent.SortTypeChanged(type))
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
                            viewModel.onEvent(PlaylistsUiEvent.CreatePlaylist(name, description))
                        }
                    )
                },
                showDragHandle = true,
                containerColor = sheetContainerColor,
                skipPartiallyExpanded = true
            )
        },
        onFavoriteClick = onFavoriteClick,
        onItemClick = { id ->
            onPlaylistClick(id)
        },
    )
}

@Composable
internal fun PlaylistsScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    playlists: List<Playlist>,
    favoriteCount: Int = 0,
    favoriteArtworkUris: List<String?> = emptyList(),
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onSortClick: () -> Unit,
    onAddClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onItemClick: (id: Long) -> Unit
) {
    val dimensions = LocalDimensions.current

    AudilyScaffold(
        topBar = {
            PlaylistsTopBar(onBack = onBack, onSearch = onSearch, onSort = onSortClick)
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Crossfade(
            targetState = isLoading,
            animationSpec = tween(durationMillis = 500),
            label = "PlaylistsLoadingCrossfade"
        ) { loading ->
            if (loading) {
                PlaylistsLoadingScreen(innerPadding = innerPadding)
            } else if (playlists.isEmpty() && favoriteCount == 0) {
                PlaylistsEmptyScreen(
                    innerPadding = innerPadding,
                    onAddClick = onAddClick
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                ) {
                    item(key = "add_playlist_card") {
                        CreatePlaylistCard(
                            onClick = onAddClick,
                            modifier = Modifier
                                .padding(top = dimensions.paddingMedium)
                                .padding(horizontal = dimensions.paddingMedium)
                        )
                        Spacer(Modifier.size(0.dp))
                    }

                    item(key = "favorite_playlist_item") {
                        AnimatedVisibility(
                            visible = favoriteCount > 0,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            PlaylistItem(
                                modifier = Modifier.padding(horizontal = dimensions.paddingMedium),
                                name = stringResource(id = R.string.feature_playlists_impl_favorite_title),
                                songCount = favoriteCount,
                                artworkUris = favoriteArtworkUris,
                                onClick = onFavoriteClick,
                                trailingContent = {
                                    Icon(
                                        painter = painterResource(id = AudilyIcons.FavoriteFill),
                                        contentDescription = null,
                                        modifier = Modifier.size(dimensions.iconSizeMedium),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        }
                    }

                    items(
                        items = playlists,
                        key = { it.id }
                    ) { item ->
                        PlaylistItem(
                            modifier = Modifier.padding(horizontal = dimensions.paddingMedium),
                            name = item.name,
                            songCount = item.songCount,
                            artworkUris = item.artworkUris,
                            onClick = {
                                onItemClick(item.id)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatePlaylistCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val stroke = remember {
        Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawWithContent {
                drawContent()
                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.6f),
                    style = stroke,
                    cornerRadius = CornerRadius(dimensions.cornerRadiusMedium.toPx())
                )
            }
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .clickable { onClick() }
            .padding(all = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
                    .background(primaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = AudilyIcons.Add),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = primaryColor
                )
            }

            Spacer(modifier = Modifier.width(dimensions.paddingMedium))

            Column {
                Text(
                    text = stringResource(id = R.string.feature_playlists_impl_add_new_playlist),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
                Text(
                    text = stringResource(id = R.string.feature_playlists_impl_add_new_playlist_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}