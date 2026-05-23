package com.lotusreichhart.audily.feature.songs.impl.menu

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.FileNotFoundException

class SongShareProvider : ContentProvider() {

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val title = uri.getQueryParameter("title") ?: "Audio"
        val songIdStr = uri.lastPathSegment ?: return null
        val songId = songIdStr.toLongOrNull() ?: return null

        val targetUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songId
        )

        var size: Long = 0
        try {
            context?.contentResolver?.query(
                targetUri,
                arrayOf(MediaStore.Audio.Media.SIZE),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val sizeIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
                    if (sizeIndex != -1) {
                        size = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore
        }

        val cols = projection ?: arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)
        val matrixCursor = MatrixCursor(cols)
        val row = arrayOfNulls<Any>(cols.size)
        cols.forEachIndexed { index, col ->
            row[index] = when (col) {
                OpenableColumns.DISPLAY_NAME -> {
                    if (!title.endsWith(".mp3", ignoreCase = true)) {
                        "$title.mp3"
                    } else {
                        title
                    }
                }
                OpenableColumns.SIZE -> size
                else -> null
            }
        }
        matrixCursor.addRow(row)
        return matrixCursor
    }

    override fun getType(uri: Uri): String {
        return "audio/*"
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val songIdStr = uri.lastPathSegment ?: throw FileNotFoundException("No song ID")
        val songId = songIdStr.toLongOrNull() ?: throw FileNotFoundException("Invalid song ID")
        val targetUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songId
        )
        return context?.contentResolver?.openFileDescriptor(targetUri, mode)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
