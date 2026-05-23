package com.lotusreichhart.audily.core.model.song

sealed class DeleteSongResult {
    object SUCCESS : DeleteSongResult()
    data class NEED_SCOPED_STORAGE_PERMISSION(val intentSender: Any) : DeleteSongResult()
    object FAILED : DeleteSongResult()
}
