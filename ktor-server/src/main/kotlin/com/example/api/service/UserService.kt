package com.example.api.service

import com.example.user.api.models.*
import com.example.application.dto.ErrorResponse
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory user service for testing purposes
 * Following Test Strategy 2.0 patterns
 */
class UserService {
    private val users = ConcurrentHashMap<String, UserData>()

    data class UserData(
        val id: String,
        val name: String,
        val email: String,
        val phone: String?,
        val createdAt: Instant,
        val updatedAt: Instant
    )

    // Add method to clear data between tests
    fun clearAll() {
        users.clear()
    }

    fun createUser(request: CreateUserRequest): Result<UserResponse> {
        // Validate input
        val validationErrors = validateCreateRequest(request)
        if (validationErrors.isNotEmpty()) {
            return Result.failure(ValidationException("Validation failed", validationErrors))
        }

        // Check for duplicate email
        if (users.values.any { it.email == request.email }) {
            return Result.failure(ConflictException("User with email ${request.email} already exists"))
        }

        // Create user
        val id = UUID.randomUUID().toString()
        val now = Clock.System.now()
        val userData = UserData(
            id = id,
            name = request.name,
            email = request.email,
            phone = request.phone,
            createdAt = now,
            updatedAt = now
        )

        users[id] = userData
        return Result.success(userData.toResponse())
    }

    fun getUserById(id: String): Result<UserResponse> {
        // Validate ID format
        if (!isValidUUID(id)) {
            return Result.failure(BadRequestException("Invalid user ID format"))
        }

        val user = users[id]
            ?: return Result.failure(NotFoundException("User with ID $id not found"))

        return Result.success(user.toResponse())
    }

    fun updateUser(id: String, request: UpdateUserRequest): Result<UserResponse> {
        // Validate ID format
        if (!isValidUUID(id)) {
            return Result.failure(BadRequestException("Invalid user ID format"))
        }

        val existingUser = users[id]
            ?: return Result.failure(NotFoundException("User with ID $id not found"))

        // Validate input
        val validationErrors = validateUpdateRequest(request)
        if (validationErrors.isNotEmpty()) {
            return Result.failure(ValidationException("Validation failed", validationErrors))
        }

        // Check for duplicate email if email is being updated
        if (request.email != null && request.email != existingUser.email) {
            if (users.values.any { it.email == request.email && it.id != id }) {
                return Result.failure(ConflictException("User with email ${request.email} already exists"))
            }
        }

        // Update user
        val updatedUser = existingUser.copy(
            name = request.name ?: existingUser.name,
            email = request.email ?: existingUser.email,
            phone = request.phone ?: existingUser.phone,
            updatedAt = Clock.System.now()
        )

        users[id] = updatedUser
        return Result.success(updatedUser.toResponse())
    }

    fun listUsers(page: Int = 0, size: Int = 10): Map<String, Any> {
        val allUsers = users.values.sortedBy { it.createdAt }
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, allUsers.size)

        val paginatedUsers = if (startIndex < allUsers.size) {
            allUsers.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return mapOf(
            "users" to paginatedUsers.map { it.toResponse() },
            "totalCount" to allUsers.size,
            "page" to page,
            "size" to size
        )
    }

    private fun validateCreateRequest(request: CreateUserRequest): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (request.name.isBlank()) {
            errors["name"] = "Name is required"
        } else if (containsSuspiciousCharacters(request.name)) {
            errors["name"] = "Invalid characters detected"
        }

        if (request.email.isBlank()) {
            errors["email"] = "Email is required"
        } else if (!isValidEmail(request.email)) {
            errors["email"] = "Invalid email format"
        } else if (containsSuspiciousCharacters(request.email)) {
            errors["email"] = "Invalid characters detected"
        }

        return errors
    }

    private fun validateUpdateRequest(request: UpdateUserRequest): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        request.name?.let { name ->
            if (name.isBlank()) {
                errors["name"] = "Name cannot be empty"
            } else if (containsSuspiciousCharacters(name)) {
                errors["name"] = "Invalid characters detected"
            }
        }

        request.email?.let { email ->
            if (email.isBlank()) {
                errors["email"] = "Email cannot be empty"
            } else if (!isValidEmail(email)) {
                errors["email"] = "Invalid email format"
            } else if (containsSuspiciousCharacters(email)) {
                errors["email"] = "Invalid characters detected"
            }
        }

        return errors
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex) && !email.contains("..") && !email.contains(" ")
    }

    private fun isValidUUID(id: String): Boolean {
        return try {
            UUID.fromString(id)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun containsSuspiciousCharacters(input: String): Boolean {
        val suspiciousPatterns = listOf(
            "<script", "javascript:", "DROP TABLE", "SELECT *", "INSERT INTO", "DELETE FROM", "UPDATE SET"
        )
        return suspiciousPatterns.any { pattern ->
            input.contains(pattern, ignoreCase = true)
        }
    }

    private fun UserData.toResponse() = UserResponse(
        id = id,
        name = name,
        email = email,
        phone = phone,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    // Custom exceptions
    class ValidationException(message: String, val errors: Map<String, String>) : Exception(message)
    class ConflictException(message: String) : Exception(message)
    class NotFoundException(message: String) : Exception(message)
    class BadRequestException(message: String) : Exception(message)
}
