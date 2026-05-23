package com.lotusreichhart.audily.feature.search.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.search.api.SearchType
import com.lotusreichhart.audily.feature.search.impl.R
import com.lotusreichhart.audily.feature.search.impl.resource.SearchIcons

@Composable
internal fun SearchQueryEmpty(
    type: SearchType,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val messageResId = when (type) {
        SearchType.ALL -> R.string.feature_search_impl_all_query_empty
        SearchType.SONGS -> R.string.feature_search_impl_songs_query_empty
        SearchType.ALBUMS -> R.string.feature_search_impl_albums_query_empty
        SearchType.PLAYLISTS -> R.string.feature_search_impl_playlists_query_empty
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensions.paddingLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = SearchIcons.SearchList),
            contentDescription = "Query Empty",
            modifier = Modifier.size(90.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        Text(
            text = stringResource(id = messageResId),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
