package com.example.api.controller

import com.example.server.module
import com.example.test.categories.FastTest
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@FastTest
@DisplayName("Health Controller Tests")
class HealthControllerTest {

    @Test
    fun `should return healthy status`() = testApplication {
        application {
            module()
        }

        // When
        val response = client.get("/health")

        // Then
        response.status shouldBe HttpStatusCode.OK
        val responseText = response.bodyAsText()
        responseText.contains("UP") shouldBe true
    }

    @Test
    fun `should handle health check endpoint`() = testApplication {
        application {
            module()
        }

        // When
        val response = client.get("/health/check")

        // Then
        response.status shouldBe HttpStatusCode.OK
    }
}
