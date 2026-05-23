package com.lotusreichhart.audily.feature.home.impl.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.R
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HomeSongCard(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    isMissing: Boolean,
    albumArt: @Composable () -> Unit,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {

    val displayTitle =
        if (isMissing) stringResource(id = R.string.core_designsystem_song_is_missing) else title
    val displayArtist =
        if (isMissing) stringResource(id = R.string.core_designsystem_song_is_missing) else artist

    Column(
        modifier = modifier
            .width(80.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
    ) {
        albumArt()

        Spacer(modifier = Modifier.height(LocalDimensions.current.paddingSmall))

        Text(
            text = displayTitle,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = displayArtist,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
