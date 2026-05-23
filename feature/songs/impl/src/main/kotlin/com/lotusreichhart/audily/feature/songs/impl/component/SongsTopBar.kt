package com.lotusreichhart.audily.feature.songs.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.songs.api.R

@Composable
internal fun SongsTopBar(
    onBack: () -> Unit,
    onSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(
                vertical = LocalDimensions.current.paddingSmall,
                horizontal = LocalDimensions.current.paddingMedium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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

            Text(
                text = stringResource(id = R.string.feature_songs_api_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        AudilyIconButton(
            onClick = onSearch,
            painter = painterResource(AudilyIcons.Search),
            contentDescription = "Song Search",
            iconSize = 24.dp,
            containerSize = 24.dp,
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}