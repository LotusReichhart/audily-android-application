package com.lotusreichhart.audily.core.model.song

/**
 * Kết quả của việc cài đặt bài hát làm nhạc chuông.
 */
/**
 * Kết quả của việc cài đặt bài hát làm nhạc chuông.
 */
sealed class RingtoneResult {
    object SUCCESS : RingtoneResult()
    object NO_PERMISSION : RingtoneResult()
    data class NEED_SCOPED_STORAGE_PERMISSION(val intentSender: Any) : RingtoneResult()
    object FAILED : RingtoneResult()
}
