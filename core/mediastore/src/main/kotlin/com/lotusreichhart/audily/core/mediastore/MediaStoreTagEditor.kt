package com.lotusreichhart.audily.core.mediastore

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.provider.MediaStore
import com.kyant.taglib.Picture
import com.kyant.taglib.TagLib
import com.lotusreichhart.audily.core.common.coroutines.AudilyDispatchers
import com.lotusreichhart.audily.core.common.coroutines.Dispatcher
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreEditTagStatus
import com.lotusreichhart.audily.core.mediastore.model.MediaStoreEditableTags
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
 * Xử lý đọc/ghi thẻ tag vật lý của file nhạc thông qua thư viện TagLib
 * và đồng bộ kết quả với MediaStore.
 */
class MediaStoreTagEditor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(AudilyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val contentResolver: ContentResolver,
) {
    companion object {
        private const val TAG = "MediaStoreTagEditor"
        private const val BUFFER_SIZE = 8 * 1024 // 8KB
    }

    /**
     * Đọc thông tin thẻ tag hiện tại của bài hát từ file vật lý.
     */
    fun getEditableTags(songId: Long): Flow<MediaStoreEditableTags?> = flow {
        val songUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songId
        )

        try {
            val pfd = contentResolver.openFileDescriptor(songUri, "r")
            if (pfd == null) {
                Timber.w("$TAG - Không mở được FileDescriptor cho songId: $songId")
                emit(null)
                return@flow
            }

            pfd.use { descriptor ->
                val fd = descriptor.detachFd()
                val metadata = TagLib.getMetadata(fd, readPictures = true)

                if (metadata == null) {
                    Timber.w("$TAG - Không đọc được metadata cho songId: $songId")
                    emit(null)
                    return@flow
                }

                val propertyMap = metadata.propertyMap
                val tags = MediaStoreEditableTags(
                    title = propertyMap["TITLE"]?.firstOrNull() ?: "",
                    artist = propertyMap["ARTIST"]?.firstOrNull() ?: "",
                    album = propertyMap["ALBUM"]?.firstOrNull() ?: "",
                    year = propertyMap["DATE"]?.firstOrNull()?.toIntOrNull(),
                    trackNumber = propertyMap["TRACKNUMBER"]?.firstOrNull()?.toIntOrNull(),
                    composer = propertyMap["COMPOSER"]?.firstOrNull(),
                    genre = propertyMap["GENRE"]?.firstOrNull(),
                    artworkBytes = metadata.pictures.firstOrNull()?.data
                )
                emit(tags)
            }
        } catch (e: Exception) {
            Timber.e(e, "$TAG - Lỗi đọc tag cho songId: $songId")
            emit(null)
        }
    }.flowOn(ioDispatcher)

    /**
     * Ghi đè thẻ tag mới vào file vật lý và đồng bộ lại MediaStore.
     * Phát ra trạng thái tiến trình qua Flow.
     *
     * Quy trình:
     * 1. Copy file gốc sang file tạm (0.0f -> 0.4f)
     * 2. Chỉnh sửa tag trên file tạm (0.4f -> 0.5f)
     * 3. Ghi đè file tạm ngược lại file gốc (0.5f -> 0.9f)
     * 4. Đồng bộ MediaStore (0.9f -> 1.0f)
     */
    fun updateSongTags(songId: Long, tags: MediaStoreEditableTags): Flow<MediaStoreEditTagStatus> = flow {
        emit(MediaStoreEditTagStatus.Progress(0.0f))

        val filePath = getFilePath(songId)
        if (filePath == null) {
            Timber.e("$TAG - Không tìm thấy file path cho songId: $songId")
            emit(MediaStoreEditTagStatus.Failed)
            return@flow
        }

        val songUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songId
        )

        // --- Giai đoạn 1: Copy file gốc sang file tạm ---
        val tempFile = File(context.cacheDir, "edittag_${songId}_${System.currentTimeMillis()}.tmp")

        try {
            val inputFd = contentResolver.openFileDescriptor(songUri, "r")
            if (inputFd == null) {
                Timber.e("$TAG - Không mở được FileDescriptor (read) cho songId: $songId")
                emit(MediaStoreEditTagStatus.Failed)
                return@flow
            }

            val totalSize = inputFd.statSize
            var bytesCopied = 0L

            inputFd.use { pfd ->
                java.io.FileInputStream(pfd.fileDescriptor).use { inputStream ->
                    tempFile.outputStream().use { outputStream ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            bytesCopied += bytesRead
                            if (totalSize > 0) {
                                val progress = (bytesCopied.toFloat() / totalSize) * 0.4f
                                emit(MediaStoreEditTagStatus.Progress(progress))
                            }
                        }
                    }
                }
            }

            emit(MediaStoreEditTagStatus.Progress(0.4f))

            // --- Giai đoạn 2: Chỉnh sửa tag trên file tạm bằng TagLib ---
            val tempFd = android.os.ParcelFileDescriptor.open(
                tempFile,
                android.os.ParcelFileDescriptor.MODE_READ_WRITE
            )

            tempFd.use { pfd ->
                val fd = pfd.detachFd()

                // Lưu property map
                val propertyMap = hashMapOf<String, Array<String>>()
                propertyMap["TITLE"] = arrayOf(tags.title)
                propertyMap["ARTIST"] = arrayOf(tags.artist)
                propertyMap["ALBUM"] = arrayOf(tags.album)

                tags.year?.let { propertyMap["DATE"] = arrayOf(it.toString()) }
                    ?: run { propertyMap["DATE"] = emptyArray() }

                tags.trackNumber?.let { propertyMap["TRACKNUMBER"] = arrayOf(it.toString()) }
                    ?: run { propertyMap["TRACKNUMBER"] = emptyArray() }

                tags.composer?.let { propertyMap["COMPOSER"] = arrayOf(it) }
                    ?: run { propertyMap["COMPOSER"] = emptyArray() }

                tags.genre?.let { propertyMap["GENRE"] = arrayOf(it) }
                    ?: run { propertyMap["GENRE"] = emptyArray() }

                TagLib.savePropertyMap(fd, propertyMap)
            }

            // Xử lý artwork - cần mở FD mới vì FD trước đã bị detach
            if (tags.removeArtwork || tags.artworkBytes != null) {
                val artworkFd = android.os.ParcelFileDescriptor.open(
                    tempFile,
                    android.os.ParcelFileDescriptor.MODE_READ_WRITE
                )
                artworkFd.use { pfd ->
                    val fd = pfd.detachFd()
                    if (tags.removeArtwork) {
                        TagLib.savePictures(fd, emptyArray())
                    } else if (tags.artworkBytes != null) {
                        val picture = Picture(
                            data = tags.artworkBytes,
                            description = "Cover",
                            pictureType = "Front Cover",
                            mimeType = "image/jpeg"
                        )
                        TagLib.savePictures(fd, arrayOf(picture))
                    }
                }
            }

            emit(MediaStoreEditTagStatus.Progress(0.5f))

            // --- Giai đoạn 3: Ghi đè file tạm ngược lại file gốc ---
            try {
                val outputStream = contentResolver.openOutputStream(songUri, "wt")
                if (outputStream == null) {
                    Timber.e("$TAG - Không mở được OutputStream cho songUri: $songUri")
                    emit(MediaStoreEditTagStatus.Failed)
                    return@flow
                }

                val tempFileSize = tempFile.length()
                var bytesWritten = 0L

                outputStream.use { os ->
                    tempFile.inputStream().use { inputStream ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            os.write(buffer, 0, bytesRead)
                            bytesWritten += bytesRead
                            if (tempFileSize > 0) {
                                val progress = 0.5f + (bytesWritten.toFloat() / tempFileSize) * 0.4f
                                emit(MediaStoreEditTagStatus.Progress(progress))
                            }
                        }
                    }
                }

                emit(MediaStoreEditTagStatus.Progress(0.9f))

            } catch (securityException: SecurityException) {
                // Xử lý Scoped Storage
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableException = securityException as? RecoverableSecurityException
                    if (recoverableException != null) {
                        emit(
                            MediaStoreEditTagStatus.NeedScopedStoragePermission(
                                recoverableException.userAction.actionIntent.intentSender
                            )
                        )
                        return@flow
                    }
                }
                Timber.e(securityException, "$TAG - SecurityException khi ghi file cho songId: $songId")
                emit(MediaStoreEditTagStatus.Failed)
                return@flow
            }

            // --- Giai đoạn 4: Đồng bộ MediaStore ---
            scanFile(filePath)
            emit(MediaStoreEditTagStatus.Progress(1.0f))
            emit(MediaStoreEditTagStatus.Success)

            Timber.i("$TAG - Đã cập nhật thành công tag cho songId: $songId")

        } catch (e: Exception) {
            Timber.e(e, "$TAG - Lỗi cập nhật tag cho songId: $songId")
            emit(MediaStoreEditTagStatus.Failed)
        } finally {
            // Dọn dẹp file tạm
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
