package com.example.domain.service

import com.example.domain.events.*
import com.example.domain.model.*
import com.example.domain.repository.*
import kotlinx.datetime.Clock

interface UserService {
    suspend fun createUser(email: Email, firstName: String, lastName: String): User
    suspend fun updateUser(userId: UserId, firstName: String?, lastName: String?): User
    suspend fun getUser(userId: UserId): User?
    suspend fun getUserByEmail(email: Email): User?
    suspend fun getAllUsers(page: Int = 0, size: Int = 10): Pair<List<User>, Long>
}

interface OrderService {
    suspend fun createOrder(userId: UserId, items: List<OrderItem>): Order
    suspend fun updateOrderStatus(orderId: OrderId, newStatus: OrderStatus): Order
    suspend fun getOrder(orderId: OrderId): Order?
    suspend fun getOrdersByUser(userId: UserId): List<Order>
    suspend fun getOrdersByStatus(status: OrderStatus): List<Order>
}

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher
) : UserService {

    override suspend fun createUser(email: Email, firstName: String, lastName: String): User {
        // Check if user with this email already exists
        userRepository.findByEmail(email)?.let {
            throw IllegalArgumentException("User with email ${email.value} already exists")
        }

        val user = User.create(email, firstName, lastName)
        val savedUser = userRepository.save(user)

        // Publish domain event
        eventPublisher.publish(
            UserCreatedEvent(
                aggregateId = savedUser.id.value,
                email = savedUser.email.value,
                firstName = savedUser.firstName,
                lastName = savedUser.lastName,
                occurredAt = Clock.System.now()
            )
        )

        return savedUser
    }

    override suspend fun updateUser(userId: UserId, firstName: String?, lastName: String?): User {
        val existingUser = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User with ID ${userId.value} not found")

        // Use existing values if new ones are null
        val newFirstName = firstName ?: existingUser.firstName
        val newLastName = lastName ?: existingUser.lastName

        val updatedUser = existingUser.updateProfile(newFirstName, newLastName)
        val savedUser = userRepository.save(updatedUser)

        // Publish domain event
        eventPublisher.publish(
            UserUpdatedEvent(
                aggregateId = savedUser.id.value,
                firstName = newFirstName,
                lastName = newLastName,
                occurredAt = Clock.System.now()
            )
        )

        return savedUser
    }

    override suspend fun getUser(userId: UserId): User? {
        return userRepository.findById(userId)
    }

    override suspend fun getUserByEmail(email: Email): User? {
        return userRepository.findByEmail(email)
    }

    override suspend fun getAllUsers(page: Int, size: Int): Pair<List<User>, Long> {
        val users = userRepository.findAll(page, size)
        val totalCount = userRepository.count()
        return Pair(users, totalCount)
    }
}

class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher
) : OrderService {

    override suspend fun createOrder(userId: UserId, items: List<OrderItem>): Order {
        // Validate user exists
        userRepository.findById(userId)
            ?: throw IllegalArgumentException("User with ID ${userId.value} not found")

        val order = Order.create(userId, items)
        val savedOrder = orderRepository.save(order)

        // Publish domain event
        eventPublisher.publish(
            OrderCreatedEvent(
                aggregateId = savedOrder.id.value,
                userId = savedOrder.userId.value,
                items = savedOrder.items,
                totalAmount = savedOrder.totalAmount,
                occurredAt = Clock.System.now()
            )
        )

        return savedOrder
    }

    override suspend fun updateOrderStatus(orderId: OrderId, newStatus: OrderStatus): Order {
        val existingOrder = orderRepository.findById(orderId)
            ?: throw IllegalArgumentException("Order with ID ${orderId.value} not found")

        val updatedOrder = existingOrder.updateStatus(newStatus)
        val savedOrder = orderRepository.save(updatedOrder)

        // Publish domain event
        eventPublisher.publish(
            OrderStatusChangedEvent(
                aggregateId = savedOrder.id.value,
                oldStatus = existingOrder.status,
                newStatus = newStatus,
                occurredAt = Clock.System.now()
            )
        )

        return savedOrder
    }

    override suspend fun getOrder(orderId: OrderId): Order? {
        return orderRepository.findById(orderId)
    }

    override suspend fun getOrdersByUser(userId: UserId): List<Order> {
        return orderRepository.findByUserId(userId)
    }

    override suspend fun getOrdersByStatus(status: OrderStatus): List<Order> {
        return orderRepository.findByStatus(status)
    }
}
