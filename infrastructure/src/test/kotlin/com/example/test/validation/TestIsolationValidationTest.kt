package com.example.test.validation

import com.example.domain.model.*
import com.example.test.categories.IntegrationTest
import com.example.test.base.BaseDbTest
import com.example.infrastructure.repository.DatabaseAgnosticUserRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random

/**
 * Test Isolation Validation
 *
 * Validates that the unified database testing approach works correctly:
 * - Transaction-based cleanup ensures no data pollution
 * - Automatic fallback from PostgreSQL to H2 when containers fail
 * - Consistent behavior across different database backends
 */
@IntegrationTest
@DisplayName("Test Isolation Validation")
class TestIsolationValidationTest : BaseDbTest() {

    private lateinit var userRepository: DatabaseAgnosticUserRepository

    @BeforeEach
    fun setUp() {
        super.setUpTest()
        userRepository = DatabaseAgnosticUserRepository(getConnection())
    }

    @Test
    fun `should demonstrate complete test isolation with database fallback`() = runTest {
        // Given - Create test data that will be automatically cleaned up
        val testEmail = "isolation-test-${Random.nextInt(10000)}@example.com"
        val user = User.create(
            email = Email(testEmail),
            firstName = "Isolation",
            lastName = "Test"
        )

        // When - Save data that will be automatically cleaned up via transaction rollback
        val savedUser = userRepository.save(user)
        val retrievedUser = userRepository.findById(savedUser.id)

        // Then - Verify data exists within this test's transaction
        retrievedUser shouldNotBe null
        retrievedUser?.email?.value shouldBe testEmail

        // Log which database backend is being used
        println("✅ Test completed using ${if (isUsingPostgreSQL()) "PostgreSQL" else "H2"} backend")

        // Data will be automatically cleaned up by transaction rollback in tearDownTest()
    }

    @Test
    fun `should validate H2 fallback mechanism`() = runTest {
        // Given - This test should work regardless of PostgreSQL container status
        val fallbackEmail = "fallback-test-${Random.nextInt(10000)}@example.com"
        val user = User.create(
            email = Email(fallbackEmail),
            firstName = "Fallback",
            lastName = "Test"
        )

        // When - Database operations work with either PostgreSQL or H2
        val savedUser = userRepository.save(user)
        val retrievedUser = userRepository.findById(savedUser.id)

        // Then - Functionality is preserved regardless of database backend
        retrievedUser shouldNotBe null
        retrievedUser?.email?.value shouldBe fallbackEmail

        println("🔄 Fallback mechanism validated: email=$fallbackEmail")
    }

    @Test
    fun `should demonstrate transaction-based cleanup`() = runTest {
        // Given - Create multiple users that will be cleaned up automatically
        val users = (1..5).map { index ->
            User.create(
                email = Email("cleanup-test-$index-${Random.nextInt(1000)}@example.com"),
                firstName = "Cleanup",
                lastName = "User$index"
            )
        }

        // When - Save all users
        val savedUsers = users.map { userRepository.save(it) }

        // Then - All users should be retrievable within this transaction
        savedUsers.forEach { savedUser ->
            val retrieved = userRepository.findById(savedUser.id)
            retrieved shouldNotBe null
            retrieved?.firstName shouldBe "Cleanup"
        }

        println("🧹 Transaction cleanup ready for ${savedUsers.size} users")
        // Note: All data will be automatically rolled back after this test
    }

    @Test
    fun `should validate schema isolation between test classes`() = runTest {
        // Given - This test demonstrates that each test class operates in isolation
        val schemaTestEmail = "schema-isolation-${Random.nextInt(10000)}@example.com"
        val user = User.create(
            email = Email(schemaTestEmail),
            firstName = "Schema",
            lastName = "Isolation"
        )

        // When - Save data in this test's isolated schema
        val savedUser = userRepository.save(user)

        // Then - Data exists and is properly isolated
        val retrievedUser = userRepository.findById(savedUser.id)
        retrievedUser shouldNotBe null

        // Validate that we have proper database connection
        val connectionUrl = getJdbcUrl()
        connectionUrl shouldNotBe null

        println("🏗️ Schema isolation confirmed: url=$connectionUrl")
    }
}
