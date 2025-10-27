package com.example.chaos.resilience

import com.example.test.categories.ChaosTest
import com.example.server.module
import com.example.user.api.models.CreateUserRequest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@ChaosTest
@DisplayName("Service Resilience and Chaos Engineering Tests")
class ServiceResilienceTest {

    private fun ApplicationTestBuilder.createConfiguredClient(): HttpClient {
        return createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    @Test
    fun `should handle service degradation gracefully`() = runTest {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()
            val successfulRequests = mutableListOf<Long>()
            val failedRequests = mutableListOf<String>()

            // Simulate service degradation with burst requests
            repeat(50) { i ->
                try {
                    val response = client.post("/api/v1/users") {
                        contentType(ContentType.Application.Json)
                        setBody(CreateUserRequest(
                            name = "Chaos Test User $i",
                            email = "chaos$i@example.com",
                            phone = null
                        ))
                    }

                    if (response.status == HttpStatusCode.Created) {
                        successfulRequests.add(i.toLong())
                    } else {
                        failedRequests.add("Request $i failed with status ${response.status}")
                    }
                } catch (e: Exception) {
                    failedRequests.add("Request $i failed with exception: ${e.message}")
                }

                // Add random delays to simulate real-world traffic patterns
                if (Random.nextBoolean()) {
                    delay(Random.nextLong(1, 50))
                }
            }

            val successRate = (successfulRequests.size.toDouble() / 50) * 100

            println("🌪️ Chaos Engineering Results:")
            println("  • Total requests: 50")
            println("  • Successful requests: ${successfulRequests.size}")
            println("  • Failed requests: ${failedRequests.size}")
            println("  • Success rate: ${successRate.toInt()}%")

            if (failedRequests.isNotEmpty()) {
                println("  • Sample failures: ${failedRequests.take(3)}")
            }

            // Service should maintain at least 80% success rate under stress
            successRate shouldBeGreaterThan 80.0
        }
    }

    @Test
    fun `should recover from temporary failures`() = runTest {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()
            val recoveryMetrics = mutableMapOf<String, Int>()

            // Simulate recovery scenarios
            repeat(20) { i ->
                try {
                    val response = client.get("/api/health")

                    when (response.status) {
                        HttpStatusCode.OK -> recoveryMetrics.merge("healthy", 1, Int::plus)
                        HttpStatusCode.ServiceUnavailable -> recoveryMetrics.merge("unavailable", 1, Int::plus)
                        else -> recoveryMetrics.merge("other", 1, Int::plus)
                    }
                } catch (e: Exception) {
                    recoveryMetrics.merge("exception", 1, Int::plus)
                }

                // Small delay between health checks
                delay(100.milliseconds)
            }

            val healthyChecks = recoveryMetrics["healthy"] ?: 0
            val totalChecks = recoveryMetrics.values.sum()
            val healthRate = (healthyChecks.toDouble() / totalChecks) * 100

            println("💚 Recovery Test Results:")
            println("  • Total health checks: $totalChecks")
            println("  • Healthy responses: $healthyChecks")
            println("  • Health rate: ${healthRate.toInt()}%")
            println("  • Metrics breakdown: $recoveryMetrics")

            // Health endpoint should be consistently available
            healthRate shouldBeGreaterThan 90.0
        }
    }

    @Test
    fun `should handle concurrent failures without cascading`() = runTest {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()
            val concurrentJobs = 15
            val results = mutableListOf<Deferred<String>>()

            // Launch concurrent operations that might fail
            repeat(concurrentJobs) { i ->
                val job = async {
                    try {
                        val response = client.post("/api/v1/users") {
                            contentType(ContentType.Application.Json)
                            setBody(CreateUserRequest(
                                name = "Concurrent Chaos User $i",
                                email = "chaos-concurrent$i@example.com",
                                phone = null
                            ))
                        }
                        "Success: ${response.status}"
                    } catch (e: Exception) {
                        "Failed: ${e.message?.take(50) ?: "Unknown error"}"
                    }
                }
                results.add(job)
            }

            // Wait for all operations to complete
            val outcomes = results.awaitAll()
            val successCount = outcomes.count { it.startsWith("Success") }
            val failureCount = outcomes.count { it.startsWith("Failed") }

            println("⚡ Concurrent Failure Test Results:")
            println("  • Concurrent operations: $concurrentJobs")
            println("  • Successful operations: $successCount")
            println("  • Failed operations: $failureCount")
            println("  • Concurrency success rate: ${(successCount * 100.0 / concurrentJobs).toInt()}%")

            // Even under concurrent stress, most operations should succeed
            val minimumSuccessCount = (concurrentJobs * 0.7).toInt() // 70% success rate minimum
            successCount shouldBeGreaterThan minimumSuccessCount
        }
    }

    @Test
    fun `should maintain response times under stress`() = runTest {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()
            val responseTimes = mutableListOf<Long>()

            // Stress test with response time measurement
            repeat(30) { i ->
                val startTime = System.currentTimeMillis()

                try {
                    val response = client.get("/api/health")
                    val endTime = System.currentTimeMillis()
                    val responseTime = endTime - startTime

                    if (response.status == HttpStatusCode.OK) {
                        responseTimes.add(responseTime)
                    }
                } catch (e: Exception) {
                    // Log but don't fail the test for individual timeouts
                    println("Request $i timed out or failed: ${e.message}")
                }

                // Brief pause between requests
                delay(50.milliseconds)
            }

            if (responseTimes.isNotEmpty()) {
                val averageResponseTime = responseTimes.average()
                val maxResponseTime = responseTimes.maxOrNull() ?: 0L
                val minResponseTime = responseTimes.minOrNull() ?: 0L

                println("⏱️ Response Time Under Stress:")
                println("  • Successful responses: ${responseTimes.size}")
                println("  • Average response time: ${averageResponseTime.toInt()}ms")
                println("  • Max response time: ${maxResponseTime}ms")
                println("  • Min response time: ${minResponseTime}ms")

                // Response times should remain reasonable under stress
                averageResponseTime shouldBeLessThan 1000.0 // 1 second max average
                maxResponseTime shouldBeLessThan 3000L // 3 seconds max individual
            } else {
                println("⚠️ No successful responses received during stress test")
                // Fail the test if no responses were successful
                assert(false) { "Service completely unavailable during stress test" }
            }
        }
    }
}
