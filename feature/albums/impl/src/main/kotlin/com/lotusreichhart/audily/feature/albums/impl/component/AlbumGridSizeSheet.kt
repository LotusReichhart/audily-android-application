package com.lotusreichhart.audily.feature.albums.impl.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.feature.albums.impl.R

@Composable
internal fun AlbumGridSizeSheet(
    currentGridSize: Int,
    onGridSizeSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium)
            .padding(bottom = dimensions.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = stringResource(R.string.feature_albums_impl_grid_size_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(vertical = dimensions.paddingMedium)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
        ) {
            (1..4).forEach { size ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .selectable(
                            selected = (currentGridSize == size),
                            onClick = {
                                onGridSizeSelected(size)
                                onDismiss()
                            },
                            role = Role.RadioButton,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .padding(horizontal = dimensions.paddingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (currentGridSize == size),
                        onClick = null
                    )
                    Text(
                        text = if (size == 1) {
                            stringResource(R.string.feature_albums_impl_grid_col_single)
                        } else {
                            stringResource(R.string.feature_albums_impl_grid_cols, size)
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(start = dimensions.paddingSmall)
                    )
                }
            }
        }
    }
}
