package com.lotusreichhart.audily.feature.songs.impl.picker.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyIconButton
import com.lotusreichhart.audily.core.designsystem.component.AudilySearchBar
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.songs.impl.R

@Composable
internal fun SongsPickerTopBar(
    selectedCount: Int,
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onSave: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val focusManager = LocalFocusManager.current
    var isSearchExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(
                horizontal = LocalDimensions.current.paddingMedium
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedCount == 0) {
                    AudilyIconButton(
                        onClick = onBack,
                        painter = painterResource(id = AudilyIcons.ArrowLeft),
                        contentDescription = "Back",
                        containerSize = 24.dp,
                        iconSize = 24.dp,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    AudilyIconButton(
                        onClick = onClear,
                        painter = painterResource(id = AudilyIcons.Close),
                        contentDescription = "Clear Selection",
                        containerSize = 24.dp,
                        iconSize = 24.dp,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                // Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (selectedCount > 0) {
                            "$selectedCount ${stringResource(R.string.feature_songs_impl_picker_selected).lowercase()}"
                        } else {
                            stringResource(R.string.feature_songs_impl_picker_title)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
            ) {
                // Search button: slides open search bar, turns white/primary when active
                AudilyIconButton(
                    onClick = {
                        isSearchExpanded = !isSearchExpanded
                        if (!isSearchExpanded) {
                            onQueryChange("") // Reset query when collapsing
                            focusManager.clearFocus() // Close keyboard on collapse
                        }
                    },
                    painter = painterResource(id = AudilyIcons.Search),
                    contentDescription = "Search",
                    containerSize = 38.dp,
                    iconSize = 24.dp,
                    backgroundColor = if (isSearchExpanded) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        Color.Transparent
                    },
                    tint = if (isSearchExpanded) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    },
                    shape = CircleShape
                )

                // Sort button
                AudilyIconButton(
                    onClick = onSortClick,
                    painter = painterResource(id = AudilyIcons.Sort),
                    contentDescription = "Sort",
                    containerSize = 28.dp,
                    iconSize = 24.dp,
                    backgroundColor = Color.Transparent,
                    tint = MaterialTheme.colorScheme.onPrimary
                )

                // Save button
                AudilyIconButton(
                    onClick = { if (selectedCount > 0) onSave() },
                    painter = painterResource(id = AudilyIcons.Check),
                    contentDescription = "Save",
                    containerSize = 28.dp,
                    iconSize = 24.dp,
                    backgroundColor = Color.Transparent,
                    tint = if (selectedCount == 0) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                )
            }
        }

        // Sliding AudilySearchBar placed UNDER the main Row inside Column
        AnimatedVisibility(
            visible = isSearchExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = dimensions.paddingSmall)
                    .fillMaxWidth()
            ) {
                AudilySearchBar(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = stringResource(R.string.feature_songs_impl_picker_find_placeholder),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
