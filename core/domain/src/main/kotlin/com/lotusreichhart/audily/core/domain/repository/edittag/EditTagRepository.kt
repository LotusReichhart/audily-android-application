package com.lotusreichhart.audily.core.domain.repository.edittag

import com.lotusreichhart.audily.core.model.edittag.EditTagStatus
import com.lotusreichhart.audily.core.model.edittag.EditableSongTags
import kotlinx.coroutines.flow.Flow

interface EditTagRepository {
    /**
     * Đọc thông tin thẻ tag hiện tại của bài hát từ file vật lý.
     */
    fun getEditableSongTags(id: Long): Flow<EditableSongTags?>

    /**
     * Ghi đè thẻ tag mới vào file vật lý và đồng bộ lại MediaStore.
     * Trả về Flow phát ra trạng thái tiến trình (Progress) cho đến khi hoàn tất (Success/Failed).
     */
    fun updateSongTags(id: Long, tags: EditableSongTags): Flow<EditTagStatus>
}
