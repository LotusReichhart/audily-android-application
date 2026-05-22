package com.lotusreichhart.audily.feature.edittag.impl.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.resource.AudilyImages
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.edittag.impl.EditTagUiEvent
import com.lotusreichhart.audily.feature.edittag.impl.EditTagUiState
import com.lotusreichhart.audily.feature.edittag.impl.R
import com.lotusreichhart.audily.feature.edittag.impl.component.EditTagPortraitLoadingScreen
import com.lotusreichhart.audily.feature.edittag.impl.component.EditTagTopBar
import com.lotusreichhart.audily.feature.edittag.impl.component.TagField

@Composable
internal fun PortraitEditTagLayout(
    modifier: Modifier = Modifier,
    uiState: EditTagUiState,
    onBack: () -> Unit,
    onArtworkClick: () -> Unit,
    onEvent: (EditTagUiEvent) -> Unit
) {
    val dimensions = LocalDimensions.current
    val focusManager = LocalFocusManager.current

    AudilyScaffold(
        topBar = {
            EditTagTopBar(
                onBack = onBack,
                onSaveEnabled = uiState.isSaveEnabled,
                onSave = { onEvent(EditTagUiEvent.SaveClicked) }
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                EditTagPortraitLoadingScreen()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = dimensions.paddingMedium)
                        .padding(bottom = dimensions.paddingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(28.dp))

                    // Ảnh bìa bài hát (Premium card cover with white border and shadow)
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .border(
                                width = 3.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clip(RoundedCornerShape(24.dp))
                            .clickable(onClick = onArtworkClick)
                    ) {
                        when {
                            uiState.removeArtwork -> {
                                Image(
                                    painter = painterResource(id = AudilyImages.MusicalNote),
                                    contentDescription = "No Artwork",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            uiState.artworkBytes != null -> {
                                AsyncImage(
                                    model = uiState.artworkBytes,
                                    contentDescription = "New Artwork",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            else -> {
                                AsyncImage(
                                    model = uiState.song?.basic?.artworkUri,
                                    contentDescription = "Current Artwork",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(id = AudilyImages.MusicalNote),
                                    placeholder = painterResource(id = AudilyImages.MusicalNote)
                                )
                            }
                        }

                        // Nút overlay Camera (Trắng, Icon đen ở góc dưới bên phải)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .size(36.dp)
                                .shadow(elevation = 4.dp, shape = CircleShape)
                                .background(Color.White)
                                .clickable(onClick = onArtworkClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = AudilyIcons.Camera),
                                contentDescription = "Camera Options",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    // Các trường nhập liệu thẻ Tag
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                    ) {
                        // Title
                        TagField(
                            label = stringResource(R.string.feature_edittag_impl_label_title).uppercase(),
                            value = uiState.title,
                            onValueChange = { onEvent(EditTagUiEvent.TitleChanged(it)) },
                            placeholder = stringResource(R.string.feature_edittag_impl_label_title)
                        )

                        // Artist
                        TagField(
                            label = stringResource(R.string.feature_edittag_impl_label_artist).uppercase(),
                            value = uiState.artist,
                            onValueChange = { onEvent(EditTagUiEvent.ArtistChanged(it)) },
                            placeholder = stringResource(R.string.feature_edittag_impl_label_artist)
                        )

                        // Album & Year (Side-by-Side)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                        ) {
                            Box(modifier = Modifier.weight(0.65f)) {
                                TagField(
                                    label = stringResource(R.string.feature_edittag_impl_label_album).uppercase(),
                                    value = uiState.album,
                                    onValueChange = { onEvent(EditTagUiEvent.AlbumChanged(it)) },
                                    placeholder = stringResource(R.string.feature_edittag_impl_label_album)
                                )
                            }
                            Box(modifier = Modifier.weight(0.35f)) {
                                TagField(
                                    label = stringResource(R.string.feature_edittag_impl_label_year).uppercase(),
                                    value = uiState.year,
                                    onValueChange = { onEvent(EditTagUiEvent.YearChanged(it)) },
                                    placeholder = stringResource(R.string.feature_edittag_impl_label_year)
                                )
                            }
                        }

                        // Track Number & Genre (Side-by-Side)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                        ) {
                            Box(modifier = Modifier.weight(0.35f)) {
                                TagField(
                                    label = stringResource(R.string.feature_edittag_impl_label_track).uppercase(),
                                    value = uiState.trackNumber,
                                    onValueChange = { onEvent(EditTagUiEvent.TrackNumberChanged(it)) },
                                    placeholder = stringResource(R.string.feature_edittag_impl_label_track).uppercase()
                                )
                            }
                            Box(modifier = Modifier.weight(0.65f)) {
                                TagField(
                                    label = stringResource(R.string.feature_edittag_impl_label_genre).uppercase(),
                                    value = uiState.genre,
                                    onValueChange = { onEvent(EditTagUiEvent.GenreChanged(it)) },
                                    placeholder = stringResource(R.string.feature_edittag_impl_label_genre)
                                )
                            }
                        }

                        // Composer
                        TagField(
                            label = stringResource(R.string.feature_edittag_impl_label_composer).uppercase(),
                            value = uiState.composer,
                            onValueChange = { onEvent(EditTagUiEvent.ComposerChanged(it)) },
                            placeholder = stringResource(R.string.feature_edittag_impl_label_composer)
                        )
                    }
                }
            }

            if (uiState.isSaving) {
                Dialog(onDismissRequest = {}) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.feature_edittag_impl_saving_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val progress = uiState.saveProgress ?: 0f
                            LinearProgressIndicator(
                                progress = { progress },
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = stringResource(R.string.feature_edittag_impl_saving_msg),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}