package com.example.health.api.models

import com.example.commons.api.models.ApiResponse
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import io.swagger.v3.oas.annotations.media.Schema

@Serializable
@Schema(description = "Health check response")
data class HealthResponse(
    @Schema(description = "Service status", example = "UP")
    val status: HealthStatus,
    @Schema(description = "Check timestamp")
    val timestamp: Instant,
    @Schema(description = "Service version", example = "1.0.0")
    val version: String,
    @Schema(description = "Additional health details")
    val details: Map<String, HealthCheckDetail>? = null
)

@Serializable
@Schema(description = "Health status enumeration")
enum class HealthStatus {
    UP,
    DOWN,
    DEGRADED
}

@Serializable
@Schema(description = "Individual health check detail")
data class HealthCheckDetail(
    @Schema(description = "Component status", example = "UP")
    val status: HealthStatus,
    @Schema(description = "Component details")
    val details: Map<String, String>? = null
)

// Type aliases for common response patterns
typealias GetHealthResponse = ApiResponse<HealthResponse>
