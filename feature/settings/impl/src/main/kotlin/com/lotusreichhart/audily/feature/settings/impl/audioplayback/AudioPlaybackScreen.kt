package com.lotusreichhart.audily.feature.settings.impl.audioplayback

import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.settings.impl.R
import com.lotusreichhart.audily.feature.settings.impl.audioplayback.component.AudioPlaybackTopBar
import timber.log.Timber

@Composable
internal fun AudioPlaybackScreen(
    modifier: Modifier = Modifier,
    viewModel: AudioPlaybackViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AudioPlaybackScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@Composable
internal fun AudioPlaybackScreen(
    modifier: Modifier = Modifier,
    uiState: AudioPlaybackUiState,
    onEvent: (AudioPlaybackUiEvent) -> Unit,
    onBack: () -> Unit
) {
    val dimensions = LocalDimensions.current
    val context = LocalContext.current

    AudilyScaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AudioPlaybackTopBar(onBack = onBack)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Equalizer card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                                )
                            )
                        )
                        .clickable(
                            onClick = {
                                val intent =
                                    Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
                                        putExtra(
                                            AudioEffect.EXTRA_PACKAGE_NAME,
                                            context.packageName
                                        )
                                        putExtra(
                                            AudioEffect.EXTRA_CONTENT_TYPE,
                                            AudioEffect.CONTENT_TYPE_MUSIC
                                        )
                                    }
                                try {
                                    if (intent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(intent)
                                    } else {
                                        onEvent(AudioPlaybackUiEvent.OnOpenEqualizerFailed)
                                    }
                                } catch (e: Exception) {
                                    Timber.e("Equalizer error: $e")
                                    onEvent(AudioPlaybackUiEvent.OnOpenEqualizerFailed)
                                }
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                ) {
                    // Soundwave drawing background decoration
                    Canvas(
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(0.15f)
                    ) {
                        val path = Path()
                        val midY = size.height / 2 + 15.dp.toPx()
                        val width = size.width
                        val amplitude = 14.dp.toPx()

                        path.moveTo(0f, midY)
                        path.cubicTo(
                            width * 0.25f, midY - amplitude,
                            width * 0.25f, midY + amplitude,
                            width * 0.5f, midY
                        )
                        path.cubicTo(
                            width * 0.75f, midY - amplitude * 1.5f,
                            width * 0.75f, midY + amplitude * 1.5f,
                            width, midY
                        )

                        drawPath(
                            path = path,
                            color = Color.White,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Content layout
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensions.paddingMedium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Slider Equalizer Icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Color.White.copy(alpha = 0.15f),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            EqualizerIcon(tint = Color.White)
                        }

                        // Text content
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.feature_settings_impl_audio_equalizer_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.feature_settings_impl_audio_equalizer_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        // Chevron Arrow Right
                        Icon(
                            painter = painterResource(id = AudilyIcons.ArrowRight),
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Auto-Play Settings Section Header
            item {
                Text(
                    text = stringResource(R.string.feature_settings_impl_audio_autoplay_section),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            // Combined options card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
                        )
                        .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                ) {
                    // Option 1: Auto-play on headphone connect
                    SettingsToggleRow(
                        titleRes = R.string.feature_settings_impl_audio_autoplay_headphone,
                        checked = uiState.autoplayOnHeadphoneConnect,
                        onCheckedChange = {
                            onEvent(AudioPlaybackUiEvent.OnAutoplayOnHeadphoneConnectChanged(it))
                        }
                    )

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )

                    // Option 2: Auto-play on Bluetooth connect
                    SettingsToggleRow(
                        titleRes = R.string.feature_settings_impl_audio_autoplay_bluetooth,
                        checked = uiState.autoplayOnBluetoothConnect,
                        onCheckedChange = {
                            onEvent(AudioPlaybackUiEvent.OnAutoplayOnBluetoothConnectChanged(it))
                        }
                    )

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )

                    // Option 3: Audio Ducking
                    SettingsToggleRow(
                        titleRes = R.string.feature_settings_impl_audio_ducking,
                        checked = uiState.audioDucking,
                        onCheckedChange = {
                            onEvent(AudioPlaybackUiEvent.OnAudioDuckingChanged(it))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    titleRes: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onCheckedChange(!checked) },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun EqualizerIcon(
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Line 1: Slider at high position
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(20.dp)
                .background(tint.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .offset(y = (-4).dp)
                    .background(tint, CircleShape)
            )
        }
        // Line 2: Slider at low position
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(20.dp)
                .background(tint.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .offset(y = 4.dp)
                    .background(tint, CircleShape)
            )
        }
        // Line 3: Slider at middle/high position
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(20.dp)
                .background(tint.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .offset(y = (-2).dp)
                    .background(tint, CircleShape)
            )
        }
    }
}