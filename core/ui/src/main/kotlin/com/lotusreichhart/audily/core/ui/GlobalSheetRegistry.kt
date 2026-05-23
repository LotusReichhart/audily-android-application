package com.lotusreichhart.audily.core.ui

import androidx.compose.runtime.Composable

/**
 * Registry dùng để đăng ký các nội dung BottomSheet từ các feature khác nhau.
 * Giúp giải quyết vấn đề phụ thuộc vòng và giữ cho AudilyApp sạch sẽ.
 */
object GlobalSheetRegistry {
    private val registry = mutableMapOf<String, @Composable (Any?) -> Unit>()

    fun register(key: String, content: @Composable (Any?) -> Unit) {
        registry[key] = content
    }

    @Composable
    fun Render(key: String, params: Any?) {
        registry[key]?.invoke(params)
    }

    fun isRegistered(key: String): Boolean = registry.containsKey(key)
}
