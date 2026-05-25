package com.lotusreichhart.audily.feature.settings.impl.about

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyScaffold
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.settings.impl.R
import com.lotusreichhart.audily.feature.settings.impl.BuildConfig
import com.lotusreichhart.audily.feature.settings.impl.about.component.AboutTopBar
import com.lotusreichhart.audily.feature.settings.impl.about.component.OpenSourceLicensesDialog
import com.lotusreichhart.audily.feature.settings.impl.resource.SettingsImages
import androidx.compose.ui.platform.LocalLocale
import androidx.core.net.toUri

@Composable
internal fun AboutScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val dimensions = LocalDimensions.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    // Retrieve version dynamically
    val versionName = remember(context) {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    var showLicensesDialog by remember { mutableStateOf(false) }

    AudilyScaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AboutTopBar(onBack = onBack)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensions.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // App Logo, Title, and Version
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = SettingsImages.AppLogo),
                        contentDescription = "Audily Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(32.dp))
                    )

                    Text(
                        text = "Audily",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = stringResource(
                            R.string.feature_settings_impl_about_version,
                            versionName
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Options Card
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
                ) {
                    val locale = LocalLocale.current.platformLocale.language
                    val privacyBaseUrl = BuildConfig.PRIVACY_URL
                    val termsBaseUrl = BuildConfig.TERMS_URL
                    val feedbackEmail = BuildConfig.FEEDBACK_EMAIL

                    // Privacy Policy
                    AboutOptionRow(
                        icon = Icons.Outlined.Gavel,
                        label = stringResource(R.string.feature_settings_impl_about_privacy_policy),
                        showExternalIcon = true,
                        onClick = {
                            val privacyUrl = "$privacyBaseUrl?lang=$locale"
                            try {
                                uriHandler.openUri(privacyUrl)
                            } catch (e: Exception) {
                                // No browser available or invalid URI
                            }
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = dimensions.paddingMedium),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )

                    // Terms of Service
                    AboutOptionRow(
                        icon = Icons.Outlined.Description,
                        label = stringResource(R.string.feature_settings_impl_about_terms_of_service),
                        showExternalIcon = true,
                        onClick = {
                            val termsUrl = "$termsBaseUrl?lang=$locale"
                            try {
                                uriHandler.openUri(termsUrl)
                            } catch (e: Exception) {
                                // No browser available or invalid URI
                            }
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = dimensions.paddingMedium),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )

                    // Open Source Licenses
                    AboutOptionRow(
                        icon = Icons.Outlined.Description,
                        label = stringResource(R.string.feature_settings_impl_about_open_source_licenses),
                        showExternalIcon = false,
                        onClick = { showLicensesDialog = true }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = dimensions.paddingMedium),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )

                    // Send Feedback
                    AboutOptionRow(
                        icon = Icons.Outlined.Email,
                        label = stringResource(R.string.feature_settings_impl_about_send_feedback),
                        showExternalIcon = false,
                        onClick = {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = "mailto:".toUri()
                                putExtra(Intent.EXTRA_EMAIL, arrayOf(feedbackEmail))
                                putExtra(Intent.EXTRA_SUBJECT, "Audily Feedback (v$versionName)")
                            }
                            try {
                                context.startActivity(
                                    Intent.createChooser(
                                        emailIntent,
                                        "Send Feedback"
                                    )
                                )
                            } catch (e: Exception) {
                                // No mail client installed
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        if (showLicensesDialog) {
            OpenSourceLicensesDialog(
                onDismiss = { showLicensesDialog = false }
            )
        }
    }
}

@Composable
private fun AboutOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    showExternalIcon: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        if (showExternalIcon) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = "External Link",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}