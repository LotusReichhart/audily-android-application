package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.lotusreichhart.audily.core.designsystem.resource.AudilyImages

/**
 * @param artworkUri URI của ảnh bìa.
 * @param contentDescription Mô tả nội dung (cho accessibility).
 * @param modifier Modifier cho component.
 */
@Composable
fun AudilyArtwork(
    artworkUri: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val isDark = isSystemInDarkTheme()

    val backgroundColor = if (isDark) {
        MaterialTheme.colorScheme.surfaceContainerLowest
    } else {
        MaterialTheme.colorScheme.surface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (artworkUri != null) {
            AsyncImage(
                model = artworkUri,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(id = AudilyImages.MusicalNote),
                placeholder = painterResource(id = AudilyImages.MusicalNote)
            )
        } else {
            Image(
                painter = painterResource(id = AudilyImages.MusicalNote),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(0.6f),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            )
        }
    }
}
