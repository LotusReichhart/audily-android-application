package com.lotusreichhart.audily.feature.home.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.icon.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.designsystem.theme.BackgroundLight // Dùng để lấy màu text đậm nếu cần

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        HomeUiState.Loading -> {
            // Hiển thị trạng thái loading nếu cần
        }

        is HomeUiState.Success -> {
            HomeScreen(
                currentTab = state.selectedTab,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                songsContent = { modifier ->
                    viewModel.songsEntry.Render(modifier)
                },
                modifier = modifier
            )
        }
    }
}

@Composable
internal fun HomeScreen(
    currentTab: HomeTab,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    songsContent: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    AudilyScaffold(
        topBar = {
            HomeTopBar(
                selectedTab = currentTab,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onSearchClick = { /* TODO: Navigate to Search Feature later */ }
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                HomeTab.SONGS -> songsContent(Modifier.fillMaxSize())
                else -> {
                    // Các tab khác đã được điều hướng đi
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    selectedTab: HomeTab,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onSearchClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = LocalDimensions.current.paddingExtraSmall),
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingSmall)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Audily",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Surface(
                modifier = Modifier
                    .padding(1.dp)
                    .size(LocalDimensions.current.iconButtonHeight),
                onClick = onSearchClick,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = AudilyIcons.Search),
                        contentDescription = "Search",
                        modifier = Modifier.size(LocalDimensions.current.iconSizeMedium),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Hàng 2: Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingSmall)
        ) {
            HomeChip(
                text = "Songs",
                selected = selectedTab == HomeTab.SONGS,
                onClick = {}
            )
            HomeChip(
                text = "Playlists",
                selected = false,
                onClick = onNavigateToPlaylists
            )
            HomeChip(
                text = "Albums",
                selected = false,
                onClick = onNavigateToAlbums
            )
        }
    }
}

@Composable
private fun HomeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(1.dp),
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = if (selected) 2.dp else 0.dp,
        shadowElevation = if (selected) 2.dp else 1.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = LocalDimensions.current.paddingLarge,
                vertical = LocalDimensions.current.paddingSmall
            ),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}
