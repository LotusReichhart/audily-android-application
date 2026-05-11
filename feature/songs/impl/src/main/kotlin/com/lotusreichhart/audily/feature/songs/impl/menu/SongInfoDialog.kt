package com.lotusreichhart.audily.feature.songs.impl.menu

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.feature.songs.impl.R
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.sp
import com.lotusreichhart.audily.core.common.util.TimeUtils
import kotlin.math.log10
import kotlin.math.pow

@Composable
internal fun SongInfoDialog(
    song: Song,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(vertical = LocalDimensions.current.paddingMedium),
            shape = RoundedCornerShape(LocalDimensions.current.cornerRadiusMedium),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(LocalDimensions.current.paddingLarge)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.feature_songs_impl_menu_info),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoItem(
                        label = stringResource(R.string.feature_songs_impl_info_title),
                        value = song.basic.title
                    )
                    InfoItem(
                        label = stringResource(R.string.feature_songs_impl_info_artist),
                        value = song.basic.artist
                    )
                    InfoItem(
                        label = stringResource(R.string.feature_songs_impl_info_album),
                        value = song.basic.album
                    )
                    InfoItem(
                        label = stringResource(R.string.feature_songs_impl_info_duration),
                        value = TimeUtils.formatDuration(song.basic.duration)
                    )
                    InfoItem(
                        label = stringResource(R.string.feature_songs_impl_info_path),
                        value = song.basic.path
                    )

                    song.extended?.let { extended ->
                        if (extended.fileSize > 0) {
                            InfoItem(
                                label = stringResource(R.string.feature_songs_impl_info_size),
                                value = formatFileSize(extended.fileSize)
                            )
                        }
                        extended.year?.let {
                            InfoItem(
                                label = stringResource(R.string.feature_songs_impl_info_year),
                                value = it.toString()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(R.string.feature_songs_impl_info_close),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(LocalLocale.current.platformLocale),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@SuppressLint("DefaultLocale")
private fun formatFileSize(sizeBytes: Long): String {
    if (sizeBytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(sizeBytes.toDouble()) / log10(1024.0)).toInt()
    return String.format(
        "%.2f %s",
        sizeBytes / 1024.0.pow(digitGroups.toDouble()),
        units[digitGroups]
    )
}
