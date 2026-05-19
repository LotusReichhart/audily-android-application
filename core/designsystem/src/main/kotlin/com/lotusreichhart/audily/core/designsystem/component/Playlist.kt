package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.R
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    name: String,
    songCount: Int,
    artworkUris: List<String?>,
    onClick: () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaylistGridCover(
            artworkUris = artworkUris,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(dimensions.cornerRadiusSmall))
        )

        Spacer(modifier = Modifier.width(dimensions.paddingSmall))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$songCount ${stringResource(R.string.core_designsystem_songs)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (trailingContent != null) {
            trailingContent()
        }
    }
}

@Composable
fun PlaylistGridCover(
    artworkUris: List<String?>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val uris = artworkUris.take(4)
        when (uris.size) {
            0, 1 -> {
                AudilyArtwork(
                    artworkUri = uris.firstOrNull(),
                    modifier = Modifier.fillMaxSize(),
                    isAspectRatio = false
                )
            }

            2 -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    AudilyArtwork(
                        artworkUri = uris[0],
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        isAspectRatio = false
                    )
                    AudilyArtwork(
                        artworkUri = uris[1],
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        isAspectRatio = false
                    )
                }
            }

            3 -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.weight(1f)) {
                        AudilyArtwork(
                            artworkUri = uris[0],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            isAspectRatio = false
                        )
                        AudilyArtwork(
                            artworkUri = uris[1],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            isAspectRatio = false
                        )
                    }
                    AudilyArtwork(
                        artworkUri = uris[2],
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        isAspectRatio = false
                    )
                }
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.weight(1f)) {
                        AudilyArtwork(
                            artworkUri = uris[0],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            isAspectRatio = false
                        )
                        AudilyArtwork(
                            artworkUri = uris[1],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            isAspectRatio = false
                        )
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        AudilyArtwork(
                            artworkUri = uris[2],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            isAspectRatio = false
                        )
                        AudilyArtwork(
                            artworkUri = uris[3],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            isAspectRatio = false
                        )
                    }
                }
            }
        }
    }
}
