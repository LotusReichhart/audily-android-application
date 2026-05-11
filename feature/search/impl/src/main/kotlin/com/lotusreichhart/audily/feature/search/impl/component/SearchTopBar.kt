package com.lotusreichhart.audily.feature.search.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.component.AudilySearchBar
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.search.api.SearchType
import com.lotusreichhart.audily.feature.search.impl.R

@Composable
internal fun SearchTopBar(
    query: String,
    type: SearchType,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    val placeholderResId = when (type) {
        SearchType.ALL -> R.string.feature_search_impl_all_placeholder
        SearchType.SONGS -> R.string.feature_search_impl_songs_placeholder
        SearchType.PLAYLISTS -> R.string.feature_search_impl_playlists_placeholder
        SearchType.ALBUMS -> R.string.feature_search_impl_albums_placeholder
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(
                vertical = dimensions.paddingSmall,
                horizontal = dimensions.paddingMedium
            ),
        horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AudilyIconButton(
            onClick = onBack,
            painter = painterResource(id = AudilyIcons.ArrowLeft),
            contentDescription = "Back",
            containerSize = 24.dp,
            iconSize = 24.dp,
            tint = MaterialTheme.colorScheme.onPrimary
        )

        AudilySearchBar(
            value = query,
            onValueChange = onQueryChange,
            placeholder = stringResource(placeholderResId),
            modifier = Modifier.weight(1f),
            trailingIcon = {
                if (query.isNotEmpty()) {
                    AudilyIconButton(
                        onClick = { onQueryChange("") },
                        painter = painterResource(id = AudilyIcons.Close),
                        contentDescription = "Clear",
                        containerSize = 24.dp,
                        iconSize = 20.dp,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}