package com.example.api.dto

import com.example.user.api.models.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class PaginatedUsersResponse(
    val users: List<UserResponse>,
    val totalCount: Int,
    val page: Int,
    val size: Int,
    val totalPages: Int = (totalCount + size - 1) / size
)
