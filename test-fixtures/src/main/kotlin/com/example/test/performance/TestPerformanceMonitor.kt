package com.example.test.performance

import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Test Performance Monitor Extension
 *
 * Automatically tracks test execution times and identifies slow tests.
 * Can be used to enforce performance SLAs and optimize test suites.
 */
class TestPerformanceMonitor : BeforeEachCallback, AfterEachCallback {

    companion object {
        private val testTimes = ConcurrentHashMap<String, Long>()
        private val slowTests = ConcurrentHashMap<String, Long>()
        private const val SLOW_TEST_THRESHOLD_MS = 5000L // 5 seconds

        fun getSlowTests(): Map<String, Duration> {
            return slowTests.mapValues { it.value.milliseconds }
        }

        fun getTestTimes(): Map<String, Duration> {
            return testTimes.mapValues { it.value.milliseconds }
        }

        fun clearMetrics() {
            testTimes.clear()
            slowTests.clear()
        }
    }

    private val startTimes = ConcurrentHashMap<String, Long>()

    override fun beforeEach(context: ExtensionContext) {
        val testKey = "${context.testClass.get().simpleName}.${context.testMethod.get().name}"
        startTimes[testKey] = System.currentTimeMillis()
    }

    override fun afterEach(context: ExtensionContext) {
        val testKey = "${context.testClass.get().simpleName}.${context.testMethod.get().name}"
        val startTime = startTimes.remove(testKey) ?: return
        val duration = System.currentTimeMillis() - startTime

        testTimes[testKey] = duration

        if (duration > SLOW_TEST_THRESHOLD_MS) {
            slowTests[testKey] = duration
            println("⚠️  SLOW TEST DETECTED: $testKey took ${duration}ms")
        }
    }
}
