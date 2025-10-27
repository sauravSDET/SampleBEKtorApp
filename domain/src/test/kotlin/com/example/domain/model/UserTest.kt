package com.example.domain.model

import com.example.test.base.BaseUnitTest
import com.example.test.categories.FastTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@FastTest
@DisplayName("User Domain Model Tests")
class UserTest : BaseUnitTest() {

    @Nested
    @DisplayName("User Creation")
    inner class CreationTests {

        @Test
        fun `should create user with valid data`() {
            // Given (AI Pattern: Use standard data)
            val email = Email("test@example.com")
            val firstName = "John"
            val lastName = "Doe"

            // When (AI Pattern: Domain operation)
            val user = User.create(email, firstName, lastName)

            // Then (AI Pattern: Business validation)
            user.id shouldNotBe null
            user.email shouldBe email
            user.firstName shouldBe firstName
            user.lastName shouldBe lastName
            user.createdAt shouldNotBe null
            user.updatedAt shouldNotBe null
            user.isValid() shouldBe true
        }

        @Test
        fun `should reject user with invalid email`() {
            // Given
            val invalidEmail = "invalid-email"

            // When & Then
            assertThrows<IllegalArgumentException> {
                Email(invalidEmail)
            }
        }

        @Test
        fun `should reject user with blank first name`() {
            // Given
            val email = Email("test@example.com")
            val blankFirstName = ""
            val lastName = "Doe"

            // When & Then
            assertThrows<IllegalArgumentException> {
                User.create(email, blankFirstName, lastName)
            }
        }

        @Test
        fun `should reject user with blank last name`() {
            // Given
            val email = Email("test@example.com")
            val firstName = "John"
            val blankLastName = ""

            // When & Then
            assertThrows<IllegalArgumentException> {
                User.create(email, firstName, blankLastName)
            }
        }

        @Test
        fun `should create user with valid email formats`() {
            // Given
            val validEmails = listOf(
                "test@example.com",
                "user.name@domain.co.uk",
                "user+tag@example.org",
                "user123@test-domain.com"
            )

            validEmails.forEach { emailString ->
                // When
                val email = Email(emailString)
                val user = User.create(email, "John", "Doe")

                // Then
                user.email.value shouldBe emailString
                user.isValid() shouldBe true
            }
        }
    }

    @Nested
    @DisplayName("User Business Operations")
    inner class BusinessOperationsTests {

        @Test
        fun `should update user profile successfully`() {
            // Given
            val user = User.create(Email("test@example.com"), "John", "Doe")
            val newFirstName = "Jane"
            val newLastName = "Smith"

            // When
            val updatedUser = user.updateProfile(newFirstName, newLastName)

            // Then
            updatedUser.id shouldBe user.id
            updatedUser.email shouldBe user.email
            updatedUser.firstName shouldBe newFirstName
            updatedUser.lastName shouldBe newLastName
            updatedUser.createdAt shouldBe user.createdAt
            updatedUser.updatedAt shouldNotBe user.updatedAt
        }

        @Test
        fun `should maintain immutability during updates`() {
            // Given
            val originalUser = User.create(Email("test@example.com"), "John", "Doe")
            val originalFirstName = originalUser.firstName

            // When
            val updatedUser = originalUser.updateProfile("Jane", "Smith")

            // Then
            originalUser.firstName shouldBe originalFirstName // Original unchanged
            updatedUser.firstName shouldBe "Jane" // New instance updated
            originalUser shouldNotBe updatedUser // Different instances
        }

        @Test
        fun `should validate user state correctly`() {
            // Given
            val validUser = User.create(Email("test@example.com"), "John", "Doe")

            // When & Then
            validUser.isValid() shouldBe true
            validUser.email.value.isNotBlank() shouldBe true
            validUser.firstName.isNotBlank() shouldBe true
            validUser.lastName.isNotBlank() shouldBe true
        }

        @Test
        fun `should handle equality correctly`() {
            // Given
            val user1 = User.create(Email("test@example.com"), "John", "Doe")
            val user2 = user1.copy()
            val user3 = User.create(Email("different@example.com"), "Jane", "Smith")

            // When & Then
            (user1 == user2) shouldBe true
            (user1 == user3) shouldBe false
            user1.hashCode() shouldBe user2.hashCode()
        }

        @Test
        fun `should convert to string representation correctly`() {
            // Given
            val user = User.create(Email("test@example.com"), "John", "Doe")

            // When
            val userString = user.toString()

            // Then
            userString.contains("User") shouldBe true
            userString.contains("test@example.com") shouldBe true
        }
    }

    @Nested
    @DisplayName("User ID Operations")
    inner class UserIdTests {

        @Test
        fun `should generate unique user IDs`() {
            // Given & When
            val id1 = UserId.generate()
            val id2 = UserId.generate()

            // Then
            id1 shouldNotBe id2
            id1.value shouldNotBe id2.value
        }

        @Test
        fun `should create user ID from UUID string`() {
            // Given
            val uuidString = "123e4567-e89b-12d3-a456-426614174000"

            // When
            val userId = UserId(uuidString)

            // Then
            userId.value shouldBe uuidString
        }
    }

    @Nested
    @DisplayName("Email Value Object")
    inner class EmailTests {

        @Test
        fun `should create valid email`() {
            // Given
            val emailString = "test@example.com"

            // When
            val email = Email(emailString)

            // Then
            email.value shouldBe emailString
        }

        @Test
        fun `should handle email comparison correctly`() {
            // Given
            val email1 = Email("test@example.com")
            val email2 = Email("test@example.com")
            val email3 = Email("different@example.com")

            // When & Then
            (email1 == email2) shouldBe true
            (email1 == email3) shouldBe false
            email1.hashCode() shouldBe email2.hashCode()
        }

        @Test
        fun `should reject invalid email formats`() {
            // Given - emails that don't contain @ or . (matching actual validation)
            val invalidEmails = listOf(
                "plainaddress",  // no @ or .
                "onlyat@",       // has @ but no .
                "onlydot.com"    // has . but no @
            )

            invalidEmails.forEach { invalidEmail ->
                // When & Then
                assertThrows<IllegalArgumentException> {
                    Email(invalidEmail)
                }
            }
        }
    }
}
