package com.example.test.fixtures

import com.example.domain.model.*
import kotlinx.datetime.Clock

/**
 * User-specific test fixtures
 */
object UserFixtures {

    fun aUser(
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

    fun multipleUsers(count: Int): List<User> {
        return (1..count).map { index ->
            aUser(
                id = "user-$index",
                email = "user$index@example.com",
                firstName = "User$index",
                lastName = "Test"
            )
        }
    }
}
