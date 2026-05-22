package com.lotusreichhart.audily.core.domain.usecase.edittag

import com.lotusreichhart.audily.core.domain.repository.edittag.EditTagRepository
import com.lotusreichhart.audily.core.model.edittag.EditTagStatus
import com.lotusreichhart.audily.core.model.edittag.EditableSongTags
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateSongTagsUseCase @Inject constructor(
    private val editTagRepository: EditTagRepository,
) {
    operator fun invoke(id: Long, tags: EditableSongTags): Flow<EditTagStatus> =
        editTagRepository.updateSongTags(id, tags)
}
