package com.lotusreichhart.audily.core.playback

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.media3.common.util.BitmapLoader
import androidx.media3.common.util.UnstableApi
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.ErrorResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Một [BitmapLoader] tùy biến cho Media3 sử dụng Coil để tải ảnh bìa (bao gồm cả schema audiocover://).
 * Điều này đảm bảo hiển thị ảnh bìa trên thông báo hệ thống đồng bộ 100% với giao diện trong ứng dụng.
 */
@UnstableApi
class AudilyBitmapLoader(
    private val context: Context,
    private val imageLoader: ImageLoader,
    private val scope: CoroutineScope
) : BitmapLoader {

    override fun supportsMimeType(mimeType: String): Boolean {
        return true
    }

    override fun decodeBitmap(data: ByteArray): ListenableFuture<Bitmap> {
        val future = SettableFuture.create<Bitmap>()
        try {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            if (bitmap != null) {
                future.set(bitmap)
            } else {
                future.setException(IOException("Failed to decode bitmap bytes"))
            }
        } catch (e: Exception) {
            future.setException(e)
        }
        return future
    }

    override fun loadBitmap(uri: Uri): ListenableFuture<Bitmap> {
        val future = SettableFuture.create<Bitmap>()
        scope.launch {
            try {
                val request = ImageRequest.Builder(context)
                    .data(uri.toString())
                    .allowHardware(false) // Bắt buộc false để bitmap có thể dùng được bởi MediaSession/Notification
                    .build()
                val result = imageLoader.execute(request)
                when (result) {
                    is SuccessResult -> {
                        future.set(result.image.toBitmap())
                    }
                    is ErrorResult -> {
                        future.setException(result.throwable)
                    }
                }
            } catch (e: Exception) {
                future.setException(e)
            }
        }
        return future
    }
}
