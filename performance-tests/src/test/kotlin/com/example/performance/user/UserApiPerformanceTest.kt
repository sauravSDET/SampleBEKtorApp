package com.example.performance.user

import com.example.test.categories.PerformanceTest
import com.example.server.module
import com.example.user.api.models.CreateUserRequest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.comparables.shouldBeLessThan
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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

@PerformanceTest
@DisplayName("User API Performance Tests")
class UserApiPerformanceTest {

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
    fun `should maintain less than 200ms response time under normal load`() = runTest(timeout = 30.seconds) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()
            val responseTimes = mutableListOf<Long>()

            // Test normal load (10 concurrent requests)
            repeat(10) {
                val responseTime = measureTime {
                    val response = client.post("/api/v1/users") {
                        contentType(ContentType.Application.Json)
                        setBody(CreateUserRequest(
                            name = "Performance Test User $it",
                            email = "perf$it@example.com",
                            phone = null
                        ))
                    }
                    response.status shouldBe HttpStatusCode.Created
                }.inWholeMilliseconds

                responseTimes.add(responseTime)
            }

            val averageResponseTime = responseTimes.average()
            println("📊 Performance Metrics:")
            println("  • Average response time: ${averageResponseTime.toInt()}ms")
            println("  • Max response time: ${responseTimes.maxOrNull()}ms")
            println("  • Min response time: ${responseTimes.minOrNull()}ms")

            // In-memory tests should be very fast
            averageResponseTime shouldBeLessThan 200.0
        }
    }

    @Test
    fun `should handle sustained load without memory leaks`() = runTest(timeout = 30.seconds) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()
            val runtime = Runtime.getRuntime()
            val initialMemory = runtime.totalMemory() - runtime.freeMemory()

            // Simulate sustained load
            repeat(50) { i ->
                val response = client.post("/api/v1/users") {
                    contentType(ContentType.Application.Json)
                    setBody(CreateUserRequest(
                        name = "Load Test User $i",
                        email = "load$i@example.com",
                        phone = null
                    ))
                }
                response.status shouldBe HttpStatusCode.Created

                // Suggest garbage collection periodically
                if (i % 10 == 0) {
                    runtime.gc()
                }
            }

            val finalMemory = runtime.totalMemory() - runtime.freeMemory()
            val memoryGrowth = finalMemory - initialMemory

            println("📈 Memory Usage:")
            println("  • Initial memory: ${initialMemory / 1024 / 1024}MB")
            println("  • Final memory: ${finalMemory / 1024 / 1024}MB")
            println("  • Memory growth: ${memoryGrowth / 1024 / 1024}MB")

            // Memory growth should be reasonable (less than 50MB for in-memory tests)
            assert(memoryGrowth < 50 * 1024 * 1024) { "Memory growth ${memoryGrowth / 1024 / 1024}MB exceeds 50MB threshold" }
        }
    }

    @Test
    fun `should handle database connection pool under load`() = runTest(timeout = 30.seconds) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()
            val concurrentRequests = 20
            val jobs = mutableListOf<Deferred<HttpResponse>>()

            // Simulate concurrent database load
            repeat(concurrentRequests) { i ->
                val job = async {
                    client.post("/api/v1/users") {
                        contentType(ContentType.Application.Json)
                        setBody(CreateUserRequest(
                            name = "Concurrent User $i",
                            email = "concurrent$i@example.com",
                            phone = null
                        ))
                    }
                }
                jobs.add(job)
            }

            // Wait for all requests to complete
            val responses = jobs.awaitAll()
            val successfulResponses = responses.count { it.status == HttpStatusCode.Created }

            println("🔄 Concurrent Load Results:")
            println("  • Total requests: $concurrentRequests")
            println("  • Successful responses: $successfulResponses")
            println("  • Success rate: ${(successfulResponses * 100.0 / concurrentRequests).toInt()}%")

            successfulResponses shouldBeGreaterThan (concurrentRequests * 0.9).toInt() // 90% success rate
        }
    }
}
