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
 * Unified Database Test Base Class
 *
 * Combines repository and integration testing with robust fallback mechanisms:
 * - Primary: PostgreSQL TestContainers with schema isolation
 * - Fallback: H2 in-memory database when Docker is unavailable
 * - Transaction-based cleanup for test method isolation
 * - Optimized for both CI/CD and local development
 */
@Testcontainers
abstract class BaseDbTest {

    companion object {
        @Container
        @JvmStatic
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
            withReuse(true)
            waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 2))
            withStartupTimeout(Duration.ofMinutes(2))
        }

        private var dataSource: HikariDataSource? = null
        private var useH2Fallback = false

        @JvmStatic
        @BeforeAll
        fun setUpDatabase() {
            try {
                // Try PostgreSQL first
                if (!postgresContainer.isRunning) {
                    postgresContainer.start()
                    Thread.sleep(2000) // Additional wait for full readiness
                }

                dataSource = createPostgreSQLDataSource()

                // Test connection
                dataSource!!.connection.use { conn ->
                    conn.createStatement().execute("SELECT 1")
                }

                println("✅ PostgreSQL TestContainer ready")
            } catch (e: Exception) {
                println("⚠️ PostgreSQL container failed, falling back to H2: ${e.message}")
                useH2Fallback = true
                dataSource = createH2DataSource()
                println("✅ H2 in-memory database ready")
            }

            initializeSchema()
        }

        @JvmStatic
        @AfterAll
        fun tearDownDatabase() {
            dataSource?.close()
        }

        private fun createPostgreSQLDataSource(): HikariDataSource {
            val config = HikariConfig().apply {
                jdbcUrl = postgresContainer.jdbcUrl
                username = postgresContainer.username
                password = postgresContainer.password
                maximumPoolSize = 5
                minimumIdle = 1
                connectionTimeout = 10000
                isAutoCommit = false
            }
            return HikariDataSource(config)
        }

        private fun createH2DataSource(): HikariDataSource {
            val config = HikariConfig().apply {
                jdbcUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
                username = "test"
                password = "test"
                maximumPoolSize = 5
                minimumIdle = 1
                connectionTimeout = 5000
                isAutoCommit = false
            }
            return HikariDataSource(config)
        }

        private fun initializeSchema() {
            dataSource!!.connection.use { conn ->
                val statement = conn.createStatement()

                // Create tables compatible with both PostgreSQL and H2
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
                statement.close()
            }
        }

        fun getSharedDataSource(): DataSource = dataSource
            ?: throw IllegalStateException("Database not initialized")

        fun isUsingPostgreSQL(): Boolean = !useH2Fallback
        fun isUsingH2(): Boolean = useH2Fallback
    }

    private var connection: Connection? = null
    private var savepoint: Savepoint? = null

    @BeforeEach
    open fun setUpTest() {
        // Get connection and start transaction for test isolation
        connection = getSharedDataSource().connection
        connection?.autoCommit = false
        savepoint = connection?.setSavepoint("test_savepoint")

        println("🔒 Database test isolation ready (${if (isUsingPostgreSQL()) "PostgreSQL" else "H2"})")

        setUpTestData()
        setUpTestSpecific()
    }

    @AfterEach
    open fun tearDownTest() {
        try {
            // Rollback to savepoint to clean up test data
            savepoint?.let { connection?.rollback(it) }
        } catch (e: Exception) {
            println("⚠️ Transaction rollback failed: ${e.message}")
        } finally {
            connection?.close()
            tearDownTestSpecific()
        }
    }

    // Template methods for subclasses
    protected open fun setUpTestSpecific() {}
    protected open fun tearDownTestSpecific() {}
    protected open fun setUpTestData() {}

    // Helper methods for database access
    protected fun getConnection(): Connection = connection
        ?: throw IllegalStateException("Connection not available")

    protected fun getDataSource(): DataSource = getSharedDataSource()

    protected fun getJdbcUrl(): String = if (isUsingPostgreSQL()) {
        postgresContainer.jdbcUrl
    } else {
        "jdbc:h2:mem:testdb"
    }

    protected fun getUsername(): String = "test"
    protected fun getPassword(): String = "test"
}
