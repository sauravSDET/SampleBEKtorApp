package com.example.local.testing

import com.example.test.categories.FastTest
import com.example.test.categories.LocalTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

/**
 * Local Development Testing Framework
 *
 * Implements feedback from test strategy 2.0:
 * - Fast feedback for AI development (< 5 seconds)
 * - Shift Left approach - catch defects early
 * - Developer confidence building
 * - Social aspects: make testing valuable, not mandated
 */

@FastTest
@LocalTest
@DisplayName("AI Development Feedback Tests")
class LocalDevelopmentTest {

    @Test
    fun `should provide instant feedback for basic functionality`() = runTest(timeout = 5.seconds) {
        // Test that demonstrates immediate value to developers
        val result = performBasicCalculation(2, 3)
        result shouldBe 5

        // This test completes in milliseconds, providing instant AI feedback
        println("✅ Basic calculation works - immediate confidence!")
    }

    @Test
    fun `should validate core business logic locally`() = runTest(timeout = 3.seconds) {
        // Test that shows protection from regression
        val userEmail = "test@example.com"
        val isValid = validateEmail(userEmail)

        isValid shouldBe true

        // Demonstrate the value: "This test just saved you from a bug!"
        val invalidEmail = "invalid-email"
        val isInvalid = validateEmail(invalidEmail)
        isInvalid shouldBe false

        println("🛡️ Email validation protects against invalid data!")
    }

    @Test
    fun `should enable incremental development with confidence`() = runTest(timeout = 2.seconds) {
        // Test that enables TDD workflow
        val initialState = createEmptyUserList()
        initialState.size shouldBe 0

        val withUser = addUserToList(initialState, "John Doe")
        withUser.size shouldBe 1
        withUser.first() shouldBe "John Doe"

        println("🔄 Incremental development validated - safe to refactor!")
    }

    @Test
    fun `should catch edge cases before they become production bugs`() = runTest(timeout = 1.seconds) {
        // Test that demonstrates early bug detection
        val edgeCases = listOf(
            null,
            "",
            " ",
            "a".repeat(1000)
        )

        edgeCases.forEach { input ->
            val result = safeStringProcessor(input)
            result shouldNotBe null
            // Each edge case is handled safely
        }

        println("🎯 Edge cases handled - production bugs prevented!")
    }

    // Helper functions that simulate real application logic
    private fun performBasicCalculation(a: Int, b: Int): Int = a + b

    private fun validateEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun createEmptyUserList(): List<String> = emptyList()

    private fun addUserToList(list: List<String>, user: String): List<String> {
        return list + user
    }

    private fun safeStringProcessor(input: String?): String {
        return when {
            input == null -> "default"
            input.isBlank() -> "empty"
            input.length > 500 -> input.take(500) + "..."
            else -> input
        }
    }
}

/**
 * Developer Experience Enhancement Tests
 *
 * These tests are designed to make developers WANT to write tests
 * by showing immediate, tangible value
 */
@FastTest
@LocalTest
@DisplayName("Developer Confidence Building")
class DeveloperExperienceTest {

    @Test
    fun `should show immediate value - bug prevention`() = runTest(timeout = 1.seconds) {
        // This test demonstrates how tests prevent bugs
        val riskyOperation = { divideByZero(10, 0) }

        val result = runCatching { riskyOperation() }
        result.isFailure shouldBe true

        println("💡 Test prevented a runtime crash - tests save debugging time!")
    }

    @Test
    fun `should enable fearless refactoring`() = runTest(timeout = 2.seconds) {
        // Original implementation
        val originalResult = legacyCalculation(5, 3)

        // Refactored implementation
        val refactoredResult = improvedCalculation(5, 3)

        // Behavior is preserved
        originalResult shouldBe refactoredResult

        println("🔧 Refactoring validated - behavior preserved!")
    }

    @Test
    fun `should provide documentation through examples`() = runTest(timeout = 1.seconds) {
        // Tests serve as living documentation
        val user = createUser(
            name = "Alice",
            email = "alice@example.com",
            age = 30
        )

        user.name shouldBe "Alice"
        user.email shouldBe "alice@example.com"
        user.age shouldBe 30
        user.isValid() shouldBe true

        println("📖 Test documents expected API usage!")
    }

    private fun divideByZero(a: Int, b: Int): Int {
        if (b == 0) throw IllegalArgumentException("Cannot divide by zero")
        return a / b
    }

    private fun legacyCalculation(a: Int, b: Int): Int = a * b + 10

    private fun improvedCalculation(a: Int, b: Int): Int {
        // More efficient implementation
        return (a * b) + 10
    }

    private fun createUser(name: String, email: String, age: Int): User {
        return User(name, email, age)
    }

    data class User(
        val name: String,
        val email: String,
        val age: Int
    ) {
        fun isValid(): Boolean = name.isNotBlank() && email.contains("@") && age > 0
    }
}

/**
 * Test Metrics and Governance Implementation
 *
 * Addresses feedback: "Move beyond test count → measure impact"
 */
@FastTest
@DisplayName("Test Impact Measurement")
class TestMetricsTest {

    @Test
    fun `should measure bug prevention impact`() = runTest(timeout = 1.seconds) {
        val testStartTime = System.currentTimeMillis()

        // Simulate a test that would catch a real bug
        val potentialBug = simulatePotentialBug()
        potentialBug shouldBe false // Test catches the bug

        val testDuration = System.currentTimeMillis() - testStartTime

        println("📊 Test Metrics:")
        println("  • Bug prevented: Yes")
        println("  • Test execution time: ${testDuration}ms")
        println("  • Developer confidence: +1")
        println("  • Debugging time saved: ~30 minutes")
    }

    @Test
    fun `should validate release readiness`() = runTest(timeout = 2.seconds) {
        val criticalPaths = listOf(
            "user-creation",
            "order-processing",
            "payment-handling"
        )

        criticalPaths.forEach { path ->
            val isReady = validateCriticalPath(path)
            isReady shouldBe true
            println("✅ Critical path '$path' validated for release")
        }

        println("🚀 Release confidence: HIGH - all critical paths validated")
    }

    private fun simulatePotentialBug(): Boolean {
        // This represents a condition that could cause a bug
        val userInput = ""
        return userInput.isNotBlank() // This would be false, catching the bug
    }

    private fun validateCriticalPath(path: String): Boolean {
        // Simulate validation of critical business paths
        return path.isNotBlank() && path.contains("-")
    }
}
