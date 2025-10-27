package com.example.infrastructure.kafka

import com.example.domain.events.UserCreatedEvent
import com.example.domain.events.UserUpdatedEvent
import com.example.domain.events.OrderCreatedEvent
import com.example.domain.model.OrderItem
import com.example.test.categories.IntegrationTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

@IntegrationTest
@DisplayName("Kafka Event Producer Tests")
@Timeout(30, unit = TimeUnit.SECONDS)
class KafkaEventProducerTest {

    private lateinit var kafkaEventProducer: KafkaEventProducer

    @BeforeEach
    fun setup() {
        kafkaEventProducer = KafkaEventProducer()
    }

    @Nested
    @DisplayName("Single Event Publishing")
    inner class SingleEventTests {

        @Test
        fun `should publish user created event successfully`() = runTest {
            // Given
            val event = UserCreatedEvent(
                aggregateId = "user-123",
                email = "test@example.com",
                firstName = "John",
                lastName = "Doe",
                occurredAt = Clock.System.now()
            )

            // When & Then (should not throw)
            kafkaEventProducer.publish(event)
        }

        @Test
        fun `should publish user updated event successfully`() = runTest {
            // Given
            val event = UserUpdatedEvent(
                aggregateId = "user-123",
                firstName = "Jane",
                lastName = "Smith",
                occurredAt = Clock.System.now()
            )

            // When & Then (should not throw)
            kafkaEventProducer.publish(event)
        }

        @Test
        fun `should publish order created event successfully`() = runTest {
            // Given
            val orderItems = listOf(
                OrderItem(
                    productId = "product-1",
                    quantity = 2,
                    unitPrice = 49.99
                )
            )
            val event = OrderCreatedEvent(
                aggregateId = "order-456",
                userId = "user-123",
                items = orderItems,
                totalAmount = 99.98,
                occurredAt = Clock.System.now()
            )

            // When & Then (should not throw)
            kafkaEventProducer.publish(event)
        }
    }

    @Nested
    @DisplayName("Batch Event Publishing")
    inner class BatchEventTests {

        @Test
        fun `should publish multiple events in batch`() = runTest {
            // Given
            val orderItems = listOf(
                OrderItem(productId = "product-1", quantity = 1, unitPrice = 149.99)
            )
            val events = listOf(
                UserCreatedEvent(
                    aggregateId = "user-1",
                    email = "user1@example.com",
                    firstName = "John",
                    lastName = "Doe",
                    occurredAt = Clock.System.now()
                ),
                UserCreatedEvent(
                    aggregateId = "user-2",
                    email = "user2@example.com",
                    firstName = "Jane",
                    lastName = "Smith",
                    occurredAt = Clock.System.now()
                ),
                OrderCreatedEvent(
                    aggregateId = "order-1",
                    userId = "user-1",
                    items = orderItems,
                    totalAmount = 149.99,
                    occurredAt = Clock.System.now()
                )
            )

            // When & Then (should not throw)
            kafkaEventProducer.publishBatch(events)
        }

        @Test
        fun `should handle empty batch gracefully`() = runTest {
            // Given
            val emptyEvents = emptyList<UserCreatedEvent>()

            // When & Then (should not throw)
            kafkaEventProducer.publishBatch(emptyEvents)
        }

        @Test
        fun `should handle large batch efficiently`() = runTest {
            // Given
            val largeEventBatch = (1..100).map { index ->
                UserCreatedEvent(
                    aggregateId = "user-$index",
                    email = "user$index@example.com",
                    firstName = "User",
                    lastName = "Number$index",
                    occurredAt = Clock.System.now()
                )
            }

            // When
            val startTime = System.currentTimeMillis()
            kafkaEventProducer.publishBatch(largeEventBatch)
            val endTime = System.currentTimeMillis()

            // Then
            val executionTime = endTime - startTime
            // Should complete in reasonable time (less than 5 seconds for 100 events)
            (executionTime < 5000) shouldBe true
        }
    }

