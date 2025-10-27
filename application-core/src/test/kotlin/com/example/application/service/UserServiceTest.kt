package com.example.application.service

import com.example.domain.model.*
import com.example.domain.repository.UserRepository
import com.example.test.base.BaseUnitTest
import com.example.test.categories.ServiceTest
import com.example.test.fixtures.TestFixtures
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@ServiceTest
@DisplayName("User Service Tests - Following Porter Test Strategy 2.0")
class UserServiceTest : BaseUnitTest() {

    private val userRepository = mockk<UserRepository>()
    private lateinit var userService: UserService

    override fun setUpTestSpecific() {
        userService = UserService(userRepository)
        clearAllMocks()
    }

    @Nested
    @DisplayName("User Creation")
    inner class UserCreationTests {

        @Test
        fun `should create user successfully when valid data provided`() = runTest {
            // Given
            val email = Email("test@example.com")
            val firstName = "John"
            val lastName = "Doe"
            val expectedUser = TestFixtures.createTestUser(email = email.value, firstName = firstName, lastName = lastName)

            coEvery { userRepository.findByEmail(email) } returns null
            coEvery { userRepository.save(any()) } returns expectedUser

            // When
            val result = userService.createUser(email, firstName, lastName)

            // Then
            result.email shouldBe email
            result.firstName shouldBe firstName
            result.lastName shouldBe lastName
            result.id shouldNotBe null

            coVerify { userRepository.findByEmail(email) }
            coVerify { userRepository.save(any()) }
        }

        @Test
        fun `should throw exception when user with email already exists`() = runTest {
            // Given
            val email = Email("existing@example.com")
            val existingUser = TestFixtures.createTestUser(email = email.value)

            coEvery { userRepository.findByEmail(email) } returns existingUser

            // When & Then
            assertThrows<IllegalArgumentException> {
                userService.createUser(email, "John", "Doe")
            }

            coVerify { userRepository.findByEmail(email) }
            coVerify(exactly = 0) { userRepository.save(any()) }
        }

        @Test
        fun `should throw exception when invalid email format provided`() = runTest {
            // When & Then
            assertThrows<IllegalArgumentException> {
                Email("invalid-email")
            }
        }

        @Test
        fun `should throw exception when first name is blank`() = runTest {
            // When & Then
            assertThrows<IllegalArgumentException> {
                userService.createUser(Email("test@example.com"), "", "Doe")
            }
        }

        @Test
        fun `should throw exception when last name is blank`() = runTest {
            // When & Then
            assertThrows<IllegalArgumentException> {
                userService.createUser(Email("test@example.com"), "John", "")
            }
        }
    }

    @Nested
    @DisplayName("User Retrieval")
    inner class UserRetrievalTests {

        @Test
        fun `should return user when found by email`() = runTest {
            // Given
            val email = Email("test@example.com")
            val expectedUser = TestFixtures.createTestUser(email = email.value)

            coEvery { userRepository.findByEmail(email) } returns expectedUser

            // When
            val result = userService.getUserByEmail(email)

            // Then
            result shouldBe expectedUser
            coVerify { userRepository.findByEmail(email) }
        }

        @Test
        fun `should return null when user not found by email`() = runTest {
            // Given
            val email = Email("nonexistent@example.com")

            coEvery { userRepository.findByEmail(email) } returns null

            // When
            val result = userService.getUserByEmail(email)

            // Then
            result shouldBe null
            coVerify { userRepository.findByEmail(email) }
        }

        @Test
        fun `should return user when found by id`() = runTest {
            // Given
            val userId = UserId.generate()
            val expectedUser = TestFixtures.createUserWithId(id = userId.value)

            coEvery { userRepository.findById(userId) } returns expectedUser

            // When
            val result = userService.getUserById(userId)

            // Then
            result shouldBe expectedUser
            coVerify { userRepository.findById(userId) }
        }

        @Test
        fun `should return null when user not found by id`() = runTest {
            // Given
            val userId = UserId.generate()

            coEvery { userRepository.findById(userId) } returns null

            // When
            val result = userService.getUserById(userId)

            // Then
            result shouldBe null
            coVerify { userRepository.findById(userId) }
        }
    }

    @Nested
    @DisplayName("User Update")
    inner class UserUpdateTests {

        @Test
        fun `should update user successfully when valid data provided`() = runTest {
            // Given
            val userId = UserId.generate()
            val existingUser = TestFixtures.createUserWithId(id = userId.value)
            val updatedUser = existingUser.copy(firstName = "Jane")

            coEvery { userRepository.findById(userId) } returns existingUser
            coEvery { userRepository.save(any()) } returns updatedUser

            // When
            val result = userService.updateUser(userId, "Jane", existingUser.lastName)

            // Then
            result shouldNotBe null
            result?.firstName shouldBe "Jane"
            result?.lastName shouldBe existingUser.lastName
            coVerify { userRepository.findById(userId) }
            coVerify { userRepository.save(any()) }
        }

        @Test
        fun `should return null when updating non-existent user`() = runTest {
            // Given
            val userId = UserId.generate()

            coEvery { userRepository.findById(userId) } returns null

            // When
            val result = userService.updateUser(userId, "Jane", "Doe")

            // Then
            result shouldBe null
            coVerify { userRepository.findById(userId) }
        }
    }

    @Nested
    @DisplayName("User Deletion")
    inner class UserDeletionTests {

        @Test
        fun `should delete user successfully when user exists`() = runTest {
            // Given
            val userId = UserId.generate()

            coEvery { userRepository.delete(userId) } returns true

            // When
            val result = userService.deleteUser(userId)

            // Then
            result shouldBe true
            coVerify { userRepository.delete(userId) }
        }

        @Test
        fun `should return false when deleting non-existent user`() = runTest {
            // Given
            val userId = UserId.generate()

            coEvery { userRepository.delete(userId) } returns false

            // When
            val result = userService.deleteUser(userId)

            // Then
            result shouldBe false
            coVerify { userRepository.delete(userId) }
        }
    }

    @Nested
    @DisplayName("User Listing")
    inner class UserListingTests {

        @Test
        fun `should return paginated users`() = runTest {
            // Given
            val users = listOf(
                TestFixtures.createTestUser(email = "user1@example.com"),
                TestFixtures.createTestUser(email = "user2@example.com"),
                TestFixtures.createTestUser(email = "user3@example.com")
            )

            coEvery { userRepository.findAll(0, 10) } returns users

            // When
            val result = userService.getAllUsers(0, 10)

            // Then
            result.size shouldBe 3
            result shouldBe users
            coVerify { userRepository.findAll(0, 10) }
        }

        @Test
        fun `should return empty list when no users exist`() = runTest {
            // Given
            coEvery { userRepository.findAll(0, 10) } returns emptyList()

            // When
            val result = userService.getAllUsers(0, 10)

            // Then
            result shouldBe emptyList()
            coVerify { userRepository.findAll(0, 10) }
        }
    }
}
