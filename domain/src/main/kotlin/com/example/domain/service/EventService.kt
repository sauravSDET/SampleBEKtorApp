package com.example.domain.service

import com.example.domain.events.DomainEvent

interface EventPublisher {
    suspend fun publish(event: DomainEvent)
    suspend fun publishBatch(events: List<DomainEvent>)
}

interface EventHandler<T : DomainEvent> {
    suspend fun handle(event: T)
}
