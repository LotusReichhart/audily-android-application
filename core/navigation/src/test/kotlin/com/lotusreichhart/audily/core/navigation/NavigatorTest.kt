package com.lotusreichhart.audily.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NavigatorTest {

    private val startKey = mockk<NavKey>()
    private val topLevelKey1 = mockk<NavKey>()
    private val topLevelKey2 = mockk<NavKey>()
    private val subKey = mockk<NavKey>()

    // Using real NavBackStack instead of mocks to avoid warnings and ensure correct behavior
    private val topLevelStack = NavBackStack(startKey)
    private val subStack1 = NavBackStack(topLevelKey1)
    private val subStack2 = NavBackStack(topLevelKey2)

    private val state = NavigationState(
        startKey = startKey,
        topLevelStack = topLevelStack,
        subStacks = mapOf(
            startKey to subStack1,
            topLevelKey1 to subStack1,
            topLevelKey2 to subStack2
        )
    )

    private val navigator = Navigator(state)

    @Test
    fun `navigate to current top level key clears sub stack`() {
        // Ensure topLevelKey1 is current
        navigator.navigate(topLevelKey1)
        
        // Prepare subStack1 with extra keys
        subStack1.add(subKey)
        assertEquals(2, subStack1.size)
        
        // Navigating to THE SAME top level key should clear sub stack
        navigator.navigate(topLevelKey1)
        
        assertEquals(1, subStack1.size)
        assertEquals(topLevelKey1, subStack1.last())
    }

    @Test
    fun `navigate to other top level key switches top level`() {
        navigator.navigate(topLevelKey2)
        
        assertEquals(topLevelKey2, topLevelStack.last())
    }

    @Test
    fun `navigate to unknown key adds to sub stack`() {
        navigator.navigate(subKey)
        
        assertEquals(subKey, subStack1.last())
    }

    @Test
    fun `goBack on start key throws exception`() {
        // Ensure we are on start key
        val startState = NavigationState(
            startKey = startKey,
            topLevelStack = NavBackStack(startKey),
            subStacks = mapOf(startKey to NavBackStack(startKey))
        )
        val startNavigator = Navigator(startState)
        
        assertFailsWith<IllegalStateException> {
            startNavigator.goBack()
        }
    }

    @Test
    fun `goBack on top level key pops top level stack`() {
        topLevelStack.add(topLevelKey1)
        
        navigator.goBack()
        
        assertEquals(startKey, topLevelStack.last())
    }

    @Test
    fun `goBack on sub key pops sub stack`() {
        subStack1.add(subKey)
        
        navigator.goBack()
        
        assertEquals(topLevelKey1, subStack1.last())
    }

    class TestPickerNavKey(val id: Long) : SingleInstanceKey

    @Test
    fun `navigate to SingleInstanceKey removes existing keys of the same type`() {
        val pickerKey1 = TestPickerNavKey(1)
        val pickerKey2 = TestPickerNavKey(2)
        
        navigator.navigate(pickerKey1)
        navigator.navigate(pickerKey2)
        
        assertEquals(2, subStack1.size) // topLevelKey1 and pickerKey2
        assertEquals(pickerKey2, subStack1.last())
        kotlin.test.assertFalse(subStack1.contains(pickerKey1))
    }
}
