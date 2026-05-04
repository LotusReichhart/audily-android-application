package com.lotusreichhart.audily.feature.home.impl

import app.cash.turbine.test
import com.lotusreichhart.audily.feature.songs.api.SongsEntry
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var songsEntry: SongsEntry
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        songsEntry = mockk()
        viewModel = HomeViewModel(songsEntry = songsEntry)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Initial state is Loading`() = runTest {
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `State becomes Success with SONGS tab`() = runTest {
        viewModel.uiState.test {
            // Skips loading if standard dispatcher handles it, or check sequence
            val firstItem = awaitItem()
            if (firstItem is HomeUiState.Loading) {
                val secondItem = awaitItem()
                assertEquals(HomeTab.Songs, (secondItem as HomeUiState.Success).selectedTab)
            } else {
                assertEquals(HomeTab.Songs, (firstItem as HomeUiState.Success).selectedTab)
            }
        }
    }
}
