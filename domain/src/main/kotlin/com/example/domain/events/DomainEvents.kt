package com.example.domain.events

import com.example.domain.model.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed interface DomainEvent {
    val eventId: String
    val aggregateId: String
    val occurredAt: Instant
}

@Serializable
data class UserCreatedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val aggregateId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    override val occurredAt: Instant
) : DomainEvent

@Serializable
data class UserUpdatedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val aggregateId: String,
    val firstName: String?,
    val lastName: String?,
    override val occurredAt: Instant
) : DomainEvent

@Serializable
data class OrderCreatedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val aggregateId: String,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    override val occurredAt: Instant
) : DomainEvent

@Serializable
data class OrderStatusChangedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val aggregateId: String,
    val oldStatus: OrderStatus,
    val newStatus: OrderStatus,
    override val occurredAt: Instant
) : DomainEvent
