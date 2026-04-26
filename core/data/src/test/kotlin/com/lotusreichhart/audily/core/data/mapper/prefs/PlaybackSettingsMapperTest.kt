package com.lotusreichhart.audily.core.data.mapper.prefs

import com.lotusreichhart.audily.core.datastore.RepeatModeProto
import com.lotusreichhart.audily.core.model.playback.RepeatMode
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaybackSettingsMapperTest {

    // === RepeatMode: Proto → Domain ===

    @Test
    fun `RepeatModeProto OFF maps to RepeatMode OFF`() {
        assertEquals(RepeatMode.OFF, RepeatModeProto.REPEAT_MODE_OFF.toDomain())
    }

    @Test
    fun `RepeatModeProto ONE maps to RepeatMode ONE`() {
        assertEquals(RepeatMode.ONE, RepeatModeProto.REPEAT_MODE_ONE.toDomain())
    }

    @Test
    fun `RepeatModeProto ALL maps to RepeatMode ALL`() {
        assertEquals(RepeatMode.ALL, RepeatModeProto.REPEAT_MODE_ALL.toDomain())
    }

    @Test
    fun `RepeatModeProto UNRECOGNIZED maps to RepeatMode OFF`() {
        assertEquals(RepeatMode.OFF, RepeatModeProto.UNRECOGNIZED.toDomain())
    }

    // === RepeatMode: Domain → Proto ===

    @Test
    fun `RepeatMode OFF maps to RepeatModeProto OFF`() {
        assertEquals(RepeatModeProto.REPEAT_MODE_OFF, RepeatMode.OFF.toProto())
    }

    @Test
    fun `RepeatMode ONE maps to RepeatModeProto ONE`() {
        assertEquals(RepeatModeProto.REPEAT_MODE_ONE, RepeatMode.ONE.toProto())
    }

    @Test
    fun `RepeatMode ALL maps to RepeatModeProto ALL`() {
        assertEquals(RepeatModeProto.REPEAT_MODE_ALL, RepeatMode.ALL.toProto())
    }
}
