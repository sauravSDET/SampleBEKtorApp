package com.example.application.dto

import kotlinx.serialization.Serializable

// User DTOs
@Serializable
data class CreateUserRequest(
    val email: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class UpdateUserRequest(
    val firstName: String?,
    val lastName: String?
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

@Serializable
data class UsersListResponse(
    val users: List<UserResponse>,
    val totalCount: Long
)

// Order DTOs
@Serializable
data class OrderItemRequest(
    val productId: String,
    val quantity: Int,
    val price: Double
)

@Serializable
data class CreateOrderRequest(
    val userId: String,
    val items: List<OrderItemRequest>
)

@Serializable
data class UpdateOrderStatusRequest(
    val status: String
)

@Serializable
data class OrderItemResponse(
    val productId: String,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double
)

@Serializable
data class OrderResponse(
    val id: String,
    val userId: String,
    val items: List<OrderItemResponse>,
    val totalAmount: Double,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)
