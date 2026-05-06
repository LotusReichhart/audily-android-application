package com.lotusreichhart.audily.core.domain.usecase.home

import com.lotusreichhart.audily.core.domain.repository.history.HistoryRepository
import com.lotusreichhart.audily.core.domain.repository.song.SongRepository
import com.lotusreichhart.audily.core.model.home.GreetingType
import com.lotusreichhart.audily.core.model.home.HomeSection
import com.lotusreichhart.audily.core.model.home.HomeSectionType
import com.lotusreichhart.audily.core.model.home.HomeVibe
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.common.SortOrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject

/**
 * UseCase tổng hợp dữ liệu cho màn hình "Home - Your Vibe".
 * Domain layer chỉ chịu trách nhiệm xác định "ngữ cảnh thời gian" (GreetingType).
 */
class GetHomeVibeUseCase @Inject constructor(
    private val songRepository: SongRepository,
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(): Flow<HomeVibe> {
        val greetingType = getGreetingType()

        val recentlyAddedFlow = songRepository.getSongIds(
            sortOrder = SongSortOrder.DATE_ADDED,
            sortType = SortOrderType.DESC
        ).map { it.take(10) }.flatMapLatest { ids ->
            songRepository.getSongs(ids)
        }.map { songs ->
            HomeSection(
                titleRes = 0,
                songs = songs,
                type = HomeSectionType.RECENTLY_ADDED
            )
        }

        val recentlyPlayedFlow = historyRepository.getRecentHistory(15).flatMapLatest { historyList ->
            if (historyList.isEmpty()) {
                getDiscoveryFlow()
            } else {
                val ids = historyList.map { it.songId }
                songRepository.getSongs(ids).map { songs ->
                    HomeSection(
                        titleRes = 0,
                        songs = songs,
                        type = HomeSectionType.RECENTLY_PLAYED
                    )
                }
            }
        }

        val heavyRotationFlow = historyRepository.getTopPlayed(10).flatMapLatest { historyList ->
            if (historyList.isEmpty()) {
                getTopPicksFlow()
            } else {
                val ids = historyList.map { it.songId }
                songRepository.getSongs(ids).map { songs ->
                    HomeSection(
                        titleRes = 0,
                        songs = songs,
                        type = HomeSectionType.HEAVY_ROTATION
                    )
                }
            }
        }

        return combine(
            recentlyAddedFlow,
            recentlyPlayedFlow,
            heavyRotationFlow
        ) { recentlyAdded, recentlyPlayed, heavyRotation ->
            HomeVibe(
                greetingType = greetingType,
                sections = listOf(recentlyPlayed, heavyRotation, recentlyAdded),
                lastPlayedSong = recentlyPlayed.songs.firstOrNull()
            )
        }
    }

    private fun getGreetingType(): GreetingType {
        val hour = LocalTime.now().hour
        return when (hour) {
            in 5..11 -> GreetingType.MORNING
            in 12..17 -> GreetingType.AFTERNOON
            in 18..21 -> GreetingType.EVENING
            else -> GreetingType.NIGHT
        }
    }

    private fun getDiscoveryFlow(): Flow<HomeSection> {
        return songRepository.getSongIds().map { it.shuffled().take(10) }.flatMapLatest { ids ->
            songRepository.getSongs(ids)
        }.map { songs ->
            HomeSection(
                titleRes = 0,
                songs = songs,
                type = HomeSectionType.DISCOVERY,
                isDiscovery = true
            )
        }
    }

    private fun getTopPicksFlow(): Flow<HomeSection> {
        return songRepository.getSongIds().map { it.shuffled().take(5) }.flatMapLatest { ids ->
            songRepository.getSongs(ids)
        }.map { songs ->
            HomeSection(
                titleRes = 0,
                songs = songs,
                type = HomeSectionType.TOP_PICKS,
                isDiscovery = true
            )
        }
    }
}
