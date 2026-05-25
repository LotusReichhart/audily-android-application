package com.lotusreichhart.audily.feature.settings.impl.personalization.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.designsystem.component.AudilyBottomSheet
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.settings.impl.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ColorPickerBottomSheet(
    initialColor: Int?,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val colorToUse = initialColor ?: 0xFF14B8A6.toInt()
    var redVal by remember { mutableFloatStateOf(((colorToUse shr 16) and 0xFF).toFloat()) }
    var greenVal by remember { mutableFloatStateOf(((colorToUse shr 8) and 0xFF).toFloat()) }
    var blueVal by remember { mutableFloatStateOf((colorToUse and 0xFF).toFloat()) }

    AudilyBottomSheet(
        onDismissRequest = onDismiss,
        showDragHandle = true,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.paddingMedium)
                .padding(bottom = dimensions.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.feature_settings_impl_personalization_custom_picker_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Selected custom color preview box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        color = Color(redVal.toInt(), greenVal.toInt(), blueVal.toInt()),
                        shape = RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium)
                    ),
                contentAlignment = Alignment.Center
            ) {
                val hexString = String.format(
                    "#%02X%02X%02X",
                    redVal.toInt(),
                    greenVal.toInt(),
                    blueVal.toInt()
                )
                val luminance = (0.299 * redVal + 0.587 * greenVal + 0.114 * blueVal) / 255.0
                val textColor = if (luminance > 0.5) Color.Black else Color.White
                Text(
                    text = hexString,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor.copy(alpha = 0.85f)
                )
            }

            // Red slider card
            ColorParameterCard(
                label = "Red",
                valueText = "${redVal.toInt()}",
                value = redVal,
                color = Color(0xFFF43F5E),
                onValueChange = { redVal = it }
            )

            // Green slider card
            ColorParameterCard(
                label = "Green",
                valueText = "${greenVal.toInt()}",
                value = greenVal,
                color = Color(0xFF10B981),
                onValueChange = { greenVal = it }
            )

            // Blue slider card
            ColorParameterCard(
                label = "Blue",
                valueText = "${blueVal.toInt()}",
                value = blueVal,
                color = Color(0xFF3B82F6),
                onValueChange = { blueVal = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = stringResource(coreR.string.core_designsystem_cancel),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        val selectedColor = (0xFF shl 24) or
                                (redVal.toInt() shl 16) or
                                (greenVal.toInt() shl 8) or
                                blueVal.toInt()
                        onColorSelected(selectedColor)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(dimensions.cornerRadiusLarge)
                ) {
                    Text(
                        text = stringResource(R.string.feature_settings_impl_personalization_custom_picker_select),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorParameterCard(
    label: String,
    valueText: String,
    value: Float,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium)
            )
            .padding(horizontal = LocalDimensions.current.paddingMedium)
            .padding(vertical = LocalDimensions.current.paddingSmall)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.titleMedium,
                    color = color,
                    fontWeight = FontWeight.Black
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = 0f..255f,
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color.White, CircleShape)
                        )
                    },
                    track = { sliderState ->
                        SliderDefaults.Track(
                            sliderState = sliderState,
                            modifier = Modifier.height(8.dp),
                            thumbTrackGapSize = 0.dp,
                            colors = SliderDefaults.colors(
                                activeTrackColor = color,
                                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        )
                    }
                )
            }
        }
    }
}
