package com.example.test.scenarios

import com.example.test.fixtures.*
import com.example.domain.model.*
import com.example.domain.events.*
import kotlinx.datetime.Clock

/**
 * Test scenarios following Porter Test Strategy 2.0 patterns
 * Provides business workflow patterns for comprehensive testing
 */
object TestScenarios {

    fun userCreationScenario(): UserCreationScenario {
        val email = Email("newuser@example.com")
        val firstName = "John"
        val lastName = "Doe"

        return UserCreationScenario(
            email = email,
            firstName = firstName,
            lastName = lastName,
            expectedEvents = listOf(
                UserCreatedEvent(
                    aggregateId = "generated-id",
                    email = email.value,
                    firstName = firstName,
                    lastName = lastName,
                    occurredAt = Clock.System.now()
                )
            )
        )
    }

    fun userUpdateScenario(): UserUpdateScenario {
        val existingUser = UserFixtures.aUser()
        val newFirstName = "Jane"
        val newLastName = "Smith"

        return UserUpdateScenario(
            existingUser = existingUser,
            newFirstName = newFirstName,
            newLastName = newLastName,
            expectedEvents = listOf(
                UserUpdatedEvent(
                    aggregateId = existingUser.id.value,
                    firstName = newFirstName,
                    lastName = newLastName,
                    occurredAt = Clock.System.now()
                )
            )
        )
    }

    fun paginatedUsersScenario(): PaginatedUsersScenario {
        val totalUsers = 25
        val users = UserFixtures.multipleUsers(totalUsers)

        return PaginatedUsersScenario(
            setupData = users,
            totalUsers = totalUsers,
            pageSize = 10
        )
    }

    fun orderCreationScenario(): OrderCreationScenario {
        val user = UserFixtures.aUser()
        val orderItems = listOf(
            OrderFixtures.anOrderItem(productId = "product-1", quantity = 2, unitPrice = 50.0),
            OrderFixtures.anOrderItem(productId = "product-2", quantity = 1, unitPrice = 75.0)
        )
        val expectedTotal = 175.0

        return OrderCreationScenario(
            userId = user.id,
            items = orderItems,
            expectedTotal = expectedTotal,
            expectedEvents = listOf(
                OrderCreatedEvent(
                    aggregateId = "generated-order-id",
                    userId = user.id.value,
                    items = orderItems,
                    totalAmount = expectedTotal,
                    occurredAt = Clock.System.now()
                )
            )
        )
    }

    fun orderStatusUpdateScenario(): OrderStatusUpdateScenario {
        val existingOrder = OrderFixtures.anOrder()
        val newStatus = com.example.domain.model.OrderStatus.CONFIRMED

        return OrderStatusUpdateScenario(
            existingOrder = existingOrder,
            newStatus = newStatus,
            expectedEvents = listOf(
                OrderStatusChangedEvent(
                    aggregateId = existingOrder.id.value,
                    oldStatus = existingOrder.status,
                    newStatus = newStatus,
                    occurredAt = Clock.System.now()
                )
            )
        )
    }

    // Data classes for scenarios
    data class UserCreationScenario(
        val email: Email,
        val firstName: String,
        val lastName: String,
        val expectedEvents: List<DomainEvent>
    )

    data class UserUpdateScenario(
        val existingUser: User,
        val newFirstName: String,
        val newLastName: String,
        val expectedEvents: List<DomainEvent>
    )

    data class PaginatedUsersScenario(
        val setupData: List<User>,
        val totalUsers: Int,
        val pageSize: Int
    ) {
        suspend fun setupData(userRepository: Any) {
            // Setup logic for scenario - would be implemented with actual repository
        }
    }

    data class OrderCreationScenario(
        val userId: UserId,
        val items: List<OrderItem>,
        val expectedTotal: Double,
        val expectedEvents: List<DomainEvent>
    )

    data class OrderStatusUpdateScenario(
        val existingOrder: Order,
        val newStatus: com.example.domain.model.OrderStatus,
        val expectedEvents: List<DomainEvent>
    )
}
