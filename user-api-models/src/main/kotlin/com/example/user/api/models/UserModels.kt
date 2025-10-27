package com.example.user.api.models

import com.example.commons.api.models.ApiResponse
import com.example.commons.api.models.PaginatedResponse
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import io.swagger.v3.oas.annotations.media.Schema

@Serializable
@Schema(description = "User creation request")
data class CreateUserRequest(
    @Schema(description = "User's full name", example = "John Doe")
    val name: String,
    @Schema(description = "User's email address", example = "john.doe@example.com")
    val email: String,
    @Schema(description = "User's phone number", example = "+1234567890")
    val phone: String? = null
)

@Serializable
@Schema(description = "User update request")
data class UpdateUserRequest(
    @Schema(description = "User's full name", example = "John Doe")
    val name: String? = null,
    @Schema(description = "User's email address", example = "john.doe@example.com")
    val email: String? = null,
    @Schema(description = "User's phone number", example = "+1234567890")
    val phone: String? = null
)

@Serializable
@Schema(description = "User response model")
data class UserResponse(
    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: String,
    @Schema(description = "User's full name", example = "John Doe")
    val name: String,
    @Schema(description = "User's email address", example = "john.doe@example.com")
    val email: String,
    @Schema(description = "User's phone number", example = "+1234567890")
    val phone: String? = null,
    @Schema(description = "User creation timestamp")
    val createdAt: Instant,
    @Schema(description = "User last update timestamp")
    val updatedAt: Instant
)

// Type aliases for common response patterns
typealias CreateUserResponse = ApiResponse<UserResponse>
typealias GetUserResponse = ApiResponse<UserResponse>
typealias UpdateUserResponse = ApiResponse<UserResponse>
typealias GetUsersResponse = PaginatedResponse<UserResponse>
