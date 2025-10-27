package com.example.test.base

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import io.mockk.clearAllMocks
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

/**
 * Enhanced base class for unit tests following Porter Test Strategy 2.0
 * Provides standardized setup for fast, isolated unit tests with AI-friendly patterns
 */
abstract class BaseUnitTest {

    protected val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        setUpTestSpecific()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
        tearDownTestSpecific()
    }

    // Template methods for subclasses
    protected open fun setUpTestSpecific() {}
    protected open fun tearDownTestSpecific() {}

    // Common test utilities
    protected fun runTestWithDispatcher(block: suspend () -> Unit) = runTest(testDispatcher) {
        block()
    }
}
