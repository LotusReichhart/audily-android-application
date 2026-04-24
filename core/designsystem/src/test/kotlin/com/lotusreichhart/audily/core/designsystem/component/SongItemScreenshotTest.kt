package com.lotusreichhart.audily.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.lotusreichhart.audily.core.designsystem.theme.AudilyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w360dp-h640dp-xhdpi")
class SongItemScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun songItem_Normal() {
        composeTestRule.setContent {
            AudilyTheme {
                Box(Modifier.fillMaxWidth().padding(16.dp).background(MaterialTheme.colorScheme.background)) {
                    SongItem(
                        title = "Biết Không Em",
                        artist = "Chi Dân",
                        albumArt = { /* Mock Art */ },
                        onClick = {},
                        onMenuClick = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun songItem_Playing() {
        composeTestRule.setContent {
            AudilyTheme {
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    SongItem(
                        title = "Biết Không Em",
                        artist = "Chi Dân",
                        albumArt = { /* Mock Art */ },
                        playbackStatus = SongPlaybackStatus.PLAYING,
                        onClick = {},
                        onMenuClick = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun songItem_Paused() {
        composeTestRule.setContent {
            AudilyTheme {
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    SongItem(
                        title = "Biết Không Em",
                        artist = "Chi Dân",
                        albumArt = { /* Mock Art */ },
                        playbackStatus = SongPlaybackStatus.PAUSED,
                        onClick = {},
                        onMenuClick = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun songItem_Missing() {
        composeTestRule.setContent {
            AudilyTheme {
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    SongItem(
                        title = "Biết Không Em",
                        artist = "Chi Dân",
                        albumArt = { /* Mock Art */ },
                        isMissing = true,
                        onClick = {},
                        onMenuClick = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun songItem_LongTitle_Marquee() {
        composeTestRule.setContent {
            AudilyTheme {
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    SongItem(
                        title = "Đây là một tiêu đề bài hát cực kỳ dài để kiểm tra hiệu ứng marquee chạy nối đuôi",
                        artist = "Ca sĩ nổi tiếng",
                        albumArt = { /* Mock Art */ },
                        playbackStatus = SongPlaybackStatus.PLAYING,
                        onClick = {},
                        onMenuClick = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
