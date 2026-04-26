package com.lotusreichhart.audily.core.domain.usecase.prefs

import com.lotusreichhart.audily.core.domain.repository.prefs.UserPreferencesRepository
import com.lotusreichhart.audily.core.model.prefs.UserPreferences
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetUserPreferencesUseCaseTest {

    private lateinit var repository: UserPreferencesRepository
    private lateinit var useCase: GetUserPreferencesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetUserPreferencesUseCase(repository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val expected = UserPreferences()
        every { repository.getUserPreferences() } returns flowOf(expected)

        val result = useCase().first()

        assertEquals(expected, result)
    }

    @Test
    fun `invoke delegates to repository getUserPreferences`() {
        val expectedFlow = flowOf(UserPreferences())
        every { repository.getUserPreferences() } returns expectedFlow

        val actualFlow = useCase()

        assertEquals(expectedFlow, actualFlow)
    }
}
