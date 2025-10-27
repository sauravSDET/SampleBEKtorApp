package com.example.test.fixtures

/**
 * Security-related test fixtures
 */
object SecurityFixtures {

    data class AuthToken(
        val token: String,
        val type: String = "Bearer"
    )

    data class UserCredentials(
        val username: String,
        val password: String
    )

    fun validAuthToken(token: String = "valid-jwt-token"): AuthToken {
        return AuthToken(token = token)
    }

    fun invalidAuthToken(): AuthToken {
        return AuthToken(token = "invalid-jwt-token")
    }

    fun expiredAuthToken(): AuthToken {
        return AuthToken(token = "expired-jwt-token")
    }

    fun adminCredentials(): UserCredentials {
        return UserCredentials(
            username = "admin@example.com",
            password = "admin123"
        )
    }

    fun userCredentials(): UserCredentials {
        return UserCredentials(
            username = "user@example.com",
            password = "user123"
        )
    }

    fun authorizationHeader(token: AuthToken = validAuthToken()): String {
        return "${token.type} ${token.token}"
    }

    // Security test payloads
    fun sqlInjectionPayload(): String {
        return "'; DROP TABLE users; --"
    }

    fun xssPayload(): String {
        return "<script>alert('XSS')</script>"
    }

    fun oversizedPayload(): String {
        return "A".repeat(10000) // 10KB string
    }

    fun invalidEmailFormats(): List<String> {
        return listOf(
            "invalid-email",
            "@example.com",
            "test@",
            "test..test@example.com",
            "test@example",
            ""
        )
    }
}
