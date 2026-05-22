package com.lotusreichhart.audily.core.network

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.Uri
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import okio.Buffer
import okio.FileSystem
import timber.log.Timber

class AudioCoverFetcher(
    private val context: Context,
    private val data: Uri,
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val androidUri = data.toString().toUri()
        val songId = androidUri.host?.toLongOrNull() ?: return null
        val songUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songId
        )

        val retriever = MediaMetadataRetriever()
        try {
            context.contentResolver.openFileDescriptor(songUri, "r")?.use { pfd ->
                retriever.setDataSource(pfd.fileDescriptor)
                val pictureBytes = retriever.embeddedPicture
                if (pictureBytes != null) {
                    val buffer = Buffer().write(pictureBytes)
                    return SourceFetchResult(
                        source = ImageSource(
                            source = buffer,
                            fileSystem = FileSystem.SYSTEM
                        ),
                        mimeType = "image/*",
                        dataSource = DataSource.DISK
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "AudioCoverFetcher - Failed to extract artwork for songId: $songId")
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return null
    }

    class Factory(private val context: Context) : Fetcher.Factory<Uri> {
        override fun create(
            data: Uri,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher? {
            if (data.scheme == "audiocover") {
                return AudioCoverFetcher(context, data)
            }
            return null
        }
    }
}