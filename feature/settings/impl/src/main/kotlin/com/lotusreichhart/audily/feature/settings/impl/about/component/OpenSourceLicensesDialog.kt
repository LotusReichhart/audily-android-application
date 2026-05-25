package com.lotusreichhart.audily.feature.settings.impl.about.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.feature.settings.impl.R

@Composable
internal fun OpenSourceLicensesDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.feature_settings_impl_about_licenses_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val licenses = listOf(
                    LicenseInfo("Jetpack Compose", "The Android Open Source Project", "Apache License 2.0"),
                    LicenseInfo("Hilt & Dagger", "Google LLC", "Apache License 2.0"),
                    LicenseInfo("Retrofit", "Square, Inc.", "Apache License 2.0"),
                    LicenseInfo("Coil", "Coil Contributors", "Apache License 2.0"),
                    LicenseInfo("Room Database", "The Android Open Source Project", "Apache License 2.0"),
                    LicenseInfo("Timber", "Jake Wharton", "Apache License 2.0")
                )

                licenses.forEach { info ->
                    Column {
                        Text(
                            text = info.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Copyright © ${info.copyright}. Licensed under ${info.license}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.feature_settings_impl_about_licenses_close),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

private data class LicenseInfo(
    val name: String,
    val copyright: String,
    val license: String
)
