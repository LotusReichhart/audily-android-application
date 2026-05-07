package com.lotusreichhart.audily.feature.home.impl.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.home.impl.HomeTab

@Composable
internal fun HomeTopBar(
    onNavigateToSongs: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onSearchClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(bottom = LocalDimensions.current.paddingSmall),
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LocalDimensions.current.paddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
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
                    .size(44.dp),
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
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Hàng 2: Tabs (Các nút điều hướng nhanh)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            HomeChip(
                modifier = Modifier.padding(start = LocalDimensions.current.paddingMedium),
                titleId = HomeTab.Home.title,
                selected = true,
                onClick = {}
            )
            HomeChip(
                modifier = Modifier.padding(start = LocalDimensions.current.paddingMedium),
                titleId = HomeTab.Songs.title,
                selected = false,
                onClick = onNavigateToSongs
            )
            HomeChip(
                modifier = Modifier.padding(start = LocalDimensions.current.paddingMedium),
                titleId = HomeTab.Playlists.title,
                selected = false,
                onClick = onNavigateToPlaylists
            )
            HomeChip(
                modifier = Modifier.padding(
                    horizontal = LocalDimensions.current.paddingMedium
                ),
                titleId = HomeTab.Albums.title,
                selected = false,
                onClick = onNavigateToAlbums
            )
        }
    }
}

@Composable
private fun HomeChip(
    modifier: Modifier = Modifier,
    titleId: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = if (selected) 2.dp else 0.dp,
        shadowElevation = if (selected) 2.dp else 1.dp
    ) {
        Text(
            text = stringResource(titleId),
            modifier = Modifier.padding(
                horizontal = LocalDimensions.current.paddingLarge,
                vertical = LocalDimensions.current.paddingSmall
            ),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}