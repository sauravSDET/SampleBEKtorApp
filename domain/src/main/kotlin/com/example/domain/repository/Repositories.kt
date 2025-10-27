package com.example.domain.repository

import com.example.domain.model.*

interface UserRepository {
    suspend fun save(user: User): User
    suspend fun findById(id: UserId): User?
    suspend fun findByEmail(email: Email): User?
    suspend fun findAll(page: Int = 0, size: Int = 10): List<User>
    suspend fun count(): Long
    suspend fun delete(id: UserId): Boolean
}

interface OrderRepository {
    suspend fun save(order: Order): Order
    suspend fun findById(id: OrderId): Order?
    suspend fun findByUserId(userId: UserId): List<Order>
    suspend fun findByStatus(status: OrderStatus): List<Order>
    suspend fun findAll(): List<Order>
    suspend fun delete(id: OrderId): Boolean
}
