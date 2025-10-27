package com.example.test.config

/**
 * Test Performance Configuration
 *
 * This configuration provides different test execution strategies based on the test category:
 *
 * 1. FAST_UNIT: H2 in-memory database, no containers (< 1 second startup)
 * 2. INTEGRATION: PostgreSQL container with reuse and connection pooling (< 10 seconds)
 * 3. E2E: Full stack with all containers (< 30 seconds)
 */
object TestConfig {

    // Test execution timeouts
    const val UNIT_TEST_TIMEOUT_SECONDS = 10
    const val INTEGRATION_TEST_TIMEOUT_SECONDS = 30
    const val E2E_TEST_TIMEOUT_SECONDS = 120

    // Database configuration
    const val H2_JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
    const val H2_CONNECTION_POOL_SIZE = 5
    const val POSTGRES_CONNECTION_POOL_SIZE = 10

    // Test data limits for performance
    const val MAX_TEST_USERS = 50 // Reduced from potentially hundreds
    const val MAX_SECURITY_TEST_PATTERNS = 5 // Reduced from all patterns
    const val BATCH_INSERT_SIZE = 100

    // Container optimization settings
    const val CONTAINER_STARTUP_TIMEOUT_MINUTES = 2L
    const val CONTAINER_REUSE_ENABLED = true
    const val USE_TMPFS_FOR_POSTGRES = true

    // Performance thresholds
    const val MAX_SINGLE_OPERATION_MS = 1000L
    const val MAX_BATCH_OPERATION_MS = 5000L
    const val MAX_API_RESPONSE_MS = 2000L

    fun getTestStrategy(testClass: Class<*>): TestStrategy {
        return when {
            testClass.isAnnotationPresent(com.example.test.categories.FastTest::class.java) -> TestStrategy.FAST_UNIT
            testClass.isAnnotationPresent(com.example.test.categories.IntegrationTest::class.java) -> TestStrategy.INTEGRATION
            testClass.isAnnotationPresent(com.example.test.categories.E2ETest::class.java) -> TestStrategy.E2E
            else -> TestStrategy.FAST_UNIT
        }
    }
}

enum class TestStrategy {
    FAST_UNIT,      // H2 in-memory, no containers
    INTEGRATION,    // PostgreSQL container with optimizations
    E2E            // Full container stack
}
