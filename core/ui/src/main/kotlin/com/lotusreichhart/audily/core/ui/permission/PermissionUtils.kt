package com.lotusreichhart.audily.core.ui.permission

import android.Manifest
import android.os.Build

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Danh sách các quyền cần thiết để ứng dụng hoạt động (đọc nhạc từ MediaStore).
 */
fun getRequiredPermissions(): List<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}

/**
 * Mở trang cài đặt chi tiết của ứng dụng để người dùng cấp quyền thủ công.
 */
fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}
