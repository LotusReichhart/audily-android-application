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

import kotlin.math.roundToInt

import com.lotusreichhart.audily.core.designsystem.component.AudilyButton
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.nowplaying.R

@SuppressLint("DefaultLocale")
@Composable
fun PlaybackTimerSheet(
    initialMinutes: Int,
    onSave: (Int?) -> Unit
) {
    var minutes by remember { mutableFloatStateOf(initialMinutes.toFloat()) }

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
            text = stringResource(R.string.feature_nowplaying_menu_timber),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(vertical = dimensions.paddingMedium)
        )

        // Timer Card
        ParameterCard(
            label = stringResource(R.string.feature_nowplaying_sheet_minutes),
            valueText = if (minutes.roundToInt() == 0) stringResource(R.string.feature_nowplaying_sheet_sleep_off)
            else "${minutes.roundToInt()}${
                stringResource(R.string.feature_nowplaying_sheet_minutes).take(1).lowercase()
            }",
            value = minutes,
            range = 0f..120f,
            onValueChange = {
                minutes = it
            },
            marks = listOf(
                0f to stringResource(R.string.feature_nowplaying_sheet_minutes),
                30f to "30${
                    stringResource(R.string.feature_nowplaying_sheet_minutes).take(1).lowercase()
                }",
                60f to "60${
                    stringResource(R.string.feature_nowplaying_sheet_minutes).take(1).lowercase()
                }",
                90f to "90${
                    stringResource(R.string.feature_nowplaying_sheet_minutes).take(1).lowercase()
                }",
                120f to "120${
                    stringResource(R.string.feature_nowplaying_sheet_minutes).take(1).lowercase()
                }"
            ),
            rulerSteps = 12
        )

        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        // Quick Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(15, 30, 45, 60).forEach { preset ->
                SuggestionChip(
                    onClick = { minutes = preset.toFloat() },
                    label = {
                        Text(
                            "${preset}${
                                stringResource(R.string.feature_nowplaying_sheet_minutes).take(1)
                                    .lowercase()
                            }"
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (minutes.roundToInt() == preset)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainer,
                        labelColor = if (minutes.roundToInt() == preset)
                            MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                    ),
                    border = null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AudilyButton(
                text = stringResource(R.string.feature_nowplaying_sheet_sleep_reset),
                onClick = {
                    minutes = 0f
                },
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
            AudilyButton(
                text = stringResource(R.string.feature_nowplaying_sheet_sleep_save),
                onClick = {
                    val finalMinutes = minutes.roundToInt()
                    onSave(if (finalMinutes == 0) null else finalMinutes)
                },
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
