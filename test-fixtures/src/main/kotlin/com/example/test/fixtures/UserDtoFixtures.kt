package com.example.test.fixtures

import kotlinx.serialization.Serializable

/**
 * DTO fixtures for API testing
 */
object UserDtoFixtures {

    @Serializable
    data class CreateUserRequest(
        val email: String,
        val firstName: String,
        val lastName: String
    )

    @Serializable
    data class UpdateUserRequest(
        val firstName: String? = null,
        val lastName: String? = null
    )

    @Serializable
    data class UserResponse(
        val id: String,
        val email: String,
        val firstName: String,
        val lastName: String,
        val createdAt: String,
        val updatedAt: String
    )

    fun createUserRequest(
        email: String = "test@example.com",
        firstName: String = "John",
        lastName: String = "Doe"
    ): CreateUserRequest {
        return CreateUserRequest(
            email = email,
            firstName = firstName,
            lastName = lastName
        )
    }

    // Alias for test consistency
    fun aCreateUserRequest(
        email: String = "test@example.com",
        firstName: String = "John",
        lastName: String = "Doe"
    ): CreateUserRequest = createUserRequest(email, firstName, lastName)

    fun updateUserRequest(
        firstName: String = "Jane",
        lastName: String = "Smith"
    ): UpdateUserRequest {
        return UpdateUserRequest(
            firstName = firstName,
            lastName = lastName
        )
    }

    // Alias for test consistency
    fun anUpdateUserRequest(
        firstName: String = "Jane",
        lastName: String = "Smith",
        email: String? = null // Add this parameter but ignore it for now
    ): UpdateUserRequest = updateUserRequest(firstName, lastName)

    fun userResponse(
        id: String = "test-user-id",
        email: String = "test@example.com",
        firstName: String = "John",
        lastName: String = "Doe"
    ): UserResponse {
        return UserResponse(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )
    }
}
