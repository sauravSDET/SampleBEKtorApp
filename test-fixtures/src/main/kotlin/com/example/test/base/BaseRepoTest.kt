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
import kotlin.random.Random

/**
 * Isolated and Independent Repository Test Base Class
 *
 * Implements Test Strategy 2.0 principles:
 * - Each test class gets its own database schema for complete isolation
 * - Transaction-based cleanup for test method isolation
 * - PostgreSQL TestContainers exclusively (no H2 fallback)
 * - Performance optimizations with container reuse
 */
@Testcontainers
abstract class BaseRepoTest {

    companion object {
        @Container
        @JvmStatic
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
            withReuse(true) // Enable reuse for faster tests
            waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 2))
            withStartupTimeout(Duration.ofMinutes(2))
        }

        private var globalDataSource: HikariDataSource? = null

        @JvmStatic
        @BeforeAll
        fun setUpGlobalContainer() {
            try {
                if (!postgresContainer.isRunning) {
                    postgresContainer.start()
                    // Wait additional time for PostgreSQL to be fully ready
                    Thread.sleep(3000)
                }

                // Create global connection pool for container management only
                globalDataSource = createGlobalDataSource()
                println("✅ PostgreSQL TestContainer ready for isolated test execution")
            } catch (e: Exception) {
                println("❌ Failed to start PostgreSQL container: ${e.message}")
                throw e
            }
        }

        @JvmStatic
        @AfterAll
        fun tearDownGlobalContainer() {
            globalDataSource?.close()
        }

        private fun createGlobalDataSource(): HikariDataSource {
            val config = HikariConfig().apply {
                jdbcUrl = postgresContainer.jdbcUrl
                username = postgresContainer.username
                password = postgresContainer.password
                maximumPoolSize = 2 // Minimal pool for container health checks
                minimumIdle = 1
                connectionTimeout = 5000
                isAutoCommit = true // For schema creation only
            }
            return HikariDataSource(config)
        }

        fun getContainerJdbcUrl(): String = postgresContainer.jdbcUrl
        fun getContainerUsername(): String = postgresContainer.username
        fun getContainerPassword(): String = postgresContainer.password
        fun getGlobalDataSource(): HikariDataSource? = globalDataSource
    }

    // Instance-level isolation
    private var isolatedDataSource: HikariDataSource? = null
    private var schemaName: String = ""
    private var connection: Connection? = null
    private var savepoint: Savepoint? = null

    @BeforeEach
    open fun setUpIsolatedTest() {
        // Create unique schema for this test class instance for complete isolation
        schemaName = "test_schema_${this::class.simpleName}_${Random.nextInt(10000)}"

        // Create isolated data source for this test
        isolatedDataSource = createIsolatedDataSource()

        // Initialize isolated schema
        initializeIsolatedSchema()

        // Set up transaction-based isolation for test methods
        connection = isolatedDataSource!!.connection
        connection?.autoCommit = false
        savepoint = connection?.setSavepoint("test_method_savepoint")

        println("🔒 PostgreSQL Test isolation ready: schema=$schemaName")

        setUpTestData()
        setUpRepoSpecific()
    }

    @AfterEach
    open fun tearDownIsolatedTest() {
        try {
            // Rollback transaction to clean up test data
            savepoint?.let { connection?.rollback(it) }
        } catch (e: Exception) {
            println("⚠️ Transaction rollback failed: ${e.message}")
        } finally {
            // Close connection
            connection?.close()

            // Clean up isolated resources
            isolatedDataSource?.close()

            tearDownRepoSpecific()
        }
    }

    private fun createIsolatedDataSource(): HikariDataSource {
        val config = HikariConfig().apply {
            jdbcUrl = "${getContainerJdbcUrl()}?currentSchema=$schemaName"
            username = getContainerUsername()
            password = getContainerPassword()
            maximumPoolSize = 3 // Small pool for single test class
            minimumIdle = 1
            connectionTimeout = 10000
            isAutoCommit = false // Transaction-based isolation
        }
        return HikariDataSource(config)
    }

    private fun initializeIsolatedSchema() {
        getGlobalDataSource()?.connection?.use { conn ->
            val statement = conn.createStatement()

            // Create isolated schema
            statement.execute("CREATE SCHEMA IF NOT EXISTS $schemaName")
            statement.execute("SET search_path TO $schemaName")

            // Create tables in isolated schema
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

            conn.commit()
        }
    }

    // Template methods for subclasses
    protected open fun setUpRepoSpecific() {}
    protected open fun tearDownRepoSpecific() {}
    protected open fun setUpTestData() {}

    // Helper methods for database access
    protected fun getConnection(): Connection = connection
        ?: throw IllegalStateException("Connection not available - test setup failed")

    protected fun getDataSource(): DataSource = isolatedDataSource
        ?: throw IllegalStateException("DataSource not available - test setup failed")

    protected fun getJdbcUrl(): String = isolatedDataSource?.jdbcUrl ?: ""
    protected fun getUsername(): String = getContainerUsername()
    protected fun getPassword(): String = getContainerPassword()
}
