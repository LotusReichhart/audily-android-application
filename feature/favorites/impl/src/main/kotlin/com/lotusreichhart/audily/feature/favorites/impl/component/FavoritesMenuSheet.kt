package com.lotusreichhart.audily.feature.favorites.impl.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.lotusreichhart.audily.core.designsystem.component.ActionItem
import com.lotusreichhart.audily.core.designsystem.component.AudilyActionSheet
import com.lotusreichhart.audily.core.designsystem.component.AudilyBottomSheet
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.feature.favorites.impl.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoritesMenuSheet(
    onDismissRequest: () -> Unit,
    onPlayAll: () -> Unit,
    onClearAll: () -> Unit
) {
    AudilyBottomSheet(
        onDismissRequest = onDismissRequest,
        isFullScreen = false,
        showDragHandle = false,
        enableSwipeToDismiss = true,
        containerColor = Color.Transparent
    ) {
        AudilyActionSheet(
            options = listOf(
                ActionItem(
                    label = stringResource(R.string.feature_favorites_impl_menu_play_all),
                    icon = AudilyIcons.Resume,
                    onClick = {
                        onPlayAll()
                        onDismissRequest()
                    }
                ),
                ActionItem(
                    label = stringResource(R.string.feature_favorites_impl_menu_clear_all),
                    icon = AudilyIcons.Delete,
                    isDestructive = true,
                    onClick = {
                        onClearAll()
                        onDismissRequest()
                    }
                )
            ),
            onDismiss = onDismissRequest
        )
    }
}
