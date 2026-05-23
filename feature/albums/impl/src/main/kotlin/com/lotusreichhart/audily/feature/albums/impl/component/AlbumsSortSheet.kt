package com.lotusreichhart.audily.feature.albums.impl.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lotusreichhart.audily.core.designsystem.R as coreR
import com.lotusreichhart.audily.core.designsystem.component.AudilyButton
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.common.SortOrderType
import com.lotusreichhart.audily.core.model.album.AlbumSortOrder
import com.lotusreichhart.audily.feature.albums.impl.R
import com.lotusreichhart.audily.feature.albums.impl.util.labelResId

@Composable
internal fun AlbumsSortSheet(
    initialSortOrder: AlbumSortOrder,
    initialSortType: SortOrderType,
    onSave: (AlbumSortOrder, SortOrderType) -> Unit,
    onDismiss: () -> Unit
) {
    var sortOrder by remember { mutableStateOf(initialSortOrder) }
    var sortType by remember { mutableStateOf(initialSortType) }

    val dimensions = LocalDimensions.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = dimensions.paddingMedium)
            .padding(bottom = dimensions.paddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = stringResource(R.string.feature_albums_impl_sort_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(vertical = dimensions.paddingMedium)
        )

        Column {
            Text(
                text = stringResource(coreR.string.core_designsystem_sort_order).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )
            // Sort Type Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.Start
            ) {
                SortOrderType.entries.forEach { type ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(horizontal = dimensions.paddingSmall)
                            .selectable(
                                selected = (sortType == type),
                                onClick = { sortType = type },
                                role = Role.RadioButton,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (sortType == type),
                            onClick = null
                        )
                        Text(
                            text = stringResource(type.labelResId()),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(start = dimensions.paddingExtraSmall)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(dimensions.paddingSmall))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        // Sort Order Selection
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
        ) {
            Text(
                text = stringResource(coreR.string.core_designsystem_sort_according_to).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )

            AlbumSortOrder.entries.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    rowItems.forEach { order ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .selectable(
                                    selected = (sortOrder == order),
                                    onClick = { sortOrder = order },
                                    role = Role.RadioButton,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .padding(horizontal = dimensions.paddingSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (sortOrder == order),
                                onClick = null
                            )
                            Text(
                                text = stringResource(order.labelResId()),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(start = dimensions.paddingSmall)
                            )
                        }
                    }

                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(dimensions.paddingLarge))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
        ) {
            AudilyButton(
                text = stringResource(R.string.feature_albums_impl_sort_default),
                onClick = {
                    sortOrder = AlbumSortOrder.NAME
                    sortType = SortOrderType.ASC
                },
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
            AudilyButton(
                text = stringResource(R.string.feature_albums_impl_sort_accept),
                onClick = {
                    onSave(sortOrder, sortType)
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
