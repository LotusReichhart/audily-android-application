package com.lotusreichhart.audily.feature.home.impl.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold

@Composable
internal fun ExpandedHome(
    modifier: Modifier = Modifier,
    onNavigateToSongs: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToAlbums: () -> Unit
){
    AudilyScaffold(
        topBar = {
            HomeTopBar(
                onNavigateToSongs = onNavigateToSongs,
                onNavigateToPlaylists = onNavigateToPlaylists,
                onNavigateToAlbums = onNavigateToAlbums,
                onSearchClick = { /* TODO: Navigate to Search Feature later */ }
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Text("Home Screen")
        }
    }
}