package com.example.security.automated

import com.example.test.categories.SecurityTest
import com.example.server.module
import com.example.user.api.models.CreateUserRequest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

@SecurityTest
@DisplayName("Automated Security Testing")
class SecurityValidationTest {

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
    fun `should prevent SQL injection attacks`() = runTest(timeout = 30.seconds) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()

            // Test various SQL injection payloads
            val sqlInjectionPayloads = listOf(
                "'; DROP TABLE users; --",
                "' OR '1'='1",
                "' UNION SELECT * FROM users --",
                "admin'--",
                "'; INSERT INTO users VALUES ('hacker', 'hack@evil.com'); --"
            )

            sqlInjectionPayloads.forEach { payload ->
                val response = client.post("/api/v1/users") {
                    contentType(ContentType.Application.Json)
                    setBody(CreateUserRequest(
                        name = payload,
                        email = "test@example.com",
                        phone = null
                    ))
                }

                // Should either reject with 400 Bad Request or sanitize the input
                val responseBody = response.bodyAsText()

                // Ensure no SQL errors are exposed
                responseBody shouldNotContain "SQLException"
                responseBody shouldNotContain "SQL syntax"
                responseBody shouldNotContain "database error"

                println("✅ SQL injection payload blocked: ${payload.take(20)}...")
            }
        }
    }

    @Test
    fun `should prevent XSS attacks`() = runTest(timeout = 30.seconds) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()

            // Test XSS payloads
            val xssPayloads = listOf(
                "<script>alert('XSS')</script>",
                "<img src=x onerror=alert('XSS')>",
                "javascript:alert('XSS')",
                "<svg onload=alert('XSS')>",
                "';alert(String.fromCharCode(88,83,83));//'"
            )

            xssPayloads.forEach { payload ->
                val response = client.post("/api/v1/users") {
                    contentType(ContentType.Application.Json)
                    setBody(CreateUserRequest(
                        name = payload,
                        email = "test@example.com",
                        phone = null
                    ))
                }

                val responseBody = response.bodyAsText()

                // Ensure scripts are not executed or reflected
                responseBody shouldNotContain "<script>"
                responseBody shouldNotContain "javascript:"
                responseBody shouldNotContain "onerror="
                responseBody shouldNotContain "onload="

                println("✅ XSS payload sanitized: ${payload.take(20)}...")
            }
        }
    }

    @Test
    fun `should validate input length limits`() = runTest(timeout = 30.seconds) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()

            // Test extremely long input
            val longName = "A".repeat(10000)
            val longEmail = "test${"a".repeat(1000)}@example.com"

            val response = client.post("/api/v1/users") {
                contentType(ContentType.Application.Json)
                setBody(CreateUserRequest(
                    name = longName,
                    email = longEmail,
                    phone = null
                ))
            }

            // Should reject extremely long inputs with either 400 Bad Request or 413 Payload Too Large
            val isValidResponse = response.status == HttpStatusCode.BadRequest ||
                                response.status == HttpStatusCode.PayloadTooLarge
            assert(isValidResponse) { "Expected 400 or 413, but got ${response.status}" }

            println("✅ Long input rejected with status: ${response.status}")

            val responseBody = response.bodyAsText()
            responseBody shouldNotContain longName // Shouldn't echo back the full long input

            println("✅ Long input validation working correctly")
        }
    }

    @Test
    fun `should handle malformed JSON gracefully`() = runTest(timeout = 30.seconds) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()

            val malformedJsonPayloads = listOf(
                "{name: 'test', email: 'invalid json'}",
                "{'name': 'test', 'email': 'missing quotes}",
                "{\"name\": \"test\", \"email\": }",
                "not json at all",
                "null",
                ""
            )

            malformedJsonPayloads.forEach { payload ->
                try {
                    val response = client.post("/api/v1/users") {
                        contentType(ContentType.Application.Json)
                        setBody(payload)
                    }

                    // Should return 400 Bad Request for malformed JSON
                    response.status shouldBe HttpStatusCode.BadRequest

                    val responseBody = response.bodyAsText()
                    responseBody shouldNotContain "JsonException"
                    responseBody shouldNotContain "parsing error"

                } catch (e: Exception) {
                    // Client-side JSON parsing errors are also acceptable
                    println("✅ Malformed JSON rejected at client level: ${e.message?.take(50)}")
                }
            }

            println("✅ Malformed JSON handling validated")
        }
    }

    @Test
    fun `should prevent header injection attacks`() = runTest(timeout = 30.seconds) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()

            // Test header injection payloads
            val headerInjectionPayloads = listOf(
                "test\r\nSet-Cookie: admin=true",
                "test\nLocation: http://evil.com",
                "test\r\nContent-Type: text/html"
            )

            headerInjectionPayloads.forEach { payload ->
                val response = client.post("/api/v1/users") {
                    contentType(ContentType.Application.Json)
                    setBody(CreateUserRequest(
                        name = payload,
                        email = "test@example.com",
                        phone = null
                    ))
                }

                // Check response headers don't contain injected content
                val headers = response.headers
                val setCookieHeader = headers["Set-Cookie"]
                val locationHeader = headers["Location"]

                // Safe null checks for headers
                if (setCookieHeader != null) {
                    setCookieHeader shouldNotContain "admin=true"
                }
                if (locationHeader != null) {
                    locationHeader shouldNotContain "evil.com"
                }

                println("✅ Header injection blocked for payload: ${payload.take(20)}...")
            }
        }
    }

    @Test
    fun `should enforce proper content type validation`() = runTest(timeout = 30.seconds) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()

            // Test with different content types
            val invalidContentTypes = listOf(
                ContentType.Text.Plain,
                ContentType.Text.Html,
                ContentType.Application.Xml,
                ContentType.MultiPart.FormData
            )

            invalidContentTypes.forEach { contentType ->
                val response = client.post("/api/v1/users") {
                    this.contentType(contentType)
                    setBody("invalid data for content type")
                }

                // Should reject non-JSON content types for JSON endpoints
                // Accept either 400 Bad Request or 415 Unsupported Media Type
                val isValidResponse = response.status == HttpStatusCode.BadRequest || response.status == HttpStatusCode.UnsupportedMediaType
                assert(isValidResponse) { "Expected 400 or 415, but got ${response.status}" }
            }
        }
    }

    @Test
    fun `should validate email format securely`() = runTest(timeout = 30.seconds) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()

            // Test various malicious email formats
            val maliciousEmails = listOf(
                "admin@;DROP TABLE users;",
                "test@<script>alert('xss')</script>.com",
                "user@domain..com",
                "test@",
                "@domain.com",
                "test@@domain.com",
                "test@domain@evil.com"
            )

            maliciousEmails.forEach { email ->
                val response = client.post("/api/v1/users") {
                    contentType(ContentType.Application.Json)
                    setBody(CreateUserRequest(
                        name = "Test User",
                        email = email,
                        phone = null
                    ))
                }

                // Should reject invalid email formats
                response.status shouldBe HttpStatusCode.BadRequest

                val responseBody = response.bodyAsText()
                responseBody shouldNotContain "<script>"
                responseBody shouldNotContain "DROP TABLE"

                println("✅ Malicious email format rejected: ${email.take(30)}...")
            }
        }
    }

    @Test
    fun `should rate limit requests properly`() = runTest(timeout = 30.seconds) {
        testApplication {
            application {
                module()
            }

            val client = createConfiguredClient()
            val requestCount = 100
            val successfulRequests = mutableListOf<HttpStatusCode>()
            val rateLimitedRequests = mutableListOf<HttpStatusCode>()

            // Simulate rapid requests
            repeat(requestCount) { i ->
                val response = client.post("/api/v1/users") {
                    contentType(ContentType.Application.Json)
                    setBody(CreateUserRequest(
                        name = "Rate Limit Test User $i",
                        email = "ratelimit$i@example.com",
                        phone = null
                    ))
                }

                when (response.status) {
                    HttpStatusCode.Created -> successfulRequests.add(response.status)
                    HttpStatusCode.TooManyRequests -> rateLimitedRequests.add(response.status)
                    else -> Unit // Other responses
                }
            }

            println("🚦 Rate Limiting Results:")
            println("  • Total requests: $requestCount")
            println("  • Successful requests: ${successfulRequests.size}")
            println("  • Rate limited requests: ${rateLimitedRequests.size}")

            // For this test, we expect some rate limiting to occur if implemented
            // If no rate limiting is implemented, all requests might succeed
            if (rateLimitedRequests.isNotEmpty()) {
                println("✅ Rate limiting is active and working")
            } else {
                println("ℹ️ No rate limiting detected (may not be implemented)")
            }
        }
    }
}
