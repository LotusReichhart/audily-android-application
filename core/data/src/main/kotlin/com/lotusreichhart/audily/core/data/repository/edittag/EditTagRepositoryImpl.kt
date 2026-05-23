package com.lotusreichhart.audily.core.data.repository.edittag

import com.lotusreichhart.audily.core.data.mapper.edittag.toEditTagStatus
import com.lotusreichhart.audily.core.data.mapper.edittag.toEditableSongTags
import com.lotusreichhart.audily.core.data.mapper.edittag.toMediaStoreEditableTags
import com.lotusreichhart.audily.core.domain.repository.edittag.EditTagRepository
import com.lotusreichhart.audily.core.mediastore.MediaStoreTagEditor
import com.lotusreichhart.audily.core.model.edittag.EditTagStatus
import com.lotusreichhart.audily.core.model.edittag.EditableSongTags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class EditTagRepositoryImpl @Inject constructor(
    private val mediaStoreTagEditor: MediaStoreTagEditor,
) : EditTagRepository {

    override fun getEditableSongTags(id: Long): Flow<EditableSongTags?> {
        return mediaStoreTagEditor.getEditableTags(id).map { it?.toEditableSongTags() }
    }

    override fun updateSongTags(id: Long, tags: EditableSongTags): Flow<EditTagStatus> {
        return mediaStoreTagEditor.updateSongTags(id, tags.toMediaStoreEditableTags())
            .map { it.toEditTagStatus() }
    }
}
