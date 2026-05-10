package com.lotusreichhart.audily.feature.nowplaying.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lotusreichhart.audily.core.designsystem.component.AudilyButton
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions

import com.lotusreichhart.audily.feature.nowplaying.R

@SuppressLint("DefaultLocale")
@Composable
fun PlaybackParametersSheet(
    initialSpeed: Float,
    initialPitch: Float,
    onSave: (Float, Float) -> Unit
) {
    var speed by remember { mutableFloatStateOf(initialSpeed) }
    var pitch by remember { mutableFloatStateOf(initialPitch) }

    val dimensions = LocalDimensions.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = dimensions.paddingMedium)
            .padding(bottom = dimensions.paddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.feature_nowplaying_menu_speed_pitch),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(vertical = dimensions.paddingMedium)
        )

        // Speed Card
        ParameterCard(
            label = stringResource(R.string.feature_nowplaying_sheet_speed),
            valueText = "${String.format("%.1f", speed)}x",
            value = speed,
            range = 0.0f..2.0f,
            onValueChange = {
                speed = it.coerceAtLeast(0.1f)
            },
            marks = listOf(0.0f to "0.1x", 1.0f to "1.0x", 2.0f to "2.0x"),
            rulerSteps = 20
        )

        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        // Pitch Card
        ParameterCard(
            label = stringResource(R.string.feature_nowplaying_sheet_pitch),
            valueText = String.format("%.1f", pitch), // Loại bỏ x cho Pitch
            value = pitch,
            range = 0.0f..2.0f,
            onValueChange = {
                pitch = it.coerceAtLeast(0.1f)
            },
            marks = listOf(0.0f to "0.1", 1.0f to "1.0", 2.0f to "2.0"),
            rulerSteps = 20
        )

        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AudilyButton(
                text = stringResource(R.string.feature_nowplaying_sheet_reset_to_default),
                onClick = {
                    speed = 1.0f
                    pitch = 1.0f
                },
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
            AudilyButton(
                text = stringResource(R.string.feature_nowplaying_sheet_speed_save),
                onClick = {
                    onSave(
                        speed.coerceAtLeast(0.1f),
                        pitch.coerceAtLeast(0.1f)
                    )
                },
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
