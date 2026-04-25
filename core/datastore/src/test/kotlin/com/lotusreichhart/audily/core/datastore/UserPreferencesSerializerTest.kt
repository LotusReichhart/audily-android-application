package com.lotusreichhart.audily.core.datastore

import androidx.datastore.core.CorruptionException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class UserPreferencesSerializerTest {

    private val serializer = UserPreferencesSerializer()

    @Test
    fun `Default value is empty proto instance`() {
        assertEquals(
            UserPreferencesProto.getDefaultInstance(),
            serializer.defaultValue
        )
    }

    @Test
    fun `Write and read back produces identical proto`() = runTest {
        val proto = UserPreferencesProto.newBuilder()
            .setUiSettings(
                UiSettingsProto.newBuilder()
                    .setAppTheme(AppThemeProto.APP_THEME_DARK)
                    .setUseAmoledBlack(true)
            )
            .setPlaybackSettings(
                PlaybackSettingsProto.newBuilder()
                    .setIsShuffleEnabled(true)
                    .setRepeatMode(RepeatModeProto.REPEAT_MODE_ALL)
                    .setPlaybackSpeed(1.5f)
                    .setLastPlayedSongId(42)
                    .setHasLastPlayedSongId(true)
            )
            .setLibrarySettings(
                LibrarySettingsProto.newBuilder()
                    .setMinAudioDuration(60_000)
                    .addExcludedFolders("/storage/Ringtones")
            )
            .build()

        val outputStream = ByteArrayOutputStream()
        serializer.writeTo(proto, outputStream)

        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        val result = serializer.readFrom(inputStream)

        assertEquals(proto, result)
    }

    @Test
    fun `Read corrupted data throws CorruptionException`() = runTest {
        val corruptedData = byteArrayOf(0x00, 0xFF.toByte(), 0x01, 0x02, 0x03)
        val inputStream = ByteArrayInputStream(corruptedData)

        assertThrows(CorruptionException::class.java) {
            kotlinx.coroutines.test.runTest {
                serializer.readFrom(inputStream)
            }
        }
    }

    @Test
    fun `Read empty stream returns default instance`() = runTest {
        val emptyStream = ByteArrayInputStream(byteArrayOf())
        val result = serializer.readFrom(emptyStream)

        assertEquals(UserPreferencesProto.getDefaultInstance(), result)
    }
}
