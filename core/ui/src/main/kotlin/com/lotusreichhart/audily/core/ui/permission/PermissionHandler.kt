package com.lotusreichhart.audily.core.ui.permission

import android.Manifest
import android.os.Build
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
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import androidx.compose.ui.platform.LocalContext

/**
 * Thành phần xử lý việc xin quyền truy cập bộ nhớ và thông báo.
 * @param onPermissionGranted Callback được gọi khi tất cả quyền đã được cấp.
 * @param deniedContent Nội dung UI hiển thị khi quyền bị từ chối hoặc cần giải thích (Rationale).
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    onPermissionGranted: @Composable () -> Unit,
    deniedContent: @Composable (isNotification: Boolean, shouldShowRationale: Boolean, onRequestPermission: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val permissions = getRequiredPermissions()
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    // Xác định xem quyền lưu trữ đã được cấp chưa
    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val storageState = permissionState.permissions.find { it.permission == storagePermission }
    val isStorageGranted = storageState?.status?.isGranted == true

    // Xác định xem quyền thông báo đã được cấp chưa
    val notificationState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionState.permissions.find { it.permission == Manifest.permission.POST_NOTIFICATIONS }
    } else {
        null
    }
    val isNotificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        notificationState?.status?.isGranted == true
    } else {
        true
    }

    // Cả hai quyền phải được cấp thì mới được xem là đã cấp quyền thành công
    val currentUiState = when {
        isStorageGranted && isNotificationGranted -> PermissionUiState.Granted
        !isStorageGranted -> PermissionUiState.StorageDenied(storageState?.status?.shouldShowRationale == true)
        else -> PermissionUiState.NotificationDenied(notificationState?.status?.shouldShowRationale == true)
    }

    AnimatedContent(
        targetState = currentUiState,
        transitionSpec = {
            if (targetState is PermissionUiState.Granted) {
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
    ) { state ->
        when (state) {
            PermissionUiState.Granted -> {
                onPermissionGranted()
            }
            is PermissionUiState.StorageDenied -> {
                // Hiển thị giao diện xin quyền lưu trữ
                storageState?.let { permissionState ->
                    deniedContent(
                        false, // isNotification = false
                        state.shouldShowRationale
                    ) {
                        permissionState.launchPermissionRequest()
                        if (!state.shouldShowRationale) {
                            context.openAppSettings()
                        }
                    }
                }
            }
            is PermissionUiState.NotificationDenied -> {
                // Hiển thị giao diện xin quyền thông báo
                notificationState?.let { permissionState ->
                    deniedContent(
                        true, // isNotification = true
                        state.shouldShowRationale
                    ) {
                        permissionState.launchPermissionRequest()
                        if (!state.shouldShowRationale) {
                            context.openAppSettings()
                        }
                    }
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

private sealed interface PermissionUiState {
    data object Granted : PermissionUiState
    data class StorageDenied(val shouldShowRationale: Boolean) : PermissionUiState
    data class NotificationDenied(val shouldShowRationale: Boolean) : PermissionUiState
}

