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
import kotlin.math.roundToInt

import com.lotusreichhart.audily.feature.nowplaying.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun PlaybackSkipDurationSheet(
    initialSeconds: Int,
    onSave: (Int) -> Unit
) {
    var seconds by remember { mutableFloatStateOf(initialSeconds.toFloat()) }

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
            text = stringResource(R.string.feature_nowplaying_menu_skip_duration),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(vertical = dimensions.paddingMedium)
        )

        // Duration Card
        ParameterCard(
            label = stringResource(R.string.feature_nowplaying_sheet_seconds),
            valueText = "${seconds.roundToInt()}${
                stringResource(R.string.feature_nowplaying_sheet_seconds).take(
                    1
                ).lowercase()
            }",
            value = seconds,
            range = 5f..60f,
            onValueChange = { seconds = it },
            steps = 10, // (60-5)/5 - 1 = 10 steps for 11 ticks
            rulerSteps = 11,
            marks = listOf(
                5f to "5${
                    stringResource(R.string.feature_nowplaying_sheet_seconds).take(1).lowercase()
                }",
                15f to "15${
                    stringResource(R.string.feature_nowplaying_sheet_seconds).take(1).lowercase()
                }",
                30f to "30${
                    stringResource(R.string.feature_nowplaying_sheet_seconds).take(1).lowercase()
                }",
                45f to "45${
                    stringResource(R.string.feature_nowplaying_sheet_seconds).take(1).lowercase()
                }",
                60f to "60${
                    stringResource(R.string.feature_nowplaying_sheet_seconds).take(1).lowercase()
                }"
            )
        )

        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        // Quick Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(10, 15, 30).forEach { preset ->
                SuggestionChip(
                    onClick = { seconds = preset.toFloat() },
                    label = {
                        Text(
                            "${preset}${
                                stringResource(R.string.feature_nowplaying_sheet_seconds).take(
                                    1
                                ).lowercase()
                            }"
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (seconds.roundToInt() == preset)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainer,
                        labelColor = if (seconds.roundToInt() == preset)
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
        AudilyButton(
            text = stringResource(R.string.feature_nowplaying_sheet_duration_save),
            onClick = {
                onSave(seconds.roundToInt())
            },
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}
