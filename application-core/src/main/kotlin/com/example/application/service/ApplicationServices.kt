package com.example.application.service

import com.example.application.dto.*
import com.example.domain.model.*
import com.example.domain.service.UserService
import com.example.domain.service.OrderService
import kotlinx.datetime.toJavaInstant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserApplicationService @Inject constructor(
    private val userService: UserService
) {
    suspend fun createUser(request: CreateUserRequest): UserResponse {
        val user = userService.createUser(
            email = Email(request.email),
            firstName = request.firstName,
            lastName = request.lastName
        )
        return user.toResponse()
    }

    suspend fun updateUser(userId: String, request: UpdateUserRequest): UserResponse {
        val user = userService.updateUser(
            userId = UserId(userId),
            firstName = request.firstName,
            lastName = request.lastName
        )
        return user.toResponse()
    }

    suspend fun getUser(userId: String): UserResponse? {
        return userService.getUser(UserId(userId))?.toResponse()
    }

    suspend fun getUsers(page: Int, size: Int): UsersListResponse {
        val (users, totalCount) = userService.getAllUsers(page, size)
        return UsersListResponse(
            users = users.map { it.toResponse() },
            totalCount = totalCount
        )
    }

    private fun User.toResponse() = UserResponse(
        id = id.value,
        email = email.value,
        firstName = firstName,
        lastName = lastName,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}

@Singleton
class OrderApplicationService @Inject constructor(
    private val orderService: OrderService
) {
    suspend fun createOrder(request: CreateOrderRequest): OrderResponse {
        val orderItems = request.items.map { item ->
            OrderItem(
                productId = item.productId,
                quantity = item.quantity,
                unitPrice = item.price
            )
        }

        val order = orderService.createOrder(
            userId = UserId(request.userId),
            items = orderItems
        )
        return order.toResponse()
    }

    suspend fun updateOrderStatus(orderId: String, request: UpdateOrderStatusRequest): OrderResponse {
        val status = OrderStatus.valueOf(request.status.uppercase())
        val order = orderService.updateOrderStatus(
            orderId = OrderId(orderId),
            newStatus = status
        )
        return order.toResponse()
    }

    suspend fun getOrder(orderId: String): OrderResponse? {
        return orderService.getOrder(OrderId(orderId))?.toResponse()
    }

    suspend fun getOrdersByUser(userId: String): List<OrderResponse> {
        return orderService.getOrdersByUser(UserId(userId)).map { it.toResponse() }
    }

    private fun Order.toResponse() = OrderResponse(
        id = id.value,
        userId = userId.value,
        items = items.map { item ->
            OrderItemResponse(
                productId = item.productId,
                quantity = item.quantity,
                price = item.unitPrice,
                totalPrice = item.getTotalPrice()
            )
        },
        totalAmount = totalAmount,
        status = status.name,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}
