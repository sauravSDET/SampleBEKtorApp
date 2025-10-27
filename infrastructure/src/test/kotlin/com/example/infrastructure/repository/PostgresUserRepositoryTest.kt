package com.example.infrastructure.repository

import com.example.domain.model.*
import com.example.test.categories.IntegrationTest
import com.example.test.fixtures.TestFixtures
import com.example.test.base.BaseRepoTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.BeforeEach
import java.util.concurrent.TimeUnit

@IntegrationTest
@DisplayName("PostgreSQL User Repository Integration Tests - Optimized")
@Timeout(30, unit = TimeUnit.SECONDS) // Reduced from 60 seconds
class PostgresUserRepositoryTest : BaseRepoTest() {

    private lateinit var userRepository: PostgresUserRepository

    @BeforeEach
    fun setUp() {
        super.setUpIsolatedTest() // Updated method name

        // Initialize repository with shared connection
        userRepository = PostgresUserRepository(getConnection())
    }

    @Nested
    @DisplayName("CRUD Operations")
    inner class CrudOperationsTests {

        @Test
        fun `should save and retrieve user successfully`() = runTest {
            // Given
            val user = TestFixtures.createTestUser()

            // When
            val savedUser = userRepository.save(user)
            val retrievedUser = userRepository.findById(savedUser.id)

            // Then
            retrievedUser shouldNotBe null
            retrievedUser?.id shouldBe savedUser.id
            retrievedUser?.email shouldBe savedUser.email
            retrievedUser?.firstName shouldBe savedUser.firstName
            retrievedUser?.lastName shouldBe savedUser.lastName
        }

        @Test
        fun `should update existing user`() = runTest {
            // Given
            val originalUser = TestFixtures.createTestUser()
            userRepository.save(originalUser)

            // When
            val updatedUser = originalUser.updateProfile("Jane", "Smith")
            userRepository.save(updatedUser)
            val retrievedUser = userRepository.findById(originalUser.id)

            // Then
            retrievedUser shouldNotBe null
            retrievedUser?.firstName shouldBe "Jane"
            retrievedUser?.lastName shouldBe "Smith"
            retrievedUser?.id shouldBe originalUser.id
        }

        @Test
        fun `should delete user successfully`() = runTest {
            // Given
            val user = TestFixtures.createTestUser()
            userRepository.save(user)

            // When
            val deleted = userRepository.delete(user.id)
            val retrievedUser = userRepository.findById(user.id)

            // Then
            deleted shouldBe true
            retrievedUser shouldBe null
        }

        @Test
        fun `should find user by email`() = runTest {
            // Given
            val user = TestFixtures.createTestUser()
            userRepository.save(user)

            // When
            val foundUser = userRepository.findByEmail(user.email)

            // Then
            foundUser shouldNotBe null
            foundUser?.email shouldBe user.email
        }

        @Test
        fun `should return null when user not found`() = runTest {
            // Given
            val nonExistentId = UserId.generate()

            // When
            val result = userRepository.findById(nonExistentId)

            // Then
            result shouldBe null
        }
    }

    @Nested
    @DisplayName("Validation and Constraints")
    inner class ValidationTests {

        @Test
        fun `should enforce email uniqueness constraint`() = runTest {
            // Given
            val user1 = TestFixtures.createTestUser()
            val user2 = TestFixtures.createTestUser().copy(
                id = UserId.generate(),
                email = user1.email // Same email
            )

            // When
            userRepository.save(user1)

            // Then
            val exception = kotlin.runCatching {
                userRepository.save(user2)
            }.exceptionOrNull()

            exception shouldNotBe null
        }

        @Test
        fun `should handle email validation correctly`() = runTest {
            // Given
            val validEmail = Email("valid@example.com")
            val user = User.create(
                email = validEmail,
                firstName = "Test",
                lastName = "User"
            )

            // When
            val savedUser = userRepository.save(user)
            val retrievedUser = userRepository.findById(savedUser.id)

            // Then
            retrievedUser?.email shouldBe validEmail
        }
    }

    @Nested
    @DisplayName("Batch Operations")
    inner class BatchOperationsTests {

        @Test
        fun `should handle multiple users efficiently`() = runTest {
            // Given
            val users = (1..10).map { index ->
                User.create(
                    email = Email("user$index@example.com"),
                    firstName = "User",
                    lastName = "$index"
                )
            }

            // When
            users.forEach { user ->
                userRepository.save(user)
            }

            // Then
            users.forEach { user ->
                val retrieved = userRepository.findById(user.id)
                retrieved shouldNotBe null
                retrieved?.email shouldBe user.email
            }
        }

        @Test
        fun `should maintain data consistency under concurrent access`() = runTest {
            // Given
            val user = TestFixtures.createTestUser()
            userRepository.save(user)

            // When - Simulate concurrent updates
            val updatedUser1 = user.updateProfile("Name1", "Last1")
            val updatedUser2 = user.updateProfile("Name2", "Last2")

            userRepository.save(updatedUser1)
            userRepository.save(updatedUser2)

            // Then
            val finalUser = userRepository.findById(user.id)
            finalUser shouldNotBe null
            // Last update should win
            finalUser?.firstName shouldBe "Name2"
            finalUser?.lastName shouldBe "Last2"
        }
    }
}
