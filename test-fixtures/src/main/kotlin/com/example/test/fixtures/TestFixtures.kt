package com.example.test.fixtures

import com.example.domain.model.*
import kotlinx.datetime.Clock

/**
 * Test fixtures for creating test data objects
 * Provides standardized test data for consistent testing
 */
object TestFixtures {

    fun createValidUser(
        email: String = "test@example.com",
        firstName: String = "John",
        lastName: String = "Doe"
    ): User {
        return User.create(
            email = Email(email),
            firstName = firstName,
            lastName = lastName
        )
    }

    fun createUserWithId(
        id: String = "test-user-id",
        email: String = "test@example.com",
        firstName: String = "John",
        lastName: String = "Doe"
    ): User {
        val now = Clock.System.now()
        return User(
            id = UserId(id),
            email = Email(email),
            firstName = firstName,
            lastName = lastName,
            createdAt = now,
            updatedAt = now
        )
    }

    fun createMultipleUsers(count: Int = 3): List<User> {
        return (1..count).map { index ->
            createValidUser(
                email = "user$index@example.com",
                firstName = "User$index",
                lastName = "Test"
            )
        }
    }

    // Alias for backwards compatibility
    fun createTestUser(
        email: String = "test@example.com",
        firstName: String = "John",
        lastName: String = "Doe"
    ): User = createValidUser(email, firstName, lastName)

    object Emails {
        const val VALID_EMAIL = "test@example.com"
        const val ANOTHER_EMAIL = "another@example.com"
        const val ADMIN_EMAIL = "admin@company.com"
    }

    object Names {
        const val FIRST_NAME = "John"
        const val LAST_NAME = "Doe"
        const val UPDATED_FIRST_NAME = "Jane"
        const val UPDATED_LAST_NAME = "Smith"
    }
}
