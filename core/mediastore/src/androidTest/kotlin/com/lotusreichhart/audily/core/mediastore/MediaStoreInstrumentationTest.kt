package com.lotusreichhart.audily.core.mediastore

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lotusreichhart.audily.core.mediastore.MediaStoreDataSource
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaStoreInstrumentationTest {

    @Test
    fun testMediaStoreDataSourceCreation() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val contentResolver = appContext.contentResolver
        val dataSource = MediaStoreDataSource(contentResolver, Dispatchers.IO)
        assertNotNull(dataSource)
    }
}
