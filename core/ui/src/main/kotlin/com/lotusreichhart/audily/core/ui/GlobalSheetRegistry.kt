package com.lotusreichhart.audily.core.ui

import androidx.compose.runtime.Composable

/**
 * Registry dùng để đăng ký các nội dung BottomSheet từ các feature khác nhau.
 * Giúp giải quyết vấn đề phụ thuộc vòng và giữ cho AudilyApp sạch sẽ.
 */
object GlobalSheetRegistry {
    private val registry = mutableMapOf<String, @Composable () -> Unit>()

    fun register(key: String, content: @Composable () -> Unit) {
        registry[key] = content
    }

    fun getContent(key: String): (@Composable () -> Unit)? {
        return registry[key]
    }
}
