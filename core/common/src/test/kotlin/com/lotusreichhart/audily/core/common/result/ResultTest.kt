package com.lotusreichhart.audily.core.common.result

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ResultTest {

    @Test
    fun asResult_emits_loading_and_success() = runTest {
        val flow = flowOf(1)
        val results = flow.asResult().toList()

        assertEquals(2, results.size)
        assertIs<Result.Loading>(results[0])
        assertEquals(Result.Success(1), results[1])
    }

    @Test
    fun asResult_emits_error_when_flow_fails() = runTest {
        val exception = RuntimeException("Test error")
        val flow = flow<Int> {
            throw exception
        }
        val results = flow.asResult().toList()

        assertEquals(2, results.size)
        assertIs<Result.Loading>(results[0])
        assertEquals(Result.Error(exception), results[1])
    }
}
