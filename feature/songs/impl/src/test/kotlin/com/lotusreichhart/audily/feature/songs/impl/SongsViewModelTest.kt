package com.lotusreichhart.audily.feature.songs.impl

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import app.cash.turbine.test
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongsPagedUseCase
import com.lotusreichhart.audily.core.domain.usecase.song.GetSongsSummaryUseCase
import com.lotusreichhart.audily.core.model.song.SongSortOrder
import com.lotusreichhart.audily.core.model.song.SongsSummary
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SongsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getSongsPagedUseCase: GetSongsPagedUseCase
    private lateinit var getSongsSummaryUseCase: GetSongsSummaryUseCase
    private lateinit var viewModel: SongsViewModel
    
    private val summaryFlow = MutableStateFlow(SongsSummary(totalCount = 0, totalDuration = 0L))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSongsPagedUseCase = mockk()
        getSongsSummaryUseCase = mockk()
        
        every { getSongsPagedUseCase(any()) } returns flowOf(PagingData.empty())
        every { getSongsSummaryUseCase() } returns summaryFlow
        
        viewModel = SongsViewModel(
            savedStateHandle = SavedStateHandle(),
            getSongsPagedUseCase = getSongsPagedUseCase,
            getSongsSummaryUseCase = getSongsSummaryUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `State starts with isLoading true`() = runTest {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `State becomes isLoading false after 3 seconds`() = runTest {
        viewModel.uiState.test {
            assertTrue(awaitItem().isLoading)
            
            advanceTimeBy(3001)
            runCurrent()

            val finalState = expectMostRecentItem()
            assertFalse(finalState.isLoading)
        }
    }

    @Test
    fun `State contains correct metadata after loading`() = runTest {
        val summary = SongsSummary(totalCount = 10, totalDuration = 3600L)
        summaryFlow.value = summary
        runCurrent()
        
        viewModel.uiState.test {
            assertTrue(awaitItem().isLoading)
            
            viewModel.onEvent(SongsUiEvent.SortOrderChanged(SongSortOrder.TITLE_DESC))
            advanceTimeBy(3001)
            runCurrent()
            
            val state = expectMostRecentItem()
            assertFalse(state.isLoading)
            assertEquals(summary, state.summary)
            assertEquals(SongSortOrder.TITLE_DESC, state.sortOrder)
        }
    }

    @Test
    fun `SortOrderChanged updates state correctly`() = runTest {
        viewModel.uiState.test {
            assertTrue(awaitItem().isLoading)
            
            viewModel.onEvent(SongsUiEvent.SortOrderChanged(SongSortOrder.ARTIST_ASC))
            advanceTimeBy(3001)
            runCurrent()
            
            val lastState = expectMostRecentItem()
            assertFalse(lastState.isLoading)
            assertEquals(SongSortOrder.ARTIST_ASC, lastState.sortOrder)
        }
    }

    @Test
    fun `SongClicked updates playing state`() = runTest {
        val songId = 123L
        viewModel.onEvent(SongsUiEvent.SongClicked(songId))
        
        assertEquals(songId, viewModel.playingSongId.value)
        assertEquals(false, viewModel.isPaused.value)

        viewModel.onEvent(SongsUiEvent.SongClicked(songId))
        assertEquals(true, viewModel.isPaused.value)
    }

    @Test
    fun `Paging flow reference is stable across metadata updates`() = runTest {
        summaryFlow.value = SongsSummary(totalCount = 5)
        runCurrent()
        
        viewModel.uiState.test {
            assertTrue(awaitItem().isLoading) 
            
            advanceTimeBy(3001)
            runCurrent()
            
            val state1 = expectMostRecentItem()
            assertFalse(state1.isLoading)
            val firstFlowReference = state1.songs

            summaryFlow.value = SongsSummary(totalCount = 10)
            runCurrent()
            
            val state2 = expectMostRecentItem()
            assertEquals("Paging Flow instance should be stable", firstFlowReference, state2.songs)
            assertEquals(10, state2.summary.totalCount)
        }
    }
}
