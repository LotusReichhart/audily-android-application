package com.lotusreichhart.audily.feature.favorites.impl.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lotusreichhart.audily.core.designsystem.component.SongItemShimmer
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

@Composable
internal fun FavoritesLoadingScreen(
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) {
            items(15) {
                SongItemShimmer()
            }
        }
    }
}