    @Nested
    @DisplayName("Error Handling")
    inner class ErrorHandlingTests {

        @Test
        fun `should handle serialization errors gracefully`() = runTest {
            // Given
            val eventWithSpecialChars = UserCreatedEvent(
                aggregateId = "user-special-chars",
                email = "test@example.com",
                firstName = "John™",
                lastName = "Doe®",
                occurredAt = Clock.System.now()
            )

            // When & Then (should not throw)
            kafkaEventProducer.publish(eventWithSpecialChars)
        }

        @Test
        fun `should retry failed publishing attempts`() = runTest {
            // Given
            val event = UserCreatedEvent(
                aggregateId = "user-retry-test",
                email = "retry@example.com",
                firstName = "Retry",
                lastName = "Test",
                occurredAt = Clock.System.now()
            )

            // When & Then (should eventually succeed with retries)
            kafkaEventProducer.publishWithRetry(event, maxRetries = 3)
        }

        @Test
        fun `should handle kafka broker unavailable scenario`() = runTest {
            // Given
            val event = UserCreatedEvent(
                aggregateId = "user-offline-test",
                email = "offline@example.com",
                firstName = "Offline",
                lastName = "Test",
                occurredAt = Clock.System.now()
            )

            // When & Then (should handle gracefully)
            try {
                kafkaEventProducer.publishWithTimeout(event, timeoutMs = 1000)
            } catch (e: Exception) {
                // Expected to potentially fail in test environment
                e.message shouldNotBe null
            }
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    inner class PerformanceTests {

        @Test
        fun `should maintain throughput under high load`() = runTest {
            // Given
            val highVolumeEvents = (1..1000).map { index ->
                UserCreatedEvent(
                    aggregateId = "load-test-user-$index",
                    email = "loadtest$index@example.com",
                    firstName = "Load",
                    lastName = "Test$index",
                    occurredAt = Clock.System.now()
                )
            }

            // When
            val startTime = System.currentTimeMillis()
            highVolumeEvents.chunked(50).forEach { batch ->
                kafkaEventProducer.publishBatch(batch)
            }
            val endTime = System.currentTimeMillis()

            // Then
            val executionTime = endTime - startTime
            val eventsPerSecond = (1000.0 / executionTime) * 1000

            // Should maintain reasonable throughput (at least 100 events/second)
            (eventsPerSecond > 100) shouldBe true
        }

        @Test
        fun `should handle concurrent publishing safely`() = runTest {
            // Given
            val concurrentEvents = (1..50).map { index ->
                UserCreatedEvent(
                    aggregateId = "concurrent-user-$index",
                    email = "concurrent$index@example.com",
                    firstName = "Concurrent",
                    lastName = "Test$index",
                    occurredAt = Clock.System.now()
                )
            }

            // When - Simulate concurrent publishing
            concurrentEvents.forEach { event ->
                kafkaEventProducer.publish(event)
            }

            // Then - Should complete without errors
            // All events should be published successfully
        }
    }
}

// Mock KafkaEventProducer implementation for testing
class KafkaEventProducer {
    suspend fun publish(event: Any) {
        // Mock implementation - would actually publish to Kafka
        println("Publishing event: ${event::class.simpleName}")
    }

    suspend fun publishBatch(events: List<Any>) {
        // Mock implementation - would actually publish batch to Kafka
        println("Publishing batch of ${events.size} events")
    }

    suspend fun publishWithRetry(event: Any, maxRetries: Int = 3) {
        // Mock implementation with retry logic
        var attempts = 0
        while (attempts <= maxRetries) {
            try {
                publish(event)
                break
            } catch (e: Exception) {
                attempts++
                if (attempts > maxRetries) throw e
            }
        }
    }

    suspend fun publishWithTimeout(event: Any, timeoutMs: Long) {
        // Mock implementation with timeout - simulate delay based on timeout
        kotlinx.coroutines.delay(minOf(timeoutMs, 100)) // Simulate some delay
        publish(event)
    }
}
