package com.lotusreichhart.audily.core.domain.repository.playback

import com.lotusreichhart.audily.core.model.playback.PaletteColors

/**
 * Interface cho dịch vụ trích xuất màu sắc từ ảnh bìa.
 */
interface PaletteRepository {
    /**
     * Trích xuất các mã màu chủ đạo từ [artworkUri].
     * Trả về null nếu không thể tải ảnh hoặc ảnh không tồn tại.
     */
    suspend fun extractColors(artworkUri: String?): PaletteColors?
}