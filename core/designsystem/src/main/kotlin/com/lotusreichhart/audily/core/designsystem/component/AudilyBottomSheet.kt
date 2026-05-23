package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.theme.SurfaceDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudilyBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    isFullScreen: Boolean = false,
    showDragHandle: Boolean = true,
    enableSwipeToDismiss: Boolean = true,
    containerColor: Color = SurfaceDark,
    skipPartiallyExpanded: Boolean = false,
    shape: Shape = if (isFullScreen) RectangleShape else BottomSheetDefaults.ExpandedShape,
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = isFullScreen || skipPartiallyExpanded,
        confirmValueChange = {
            if (enableSwipeToDismiss) true
            else it != SheetValue.Hidden
        }
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
        contentWindowInsets = if (isFullScreen) {
            { WindowInsets(0.dp, 0.dp, 0.dp, 0.dp) }
        } else {
            { BottomSheetDefaults.windowInsets }
        },
        content = {
            val nestedScrollConnection = remember(enableSwipeToDismiss) {
                if (!enableSwipeToDismiss) {
                    object : NestedScrollConnection {
                        override fun onPostScroll(
                            consumed: Offset,
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            // Tiêu thụ toàn bộ lượng cuộn dư thừa để không truyền lên ModalBottomSheet
                            return available
                        }
                    }
                } else {
                    object : NestedScrollConnection {}
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .nestedScroll(nestedScrollConnection)
                    .pointerInput(enableSwipeToDismiss) {
                        if (!enableSwipeToDismiss) {
                            detectVerticalDragGestures { change, _ ->
                                // Bắt tất cả các thao tác vuốt dọc ở vùng trống để không truyền lên ModalBottomSheet
                                change.consume()
                            }
                        }
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { },
                content = content
            )
        }
    )
}
