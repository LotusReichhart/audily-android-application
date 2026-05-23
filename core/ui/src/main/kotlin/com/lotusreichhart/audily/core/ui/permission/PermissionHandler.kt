package com.lotusreichhart.audily.core.ui.permission

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.ui.platform.LocalContext

/**
 * Thành phần xử lý việc xin quyền truy cập bộ nhớ.
 * @param onPermissionGranted Callback được gọi khi tất cả quyền đã được cấp.
 * @param deniedContent Nội dung UI hiển thị khi quyền bị từ chối hoặc cần giải thích (Rationale).
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    onPermissionGranted: @Composable () -> Unit,
    deniedContent: @Composable (shouldShowRationale: Boolean, onRequestPermission: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val permissions = getRequiredPermissions()
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    AnimatedContent(
        targetState = permissionState.allPermissionsGranted,
        transitionSpec = {
            if (targetState) {
                fadeIn(animationSpec = tween(500)) togetherWith
                        (slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(1000)
                        ) + fadeOut(animationSpec = tween(1000)))
            } else {
                fadeIn(animationSpec = tween(500)) togetherWith
                        fadeOut(animationSpec = tween(500))
            }
        },
        label = "PermissionTransition"
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            deniedContent(
                permissionState.shouldShowRationale
            ) {
                if (permissionState.shouldShowRationale) {
                    permissionState.launchMultiplePermissionRequest()
                } else {
                    permissionState.launchMultiplePermissionRequest()
                    context.openAppSettings()
                }
            }
        }
    }

    LaunchedEffect(permissionState) {
        if (!permissionState.allPermissionsGranted && !permissionState.shouldShowRationale) {
            permissionState.launchMultiplePermissionRequest()
        }
    }
}
