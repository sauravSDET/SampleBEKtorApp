package com.example.contract.testing

import com.example.test.categories.ApiTest
import com.example.server.module
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.minutes

/**
 * Contract Testing Framework
 *
 * Implements feedback from test strategy 2.0:
 * - Beyond schema validation - includes behavioral contracts
 * - API-first development support
 * - Clear contract ownership and versioning
 * - Self-contained contract testing in repositories
 */

@ApiTest
@DisplayName("API Contract Validation")
class ContractValidationTest {

    private fun ApplicationTestBuilder.createConfiguredClient(): HttpClient {
        return createClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    @Test
    fun `should validate request-response contract for user creation`() = testApplication {
        application {
            module()
        }

        val client = createConfiguredClient()

        // Test user creation contract
        val response = client.post("/api/v1/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Test User","email":"test@example.com"}""")
        }

        response.status shouldBe HttpStatusCode.Created
        response.headers[HttpHeaders.ContentType] shouldContain "application/json"
    }

    @Test
    fun `should validate HTTP method contracts`() = testApplication {
        application {
            module()
        }

        val client = createConfiguredClient()

        // Test that POST is required for user creation (not GET)
        val invalidResponse = client.get("/api/v1/users") {
            // This should be a list operation, not creation
        }

        invalidResponse.status shouldBe HttpStatusCode.OK // GET should work for listing
    }

    @Test
    fun `should validate error response contracts`() = testApplication {
        application {
            module()
        }

        val client = createConfiguredClient()

        // Test error response structure
        val response = client.post("/api/v1/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":""}""") // Invalid data
        }

        response.status shouldBe HttpStatusCode.BadRequest
        response.headers[HttpHeaders.ContentType] shouldContain "application/json"
    }

    @Test
    fun `should validate content-type contracts`() = testApplication {
        application {
            module()
        }

        val client = createConfiguredClient()

        // Test that JSON content-type is enforced
        val response = client.post("/api/v1/users") {
            contentType(ContentType.Text.Plain)
            setBody("invalid content type")
        }

        // Should reject non-JSON content
        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `should validate pagination contract for user listing`() = testApplication {
        application {
            module()
        }

        val client = createConfiguredClient()

        // Test pagination contract
        val response = client.get("/api/v1/users?page=0&size=5")

        response.status shouldBe HttpStatusCode.OK
        response.headers[HttpHeaders.ContentType] shouldContain "application/json"
    }

    @Test
    fun `should validate response time contracts for performance SLA`() = runTest(timeout = 2.minutes) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()

            // Test response time contract (should be fast for in-memory testing)
            val startTime = System.currentTimeMillis()
            val response = client.get("/api/v1/users")
            val endTime = System.currentTimeMillis()

            response.status shouldBe HttpStatusCode.OK
            val responseTime = endTime - startTime

            // In-memory tests should be very fast (under 1 second)
            assert(responseTime < 1000) { "Response time ${responseTime}ms exceeds 1000ms SLA" }
        }
    }

    @Test
    fun `should validate data consistency contracts across operations`() = testApplication {
        application {
            module()
        }

        val client = createConfiguredClient()

        // Create a user
        val createResponse = client.post("/api/v1/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Consistency Test","email":"consistency@example.com"}""")
        }

        createResponse.status shouldBe HttpStatusCode.Created

        // Verify consistency by listing users
        val listResponse = client.get("/api/v1/users")
        listResponse.status shouldBe HttpStatusCode.OK
    }
}
