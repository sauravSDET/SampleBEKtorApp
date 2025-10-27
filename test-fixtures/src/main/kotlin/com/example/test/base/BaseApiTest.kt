package com.example.test.base

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Base class for API tests using Ktor test server
 * Following Test Strategy 2.0 patterns for API contract validation
 */
abstract class BaseApiTest : BaseIntegrationTest() {

    protected lateinit var testClient: HttpClient

    @BeforeEach
    override fun setUpIntegration() {
        super.setUpIntegration()
        setUpApiTest()
    }

    @AfterEach
    override fun tearDownIntegration() {
        if (::testClient.isInitialized) {
            testClient.close()
        }
        super.tearDownIntegration()
    }

    private fun setUpApiTest() {
        // API-specific setup
    }

    protected fun configureTestClient(client: HttpClient) {
        client.config {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }
}
