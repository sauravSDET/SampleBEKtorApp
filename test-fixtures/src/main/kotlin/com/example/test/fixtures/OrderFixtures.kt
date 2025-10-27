package com.example.test.fixtures

import com.example.domain.model.*
import kotlinx.datetime.Clock

/**
 * Order-specific test fixtures
 */
object OrderFixtures {

    fun anOrder(
        id: String = "test-order-id",
        userId: String = "test-user-id",
        status: OrderStatus = OrderStatus.PENDING
    ): Order {
        val now = Clock.System.now()
        return Order(
            id = OrderId(id),
            userId = UserId(userId),
            items = listOf(anOrderItem()),
            status = status,
            totalAmount = 100.0,
            createdAt = now,
            updatedAt = now
        )
    }

    fun anOrderItem(
        productId: String = "test-product-id",
        quantity: Int = 1,
        unitPrice: Double = 50.0
    ): OrderItem {
        return OrderItem(
            productId = productId,
            quantity = quantity,
            unitPrice = unitPrice
        )
    }
}
