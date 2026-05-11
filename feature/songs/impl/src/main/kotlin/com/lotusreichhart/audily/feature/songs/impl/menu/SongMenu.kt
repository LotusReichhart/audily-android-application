package com.lotusreichhart.audily.feature.songs.impl.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import com.lotusreichhart.audily.core.designsystem.component.ActionItem
import com.lotusreichhart.audily.core.designsystem.component.AudilyActionSheet
import com.lotusreichhart.audily.core.designsystem.component.AudilyArtwork
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.model.song.Song
import com.lotusreichhart.audily.core.ui.GlobalParams
import com.lotusreichhart.audily.core.ui.LocalAudilySheetController
import com.lotusreichhart.audily.feature.songs.impl.R
import com.lotusreichhart.audily.feature.songs.impl.resource.SongsIcons

@Composable
internal fun SongMenu(
    params: Any?,
    viewModel: SongMenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val sheetController = LocalAudilySheetController.current

    // Initialize ViewModel with params
    LaunchedEffect(params) {
        if (params is Map<*, *>) {
            val song = params[GlobalParams.PARAM_SONG] as? Song
            val caller = params[GlobalParams.PARAM_CALLER] as? String ?: ""
            val queueIds =
                (params[GlobalParams.PARAM_QUEUE_IDS] as? List<*>)?.filterIsInstance<Long>()
                    ?: emptyList()
            if (song != null) {
                viewModel.init(song, caller, queueIds)
            }
        }
    }

    uiState?.let { state ->
        val (quickActions, verticalActions) = state.options.partition { isQuickAction(it) }

        val verticalActionItems = verticalActions.map { action ->
            val (label, icon) = getActionDisplayInfo(action)
            ActionItem(
                label = stringResource(label),
                icon = icon,
                onClick = { viewModel.onEvent(SongMenuUiEvent.OnActionClick(action)) },
                isDestructive = action is SongMenuAction.Delete
            )
        }

        AudilyActionSheet(
            options = verticalActionItems,
            onDismiss = { sheetController.hideSheet() },
            header = {
                SongMenuHeader(
                    song = state.song,
                    quickActions = quickActions,
                    onActionClick = { action ->
                        viewModel.onEvent(SongMenuUiEvent.OnActionClick(action))
                        if (action !is SongMenuAction.ShowInfo) {
                            sheetController.hideSheet()
                        }
                    }
                )
            }
        )

        if (state.isShowingInfoDialog) {
            SongInfoDialog(
                song = state.song,
                onDismiss = { viewModel.onEvent(SongMenuUiEvent.OnDismissInfoDialog) }
            )
        }
    }
}

private fun isQuickAction(action: SongMenuAction): Boolean {
    return action is SongMenuAction.Play ||
            action is SongMenuAction.ResumePause ||
            action is SongMenuAction.ToggleFavorite ||
            action is SongMenuAction.ShowInfo ||
            action is SongMenuAction.EditTags ||
            action is SongMenuAction.Share
}

@Composable
private fun SongMenuHeader(
    song: Song,
    quickActions: List<SongMenuAction>,
    onActionClick: (SongMenuAction) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AudilyArtwork(
                artworkUri = song.basic.artworkUri,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = song.basic.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.basic.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            quickActions.forEach { action ->
                QuickActionItem(
                    action = action,
                    onClick = { onActionClick(action) }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun QuickActionItem(
    action: SongMenuAction,
    onClick: () -> Unit
) {
    val (labelRes, iconRes) = getActionDisplayInfo(action)
    val isFavorite = action is SongMenuAction.ToggleFavorite && action.isFavorite

    Column(
        modifier = Modifier
            .width(64.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(labelRes),
            modifier = Modifier.size(24.dp),
            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getActionDisplayInfo(action: SongMenuAction): Pair<Int, Int> {
    return when (action) {
        is SongMenuAction.Play -> R.string.feature_songs_impl_menu_play to AudilyIcons.Resume
        is SongMenuAction.PlayOnce -> R.string.feature_songs_impl_menu_play_once to AudilyIcons.Resume
        is SongMenuAction.ResumePause -> {
            if (action.isPlaying) R.string.feature_songs_impl_menu_pause to AudilyIcons.Pause
            else R.string.feature_songs_impl_menu_resume to AudilyIcons.Resume
        }

        SongMenuAction.PlayNext -> R.string.feature_songs_impl_menu_play_next to AudilyIcons.PlayNext
        SongMenuAction.AddToQueue -> R.string.feature_songs_impl_menu_add_to_queue to AudilyIcons.QueueMusic
        SongMenuAction.AddToPlaylist -> R.string.feature_songs_impl_menu_add_to_playlist to AudilyIcons.Playlist
        is SongMenuAction.ToggleFavorite -> {
            if (action.isFavorite) R.string.feature_songs_impl_menu_remove_from_favorite to AudilyIcons.FavoriteOutline
            else R.string.feature_songs_impl_menu_add_to_favorite to AudilyIcons.FavoriteFill
        }

        SongMenuAction.ShowInfo -> R.string.feature_songs_impl_menu_info to AudilyIcons.Info
        SongMenuAction.EditTags -> R.string.feature_songs_impl_menu_edit_tag to AudilyIcons.Edit
        SongMenuAction.SetRingtone -> R.string.feature_songs_impl_menu_ringtone to SongsIcons.Ringtone
        SongMenuAction.Share -> R.string.feature_songs_impl_menu_share to AudilyIcons.Share
        SongMenuAction.Delete -> R.string.feature_songs_impl_menu_delete to AudilyIcons.Delete
    }
}
