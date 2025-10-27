package com.example.commons.api.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import io.swagger.v3.oas.annotations.media.Schema

@Serializable
@Schema(description = "Common error response model")
data class ErrorResponse(
    @Schema(description = "Error code", example = "VALIDATION_ERROR")
    val code: String,
    @Schema(description = "Human readable error message", example = "Invalid input data")
    val message: String,
    @Schema(description = "Timestamp when error occurred")
    val timestamp: Instant,
    @Schema(description = "Additional error details")
    val details: Map<String, String>? = null
)

@Serializable
@Schema(description = "Common success response wrapper")
data class ApiResponse<T>(
    @Schema(description = "Response data")
    val data: T,
    @Schema(description = "Success status", example = "true")
    val success: Boolean = true,
    @Schema(description = "Response timestamp")
    val timestamp: Instant
)

@Serializable
@Schema(description = "Pagination metadata")
data class PaginationMeta(
    @Schema(description = "Current page number", example = "1")
    val page: Int,
    @Schema(description = "Number of items per page", example = "10")
    val size: Int,
    @Schema(description = "Total number of items", example = "100")
    val total: Long,
    @Schema(description = "Total number of pages", example = "10")
    val totalPages: Int
)

@Serializable
@Schema(description = "Paginated response wrapper")
data class PaginatedResponse<T>(
    @Schema(description = "List of items")
    val items: List<T>,
    @Schema(description = "Pagination metadata")
    val meta: PaginationMeta,
    @Schema(description = "Success status", example = "true")
    val success: Boolean = true,
    @Schema(description = "Response timestamp")
    val timestamp: Instant
)
