package com.lotusreichhart.audily.core.model.edittag

/**
 * Trạng thái tiến độ của thao tác lưu thẻ tag.
 *
 * - [Progress]: Đang xử lý, chứa giá trị tiến trình từ 0.0f đến 1.0f.
 * - [Success]: Hoàn thành thành công.
 * - [NeedScopedStoragePermission]: Cần quyền ghi file (Android 10+), chứa IntentSender để hiển thị hộp thoại hệ thống.
 * - [Failed]: Thất bại.
 */
sealed class EditTagStatus {
    data class Progress(val progress: Float) : EditTagStatus()
    data object Success : EditTagStatus()
    data class NeedScopedStoragePermission(val intentSender: Any) : EditTagStatus()
    data object Failed : EditTagStatus()
}
