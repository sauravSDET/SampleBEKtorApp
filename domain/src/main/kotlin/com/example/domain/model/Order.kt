package com.example.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderId(val value: String) {
    companion object {
        fun generate() = OrderId(UUID.randomUUID().toString())
    }
}

@Serializable
data class ProductId(val value: String) {
    companion object {
        fun generate() = ProductId(UUID.randomUUID().toString())
    }
}

@Serializable
data class OrderItem(
    val productId: String,
    val quantity: Int,
    val unitPrice: Double
) {
    init {
        require(productId.isNotBlank()) { "Product ID cannot be blank" }
        require(quantity > 0) { "Quantity must be greater than 0" }
        require(unitPrice > 0) { "Unit price must be greater than 0" }
    }

    fun getTotalPrice(): Double = quantity * unitPrice
}

@Serializable
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    fun canTransitionTo(newStatus: OrderStatus): Boolean {
        return when (this) {
            PENDING -> newStatus in setOf(CONFIRMED, CANCELLED)
            CONFIRMED -> newStatus in setOf(PROCESSING, CANCELLED)
            PROCESSING -> newStatus in setOf(SHIPPED, CANCELLED)
            SHIPPED -> newStatus in setOf(DELIVERED)
            DELIVERED -> false
            CANCELLED -> false
        }
    }
}

@Serializable
data class Order(
    val id: OrderId,
    val userId: UserId,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val totalAmount: Double,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(items.isNotEmpty()) { "Order must have at least one item" }
        require(totalAmount >= 10.0) { "Minimum order amount is 10.0" }
    }

    companion object {
        fun create(userId: UserId, items: List<OrderItem>): Order {
            require(items.isNotEmpty()) { "Order must have at least one item" }
            require(items.all { it.quantity > 0 && it.unitPrice > 0 }) { "All items must have valid quantity and price" }

            val totalAmount = items.sumOf { it.getTotalPrice() }
            val now = kotlinx.datetime.Clock.System.now()

            return Order(
                id = OrderId.generate(),
                userId = userId,
                items = items,
                status = OrderStatus.PENDING,
                totalAmount = totalAmount,
                createdAt = now,
                updatedAt = now
            )
        }
    }

    fun updateStatus(newStatus: OrderStatus): Order {
        require(status.canTransitionTo(newStatus)) {
            "Cannot transition from $status to $newStatus"
        }

        return copy(
            status = newStatus,
            updatedAt = kotlinx.datetime.Clock.System.now()
        )
    }

    fun isValid(): Boolean = items.isNotEmpty() && totalAmount > 0 && items.all {
        it.quantity > 0 && it.unitPrice > 0 && it.productId.isNotBlank()
    }

    fun isModifiable(): Boolean = status in setOf(OrderStatus.PENDING, OrderStatus.CONFIRMED)

    fun isCancellable(): Boolean = status in setOf(OrderStatus.PENDING, OrderStatus.CONFIRMED)

    fun getAgeInHours(): Double {
        val now = kotlinx.datetime.Clock.System.now()
        val duration = now - createdAt
        return duration.inWholeMilliseconds / (1000.0 * 60.0 * 60.0)
    }
}
