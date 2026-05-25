package com.lotusreichhart.audily.feature.settings.impl.help

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.settings.impl.R
import com.lotusreichhart.audily.feature.settings.impl.help.component.HelpTopBar

@Composable
internal fun HelpScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val dimensions = LocalDimensions.current

    AudilyScaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HelpTopBar(onBack = onBack)
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

            item {
                Text(
                    text = stringResource(R.string.feature_settings_impl_help_faq_title).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // FAQ Items
            val faqItems = listOf(
                R.string.feature_settings_impl_help_q1 to R.string.feature_settings_impl_help_a1,
                R.string.feature_settings_impl_help_q2 to R.string.feature_settings_impl_help_a2,
                R.string.feature_settings_impl_help_q3 to R.string.feature_settings_impl_help_a3,
                R.string.feature_settings_impl_help_q4 to R.string.feature_settings_impl_help_a4
            )

            faqItems.forEach { (qRes, aRes) ->
                item {
                    FaqAccordionItem(
                        questionRes = qRes,
                        answerRes = aRes,
                        cornerRadius = dimensions.cornerRadiusMedium
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun FaqAccordionItem(
    questionRes: Int,
    answerRes: Int,
    cornerRadius: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable { expanded = !expanded }
            .padding(16.dp)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(questionRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(
                    id = if (expanded) AudilyIcons.ArrowDown else AudilyIcons.ArrowRight
                ),
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(answerRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2f
            )
        }
    }
}