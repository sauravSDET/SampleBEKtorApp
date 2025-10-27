package com.example.order.api.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class CreateOrderRequest(
    val userId: String,
    val items: List<OrderItem>
) {
    @Serializable
    data class OrderItem(
        val productId: String,
        val quantity: Int,
        val price: Double
    )
}

@Serializable
data class OrderResponse(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    @Serializable
    data class OrderItem(
        val productId: String,
        val quantity: Int,
        val price: Double
    )
}

@Serializable
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
