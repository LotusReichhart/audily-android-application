package com.lotusreichhart.audily.core.palette

import android.content.Context
import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.lotusreichhart.audily.core.common.coroutines.AudilyDispatchers
import com.lotusreichhart.audily.core.common.coroutines.Dispatcher
import com.lotusreichhart.audily.core.domain.repository.playback.PaletteRepository
import com.lotusreichhart.audily.core.model.playback.PaletteColors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Triển khai trích xuất màu sắc bằng Android Palette API và Coil.
 */
internal class PaletteRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(AudilyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val imageLoader: ImageLoader
) : PaletteRepository {

    override suspend fun extractColors(artworkUri: String?): PaletteColors? =
        withContext(ioDispatcher) {
            if (artworkUri.isNullOrBlank()) return@withContext null

            try {
                val request = ImageRequest.Builder(context)
                    .data(artworkUri)
                    .allowHardware(false)
                    .build()

                val result = imageLoader.execute(request)
                if (result is SuccessResult) {
                    val bitmap = result.image.toBitmap()
                    calculatePalette(bitmap)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

    private fun calculatePalette(bitmap: Bitmap): PaletteColors {
        val palette = Palette.from(bitmap).generate()

        return PaletteColors(
            vibrant = palette.getVibrantColor(0),
            vibrantDark = palette.getDarkVibrantColor(0),
            dominant = palette.getDominantColor(0)
        )
    }
}
