package com.lotusreichhart.audily.feature.settings.impl

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.settings.api.R as apiR
import com.lotusreichhart.audily.feature.settings.impl.resource.SettingsIcons

@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    onPersonalization: () -> Unit,
    onAudio: () -> Unit,
    onLibrary: () -> Unit,
    onLanguage: () -> Unit,
    onLyricsNetwork: () -> Unit,
    onHelp: () -> Unit,
    onAbout: () -> Unit
) {
    val dimensions = LocalDimensions.current

    AudilyScaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensions.paddingMedium,
                    )
                    .padding(bottom = dimensions.paddingSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(apiR.string.feature_settings_api_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }
                item {
                    SettingItem(
                        iconRes = SettingsIcons.Palette,
                        titleRes = R.string.feature_settings_impl_personalization_title,
                        descRes = R.string.feature_settings_impl_personalization_description,
                        onClick = onPersonalization
                    )
                }
                item {
                    SettingItem(
                        iconRes = SettingsIcons.Speaker,
                        titleRes = R.string.feature_settings_impl_audio_title,
                        descRes = R.string.feature_settings_impl_audio_description,
                        onClick = onAudio
                    )
                }
                item {
                    SettingItem(
                        iconRes = SettingsIcons.Folders,
                        titleRes = R.string.feature_settings_impl_library_title,
                        descRes = R.string.feature_settings_impl_library_description,
                        onClick = onLibrary
                    )
                }
                item {
                    SettingItem(
                        iconRes = SettingsIcons.Translate,
                        titleRes = R.string.feature_settings_impl_language_title,
                        descRes = R.string.feature_settings_impl_language_description,
                        onClick = onLanguage
                    )
                }
                item {
                    SettingItem(
                        iconRes = SettingsIcons.Network,
                        titleRes = R.string.feature_settings_impl_network_title,
                        descRes = R.string.feature_settings_impl_network_description,
                        onClick = onLyricsNetwork
                    )
                }
                item {
                    SettingItem(
                        iconRes = SettingsIcons.Question,
                        titleRes = R.string.feature_settings_impl_question_title,
                        descRes = R.string.feature_settings_impl_question_description,
                        onClick = onHelp
                    )
                }
                item {
                    SettingItem(
                        iconRes = SettingsIcons.Info,
                        titleRes = R.string.feature_settings_impl_about_title,
                        descRes = R.string.feature_settings_impl_about_description,
                        onClick = onAbout
                    )
                }
                item { Spacer(modifier = Modifier.height(dimensions.paddingMedium)) }
            }
        }
    }
}

@Composable
private fun SettingItem(
    iconRes: Int,
    titleRes: Int,
    descRes: Int,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier
            .padding(horizontal = dimensions.paddingMedium)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
            )
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
            .padding(dimensions.paddingMedium),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(dimensions.cornerRadiusSmall)
                )
                .clip(RoundedCornerShape(dimensions.cornerRadiusSmall)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center),
                painter = painterResource(iconRes),
                contentDescription = stringResource(titleRes),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(descRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(AudilyIcons.ArrowRight),
            contentDescription = stringResource(titleRes),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}