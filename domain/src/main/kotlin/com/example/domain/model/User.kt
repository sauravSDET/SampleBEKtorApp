package com.example.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@JvmInline
value class UserId(val value: String) {
    companion object {
        fun generate(): UserId = UserId(UUID.randomUUID().toString())
    }
}

@Serializable
@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@") && value.contains(".")) {
            "Invalid email format: $value"
        }
    }
}

@Serializable
data class User(
    val id: UserId,
    val email: Email,
    val firstName: String,
    val lastName: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun create(
            email: Email,
            firstName: String,
            lastName: String
        ): User {
            require(firstName.isNotBlank()) { "First name cannot be blank" }
            require(lastName.isNotBlank()) { "Last name cannot be blank" }

            val now = Clock.System.now()
            return User(
                id = UserId.generate(),
                email = email,
                firstName = firstName,
                lastName = lastName,
                createdAt = now,
                updatedAt = now
            )
        }
    }

    fun updateProfile(newFirstName: String, newLastName: String): User {
        require(newFirstName.isNotBlank()) { "First name cannot be blank" }
        require(newLastName.isNotBlank()) { "Last name cannot be blank" }

        return copy(
            firstName = newFirstName,
            lastName = newLastName,
            updatedAt = Clock.System.now()
        )
    }

    fun isValid(): Boolean {
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.value.contains("@")
    }
}
