package com.lotusreichhart.audily.feature.settings.impl.personalization

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.prefs.AppTheme
import com.lotusreichhart.audily.feature.settings.impl.R
import com.lotusreichhart.audily.feature.settings.impl.personalization.component.ColorPickerBottomSheet
import com.lotusreichhart.audily.feature.settings.impl.personalization.component.PersonalizationTopBar
import com.lotusreichhart.audily.feature.settings.impl.resource.SettingsIcons

@Composable
internal fun PersonalizationScreen(
    modifier: Modifier = Modifier,
    viewModel: PersonalizationViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PersonalizationScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@Composable
internal fun PersonalizationScreen(
    modifier: Modifier = Modifier,
    uiState: PersonalizationUiState,
    onEvent: (PersonalizationUiEvent) -> Unit,
    onBack: () -> Unit
) {
    val dimensions = LocalDimensions.current
    var showColorPicker by remember { mutableStateOf(false) }

    AudilyScaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            PersonalizationTopBar(onBack = onBack)
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

            // Feature 1: Choose Theme
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_settings_impl_personalization_theme).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(50)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(4.dp)
                    ) {
                        val options = listOf(
                            AppTheme.FOLLOW_SYSTEM to stringResource(R.string.feature_settings_impl_personalization_theme_system),
                            AppTheme.LIGHT to stringResource(R.string.feature_settings_impl_personalization_theme_light),
                            AppTheme.DARK to stringResource(R.string.feature_settings_impl_personalization_theme_dark)
                        )
                        options.forEach { (theme, label) ->
                            val selected = uiState.appTheme == theme
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(dimensions.cornerRadiusLarge))
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primary
                                        else Color.Transparent
                                    )
                                    .clickable(
                                        onClick = {
                                            onEvent(
                                                PersonalizationUiEvent.OnThemeSelected(
                                                    theme
                                                )
                                            )
                                        },
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    )
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Feature 2: Accent Color
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_settings_impl_personalization_accent_color).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    val isAccentEnabled = !uiState.dynamicColor
                    Row(
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
                            .padding(dimensions.paddingMedium)
                            .alpha(if (isAccentEnabled) 1f else 0.5f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Color options definitions
                        val accentOptions = listOf(
                            null to Color(0xFF14B8A6), // Default Teal
                            0xFF8B5CF6.toInt() to Color(0xFF8B5CF6), // Purple
                            0xFFD946EF.toInt() to Color(0xFFD946EF), // Pink
                            0xFFF43F5E.toInt() to Color(0xFFF43F5E), // Coral/Red
                            0xFFF97316.toInt() to Color(0xFFF97316)  // Orange
                        )

                        // Render regular colors
                        accentOptions.forEach { (colorVal, color) ->
                            val isSelected = uiState.accentColor == colorVal
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                        else Color.White.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable(enabled = isAccentEnabled) {
                                        onEvent(
                                            PersonalizationUiEvent.OnAccentColorSelected(
                                                colorVal
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        painter = painterResource(id = AudilyIcons.Check),
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Custom color circle
                        val isPredefined = accentOptions.any { it.first == uiState.accentColor }
                        val customColor = if (!isPredefined && uiState.accentColor != null) {
                            Color(uiState.accentColor)
                        } else {
                            null
                        }

                        val isCustomSelected = !isPredefined && uiState.accentColor != null

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .border(
                                    width = if (isCustomSelected) 3.dp else 1.dp,
                                    color = if (isCustomSelected) MaterialTheme.colorScheme.primary
                                    else Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = if (customColor == null) {
                                        Brush.sweepGradient(
                                            colors = listOf(
                                                Color.Red, Color.Yellow, Color.Green,
                                                Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                                            )
                                        )
                                    } else {
                                        Brush.linearGradient(listOf(customColor, customColor))
                                    }
                                )
                                .clickable(enabled = isAccentEnabled) {
                                    showColorPicker = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCustomSelected) {
                                Icon(
                                    painter = painterResource(id = AudilyIcons.Check),
                                    contentDescription = "Selected Custom Color",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = SettingsIcons.Pick),
                                    contentDescription = "Custom Color Picker",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Feature 3: Glassmorphism & Blur Switch
            item {
                Row(
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
                        .padding(dimensions.paddingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.feature_settings_impl_personalization_glassmorphism),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.feature_settings_impl_personalization_glassmorphism_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = uiState.useGlassmorphism,
                        onCheckedChange = { onEvent(PersonalizationUiEvent.OnGlassmorphismToggled(it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            // Feature 4: Dynamic Color (Material You) Switch
            item {
                Row(
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
                        .padding(dimensions.paddingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.feature_settings_impl_personalization_dynamic_color),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.feature_settings_impl_personalization_dynamic_color_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = uiState.dynamicColor,
                        onCheckedChange = { onEvent(PersonalizationUiEvent.OnDynamicColorToggled(it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }

        // Custom RGB Color Picker Bottom Sheet
        if (showColorPicker) {
            ColorPickerBottomSheet(
                initialColor = uiState.accentColor,
                onColorSelected = { selectedColor ->
                    onEvent(PersonalizationUiEvent.OnAccentColorSelected(selectedColor))
                    showColorPicker = false
                },
                onDismiss = {
                    showColorPicker = false
                }
            )
        }
    }
}