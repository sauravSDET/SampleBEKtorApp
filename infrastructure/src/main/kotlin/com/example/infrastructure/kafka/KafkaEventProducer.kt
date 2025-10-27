package com.example.infrastructure.kafka

import com.example.domain.service.EventPublisher
import com.example.domain.events.DomainEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KafkaEventProducer @Inject constructor() : EventPublisher {
    suspend fun sendEvent(topic: String, key: String, message: String) {
        // TODO: Implement Kafka event production
        println("Sending event to topic: $topic, key: $key, message: $message")
    }

    override suspend fun publish(event: DomainEvent) {
        // TODO: Implement domain event publishing to Kafka
        println("Publishing domain event: ${event::class.simpleName} with ID: ${event.aggregateId}")
    }

    override suspend fun publishBatch(events: List<DomainEvent>) {
        // TODO: Implement batch domain event publishing to Kafka
        println("Publishing batch of ${events.size} domain events to Kafka")
        events.forEach { event ->
            publish(event)
        }
    }
}
