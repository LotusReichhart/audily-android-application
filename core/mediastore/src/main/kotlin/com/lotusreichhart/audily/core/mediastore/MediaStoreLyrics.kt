package com.lotusreichhart.audily.core.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.provider.MediaStore
import com.kyant.taglib.TagLib
import com.lotusreichhart.audily.core.common.coroutines.AudilyDispatchers
import com.lotusreichhart.audily.core.common.coroutines.Dispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Xử lý đọc/ghi lời bài hát (lyrics) vật lý của file nhạc thông qua thư viện TagLib
 * và đồng bộ kết quả với MediaStore.
 */
class MediaStoreLyrics @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(AudilyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val contentResolver: ContentResolver,
) {
    companion object {
        private const val TAG = "MediaStoreLyricsEditor"
    }

    /**
     * Đọc lyrics từ tệp vật lý thông qua TagLib.
     */
    fun getSongLyrics(songId: Long): Flow<String?> = flow {
        val songUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songId
        )

        try {
            val pfd = contentResolver.openFileDescriptor(songUri, "r")
            if (pfd == null) {
                emit(null)
                return@flow
            }

            pfd.use { descriptor ->
                val fd = descriptor.detachFd()
                val metadata = TagLib.getMetadata(fd, readPictures = false)
                val lyrics = metadata?.propertyMap?.get("LYRICS")?.firstOrNull()
                emit(lyrics)
            }
        } catch (e: Exception) {
            Timber.e(e, "$TAG - Lỗi đọc lyrics cho songId: $songId")
            emit(null)
        }
    }.flowOn(ioDispatcher)

    /**
     * Ghi đè lời bài hát vào file vật lý và đồng bộ lại MediaStore.
     */
    fun updateSongLyrics(songId: Long, lyricsText: String): Flow<Boolean> = flow {
        val filePath = getFilePath(songId)
        if (filePath == null) {
            Timber.e("$TAG - Không tìm thấy file path cho songId: $songId")
            emit(false)
            return@flow
        }

        val songUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songId
        )

        // Check if the file is writable (Scoped Storage permission check)
        val file = File(filePath)
        if (!file.canWrite()) {
            Timber.w("$TAG - Không có quyền ghi vào file cho songId: $songId. Lưu lời bài hát vào database cache.")
            emit(false)
            return@flow
        }

        val tempFile = File(context.cacheDir, "editlyrics_${songId}_${System.currentTimeMillis()}.tmp")

        try {
            // 1. Copy sang file tạm
            val inputFd = contentResolver.openFileDescriptor(songUri, "r")
            if (inputFd == null) {
                emit(false)
                return@flow
            }
            inputFd.use { pfd ->
                java.io.FileInputStream(pfd.fileDescriptor).use { inputStream ->
                    tempFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }

            // 2. Chỉnh sửa tag LYRICS trên file tạm
            val tempFd = android.os.ParcelFileDescriptor.open(
                tempFile,
                android.os.ParcelFileDescriptor.MODE_READ_WRITE
            )
            tempFd.use { pfd ->
                val fd = pfd.detachFd()
                val metadata = TagLib.getMetadata(fd, readPictures = false)
                val propertyMap = metadata?.propertyMap?.let { HashMap(it) } ?: hashMapOf<String, Array<String>>()
                propertyMap["LYRICS"] = arrayOf(lyricsText)
                TagLib.savePropertyMap(fd, propertyMap)
            }

            // 3. Ghi đè file tạm ngược lại file gốc
            val outputStream = contentResolver.openOutputStream(songUri, "wt")
            if (outputStream == null) {
                emit(false)
                return@flow
            }
            outputStream.use { os ->
                tempFile.inputStream().use { inputStream ->
                    inputStream.copyTo(os)
                }
            }

            // 4. Đồng bộ MediaStore
            scanFile(filePath)
            emit(true)
        } catch (e: Exception) {
            Timber.e(e, "$TAG - Lỗi cập nhật lyrics cho songId: $songId")
            emit(false)
        } finally {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }.flowOn(ioDispatcher)

    /**
     * Lấy đường dẫn file vật lý của bài hát từ MediaStore.
     */
    private fun getFilePath(songId: Long): String? {
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        return contentResolver.query(
            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId),
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            } else null
        }
    }

    /**
     * Yêu cầu hệ thống quét lại file để cập nhật MediaStore.
     */
    private suspend fun scanFile(filePath: String) = suspendCancellableCoroutine { cont ->
        MediaScannerConnection.scanFile(
            context,
            arrayOf(filePath),
            null
        ) { _, _ ->
            if (cont.isActive) {
                cont.resume(Unit)
            }
        }
    }
}
