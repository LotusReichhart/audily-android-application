package com.lotusreichhart.audily.feature.home.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lotusreichhart.audily.core.designsystem.component.AudilyButton
import com.lotusreichhart.audily.core.designsystem.resource.AudilyIcons
import com.lotusreichhart.audily.core.designsystem.theme.LocalDimensions
import com.lotusreichhart.audily.core.model.home.GreetingType
import com.lotusreichhart.audily.core.model.home.HomeVibe
import com.lotusreichhart.audily.feature.home.impl.HomeUiEvent
import com.lotusreichhart.audily.feature.home.impl.R

@Composable
internal fun HomeContent(
    homeVibe: HomeVibe,
    canResume: Boolean,
    onNavigateToSongs: () -> Unit,
    onEvent: (HomeUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val uiInfo = getGreetingUiInfo(homeVibe.greetingType)
    val density = LocalDensity.current

    // topPaddingPx: Chiều cao thực tế của HomeTopBar (được đo bởi Scaffold)
    val topPaddingPx = with(density) { contentPadding.calculateTopPadding().toPx() }

    // fadeLengthPx: Khoảng cách mà nội dung bắt đầu mờ dần TRƯỚC KHI chạm vào TopBar
    val fadeLengthPx = with(density) { 5.dp.toPx() }

    Box(modifier = modifier.fillMaxSize()) {
        // Background Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            uiInfo.color.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        if (homeVibe.sections.isEmpty()) {
            HomeEmptyContent(
                onNavigateToSongs = onNavigateToSongs,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()

                        // Logic mờ dần:
                        // - Bên dưới vùng (topPadding + fadeLength): Alpha = 1 (Hiện rõ)
                        // - Từ (topPadding + fadeLength) tới topPadding: Alpha giảm từ 1 về 0 (Mờ dần)
                        // - Từ topPadding trở lên (vùng của TopBar): Alpha = 0 (Biến mất hẳn)

                        val fadeStart = (topPaddingPx) / size.height
                        val fadeEnd = (topPaddingPx + fadeLengthPx) / size.height

                        drawRect(
                            brush = Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0f to Color.Transparent,
                                    fadeStart.coerceIn(0f, 1f) to Color.Transparent,
                                    fadeEnd.coerceIn(0f, 1f) to Color.Black,
                                    1f to Color.Black
                                )
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    },
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingLarge)
            ) {
                item {
                    GreetingSection(
                        greeting = stringResource(id = uiInfo.textRes),
                        greetingColor = uiInfo.color,
                        canResume = canResume,
                        onShuffleAllClick = { onEvent(HomeUiEvent.OnShuffleAll) },
                        onResumeClick = { onEvent(HomeUiEvent.OnResume) },
                        modifier = Modifier.padding(
                            top = LocalDimensions.current.paddingMedium,
                            start = LocalDimensions.current.paddingMedium,
                            end = LocalDimensions.current.paddingMedium
                        )
                    )
                }

                if (homeVibe.topPlayed.isNotEmpty()) {
                    item {
                        TopPlayedSection(
                            songs = homeVibe.topPlayed,
                            onSongClick = { id ->
                                onEvent(
                                    HomeUiEvent.OnSongClick(
                                        id,
                                        homeVibe.topPlayed
                                    )
                                )
                            }
                        )
                    }
                }

                if (homeVibe.recentHistory.isNotEmpty()) {
                    item {
                        RecentlyPlayedSection(
                            songs = homeVibe.recentHistory,
                            onSongClick = { id ->
                                onEvent(
                                    HomeUiEvent.OnSongClick(
                                        id,
                                        homeVibe.recentHistory
                                    )
                                )
                            }
                        )
                    }
                }

                if (homeVibe.discovery.isNotEmpty()) {
                    item {
                        DiscoverySection(
                            songs = homeVibe.discovery,
                            onSongClick = { id ->
                                onEvent(HomeUiEvent.OnSongClick(id, homeVibe.discovery))
                            }
                        )
                    }
                }

                if (homeVibe.recentlyAdded.isNotEmpty()) {
                    item {
                        RecentlyAddedSection(
                            songs = homeVibe.recentlyAdded,
                            onSongClick = { id ->
                                onEvent(
                                    HomeUiEvent.OnSongClick(
                                        id,
                                        homeVibe.recentlyAdded
                                    )
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(LocalDimensions.current.paddingMedium))
                }
            }
        }
    }
}

private data class GreetingUiInfo(
    val textRes: Int,
    val color: Color
)

@Composable
private fun getGreetingUiInfo(type: GreetingType): GreetingUiInfo {
    return when (type) {
        GreetingType.MORNING -> GreetingUiInfo(
            textRes = R.string.feature_home_impl_greeting_morning,
            color = Color(0xFFFFCC80)
        )

        GreetingType.AFTERNOON -> GreetingUiInfo(
            textRes = R.string.feature_home_impl_greeting_afternoon,
            color = Color(0xFF90CAF9)
        )

        GreetingType.EVENING -> GreetingUiInfo(
            textRes = R.string.feature_home_impl_greeting_evening,
            color = Color(0xFFB39DDB)
        )

        GreetingType.NIGHT -> GreetingUiInfo(
            textRes = R.string.feature_home_impl_greeting_night,
            color = Color(0xFF80CBC4)
        )
    }
}

@Composable
private fun GreetingSection(
    greeting: String,
    greetingColor: Color,
    canResume: Boolean,
    onShuffleAllClick: () -> Unit,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = greetingColor
        )

        Spacer(modifier = Modifier.height(LocalDimensions.current.paddingMedium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.paddingMedium)
        ) {
            AudilyButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.feature_home_impl_action_shuffle_all),
                leadingIcon = AudilyIcons.Shuffle,
                onClick = onShuffleAllClick,
            )

            AudilyButton(
                modifier = Modifier
                    .weight(1f),
                text = stringResource(R.string.feature_home_impl_action_resume),
                leadingIcon = AudilyIcons.Resume,
                enabled = canResume,
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.surfaceVariant,
                onClick = onResumeClick,
            )
        }
    }
}
