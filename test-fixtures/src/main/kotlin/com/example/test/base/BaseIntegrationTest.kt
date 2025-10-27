package com.example.test.base

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.AfterAll
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.containers.wait.strategy.Wait
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.Savepoint
import java.time.Duration
import javax.sql.DataSource

/**
 * Optimized base class for integration tests using Testcontainers
 *
 * Key optimizations:
 * - Container reuse to avoid startup costs
 * - Connection pooling for efficient resource usage
 * - Transaction-based test isolation (faster than schema recreation)
 * - Shared container across test classes
 */
@Testcontainers
abstract class BaseIntegrationTest {

    companion object {
        @Container
        @JvmStatic
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:16.2-alpine").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
            withReuse(true) // Enable reuse for faster tests
            withTmpFs(mapOf("/var/lib/postgresql/data" to "rw")) // Use tmpfs for faster I/O
            waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 2)) // Consistent wait strategy
            withStartupTimeout(Duration.ofMinutes(3)) // Increased timeout for reliability
            withCommand("postgres", "-c", "fsync=off", "-c", "synchronous_commit=off", "-c", "max_connections=100") // Optimized for tests
        }

        private var dataSource: HikariDataSource? = null
        private var schemaInitialized = false

        @JvmStatic
        @BeforeAll
        fun setUpClass() {
            if (!postgresContainer.isRunning) {
                postgresContainer.start()
            }

            // Initialize connection pool once
            if (dataSource == null) {
                dataSource = createDataSource()
                initializeSchemaOnce()
            }
        }

        @JvmStatic
        @AfterAll
        fun tearDownClass() {
            dataSource?.close()
        }

        private fun createDataSource(): HikariDataSource {
            val config = HikariConfig().apply {
                jdbcUrl = postgresContainer.jdbcUrl
                username = postgresContainer.username
                password = postgresContainer.password
                maximumPoolSize = 10
                minimumIdle = 2
                connectionTimeout = 30000
                idleTimeout = 600000
                maxLifetime = 1800000
                isAutoCommit = false // Important for transaction-based test isolation
            }
            return HikariDataSource(config)
        }

        private fun initializeSchemaOnce() {
            if (!schemaInitialized) {
                getSharedDataSource().connection.use { connection ->
                    val statement = connection.createStatement()

                    // Create users table with correct schema matching PostgresUserRepository
                    statement.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id VARCHAR(36) PRIMARY KEY,
                            email VARCHAR(255) UNIQUE NOT NULL,
                            first_name VARCHAR(255) NOT NULL,
                            last_name VARCHAR(255) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )
                    """.trimIndent())

                    // Create orders table for integration tests
                    statement.execute("""
                        CREATE TABLE IF NOT EXISTS orders (
                            id VARCHAR(36) PRIMARY KEY,
                            user_id VARCHAR(36) NOT NULL,
                            status VARCHAR(50) NOT NULL,
                            total_amount DECIMAL(10,2),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(id)
                        )
                    """.trimIndent())

                    connection.commit()
                    statement.close()
                }
                schemaInitialized = true
            }
        }

        fun getSharedDataSource(): DataSource = dataSource
            ?: throw IllegalStateException("DataSource not initialized")
    }

    private var connection: Connection? = null
    private var savepoint: Savepoint? = null

    @BeforeEach
    open fun setUpIntegration() {
        // Get connection from pool and start transaction
        connection = getSharedDataSource().connection
        connection?.autoCommit = false
        savepoint = connection?.setSavepoint("test_savepoint")

        setUpTestData()
        setUpIntegrationSpecific()
    }

    @AfterEach
    open fun tearDownIntegration() {
        try {
            // Rollback to savepoint to clean up test data
            savepoint?.let { connection?.rollback(it) }
        } finally {
            connection?.close()
            tearDownIntegrationSpecific()
        }
    }

    // Template methods for subclasses
    protected open fun setUpIntegrationSpecific() {}
    protected open fun tearDownIntegrationSpecific() {}
    protected open fun setUpTestData() {}

    // Helper methods for database access
    protected fun getConnection(): Connection = connection
        ?: throw IllegalStateException("Connection not available")

    protected fun getJdbcUrl(): String = postgresContainer.jdbcUrl
    protected fun getUsername(): String = postgresContainer.username
    protected fun getPassword(): String = postgresContainer.password
}
