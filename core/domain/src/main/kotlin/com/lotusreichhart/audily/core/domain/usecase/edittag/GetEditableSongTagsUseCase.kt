package com.lotusreichhart.audily.core.domain.usecase.edittag

import com.lotusreichhart.audily.core.domain.repository.edittag.EditTagRepository
import com.lotusreichhart.audily.core.model.edittag.EditableSongTags
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEditableSongTagsUseCase @Inject constructor(
    private val editTagRepository: EditTagRepository,
) {
    operator fun invoke(id: Long): Flow<EditableSongTags?> =
        editTagRepository.getEditableSongTags(id)
}
