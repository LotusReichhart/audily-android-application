package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.SurfaceDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudilyBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    isFullScreen: Boolean = false,
    showDragHandle: Boolean = true,
    containerColor: Color = SurfaceDark,
    shape: Shape = if (isFullScreen) RectangleShape else BottomSheetDefaults.ExpandedShape,
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = isFullScreen
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = if (isFullScreen) {
            modifier
                .fillMaxWidth()
                .fillMaxHeight()
        } else {
            modifier
        },
        sheetState = sheetState,
        shape = shape,
        containerColor = containerColor,
        dragHandle = if (showDragHandle) {
            {
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .size(width = 48.dp, height = 3.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onDismissRequest() }
                )
            }
        } else {
            null
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { },
                content = content
            )
        }
    )
}
