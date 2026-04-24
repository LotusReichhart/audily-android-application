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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SongsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getSongsPagedUseCase: GetSongsPagedUseCase
    private lateinit var getSongsSummaryUseCase: GetSongsSummaryUseCase
    private lateinit var viewModel: SongsViewModel
    
    // Sử dụng MutableStateFlow để mô phỏng dữ liệu thay đổi thực tế
    private val summaryFlow = MutableStateFlow(SongsSummary(totalCount = 0, totalDuration = 0L))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSongsPagedUseCase = mockk()
        getSongsSummaryUseCase = mockk()
        
        // Mặc định trả về flow động để test có thể nhận thay đổi
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
    fun `State starts as Loading`() = runTest {
        assertEquals(SongsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `State becomes Success after metadata is loaded`() = runTest {
        val summary = SongsSummary(totalCount = 10, totalDuration = 3600L)
        summaryFlow.value = summary

        viewModel.onEvent(SongsUiEvent.SortOrderChanged(SongSortOrder.TITLE_DESC))
        
        viewModel.uiState.test {
            assertEquals(SongsUiState.Loading, awaitItem())
            
            val state = awaitItem()
            assertTrue("Expected Success but was $state", state is SongsUiState.Success)
            val success = state as SongsUiState.Success
            assertEquals(summary, success.summary)
            assertEquals(SongSortOrder.TITLE_DESC, success.sortOrder)
        }
    }

    @Test
    fun `SortOrderChanged updates state correctly`() = runTest {
        viewModel.uiState.test {
            assertEquals(SongsUiState.Loading, awaitItem())
            
            viewModel.onEvent(SongsUiEvent.SortOrderChanged(SongSortOrder.ARTIST_ASC))
            
            val state = awaitItem()
            if (state is SongsUiState.Success) {
                assertEquals(SongSortOrder.ARTIST_ASC, state.sortOrder)
            }
        }
    }

    @Test
    fun `SongClicked updates playing state`() = runTest {
        val songId = 123L
        viewModel.onEvent(SongsUiEvent.SongClicked(songId))
        
        assertEquals(songId, viewModel.playingSongId.value)
        assertEquals(false, viewModel.isPaused.value)

        // Toggle pause
        viewModel.onEvent(SongsUiEvent.SongClicked(songId))
        assertEquals(true, viewModel.isPaused.value)
    }

    @Test
    fun `Paging flow reference is stable across metadata updates`() = runTest {
        summaryFlow.value = SongsSummary(totalCount = 5)
        
        var firstFlowReference : Any? = null
        
        viewModel.uiState.test {
            assertEquals(SongsUiState.Loading, awaitItem()) 
            
            // Trigger 1st emission
            viewModel.onEvent(SongsUiEvent.SortOrderChanged(SongSortOrder.TITLE_ASC))
            
            val state1 = awaitItem()
            assertTrue(state1 is SongsUiState.Success)
            firstFlowReference = (state1 as SongsUiState.Success).songs

            // Cập nhật giá trị flow mới (dữ liệu thay đổi nhưng SORT giữ nguyên)
            summaryFlow.value = SongsSummary(totalCount = 10)
            
            // Flow của uiState sẽ tự phát ra emission mới vì nó đang collect summaryFlow
            val state2 = awaitItem()
            assertTrue(state2 is SongsUiState.Success)
            
            // KIỂM TRA QUAN TRỌNG: Instance của Paging flow (_songs) PHẢI giống nhau 
            // để LazyPagingItems trong UI không bị reset về đầu trang.
            assertEquals("Paging Flow instance should be stable", firstFlowReference, (state2 as SongsUiState.Success).songs)
            assertEquals(10, state2.summary.totalCount)
        }
    }
}
