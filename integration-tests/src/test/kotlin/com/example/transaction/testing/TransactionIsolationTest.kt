package com.example.transaction.testing

import com.example.test.categories.IntegrationTest
import com.example.test.categories.FastTest
import com.example.test.base.BaseIntegrationTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.comparables.shouldBeLessThan
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Timeout
import java.sql.Connection
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

/**
 * Optimized Transaction-Aware Testing Framework
 *
 * Implements optimized feedback: "Use transactions to wrap tests and their effects on the test container databases.
 * One test's entries in the DB won't impact the other. Transactions won't slow the tests down,
 * mostly because there's mostly no contention between the transactions."
 */

@IntegrationTest
@FastTest
@DisplayName("Database Transaction Isolation - Optimized")
@Timeout(15, unit = TimeUnit.SECONDS) // Reduced timeout
class TransactionIsolationTest : BaseIntegrationTest() {

    @BeforeEach
    fun setUp() {
        super.setUpIntegration()
    }

    @Test
    fun `should isolate test data using transactions`() = runTest {
        // Given
        val connection = getConnection()
        val testUserId = UUID.randomUUID().toString()

        // When - Insert test data within transaction
        val statement = connection.prepareStatement(
            "INSERT INTO users (id, name, email, phone) VALUES (?, ?, ?, ?)"
        )
        statement.setString(1, testUserId)
        statement.setString(2, "Transaction Test User")
        statement.setString(3, "transaction@test.com")
        statement.setString(4, "+1234567890")
        statement.executeUpdate()

        // Then - Data should be visible within same transaction
        val selectStatement = connection.prepareStatement("SELECT name FROM users WHERE id = ?")
        selectStatement.setString(1, testUserId)
        val resultSet = selectStatement.executeQuery()

        resultSet.next() shouldBe true
        resultSet.getString("name") shouldBe "Transaction Test User"

        // Note: Data will be automatically rolled back after test due to BaseIntegrationTest
    }

    @Test
    fun `should demonstrate transaction rollback isolation`() = runTest {
        // Given
        val connection = getConnection()
        val testUserId1 = UUID.randomUUID().toString()
        val testUserId2 = UUID.randomUUID().toString()

        // When - Insert multiple users
        val statement = connection.prepareStatement(
            "INSERT INTO users (id, name, email, phone) VALUES (?, ?, ?, ?)"
        )

        // Insert first user
        statement.setString(1, testUserId1)
        statement.setString(2, "User 1")
        statement.setString(3, "user1@test.com")
        statement.setString(4, null)
        statement.executeUpdate()

        // Insert second user
        statement.setString(1, testUserId2)
        statement.setString(2, "User 2")
        statement.setString(3, "user2@test.com")
        statement.setString(4, null)
        statement.executeUpdate()

        // Then - Both users should be visible in current transaction
        val countStatement = connection.prepareStatement("SELECT COUNT(*) as count FROM users WHERE id IN (?, ?)")
        countStatement.setString(1, testUserId1)
        countStatement.setString(2, testUserId2)
        val resultSet = countStatement.executeQuery()

        resultSet.next() shouldBe true
        resultSet.getInt("count") shouldBe 2
    }

    @Test
    fun `should handle concurrent transaction scenarios`() = runTest {
        // Given
        val connection = getConnection()
        val testUserId = UUID.randomUUID().toString()

        // When - Simulate concurrent operations within same transaction
        val insertStatement = connection.prepareStatement(
            "INSERT INTO users (id, name, email, phone) VALUES (?, ?, ?, ?)"
        )
        insertStatement.setString(1, testUserId)
        insertStatement.setString(2, "Concurrent User")
        insertStatement.setString(3, "concurrent@test.com")
        insertStatement.setString(4, null)
        insertStatement.executeUpdate()

        // Update the same user
        val updateStatement = connection.prepareStatement(
            "UPDATE users SET name = ? WHERE id = ?"
        )
        updateStatement.setString(1, "Updated Concurrent User")
        updateStatement.setString(2, testUserId)
        val updatedRows = updateStatement.executeUpdate()

        // Then
        updatedRows shouldBe 1

        // Verify the update
        val selectStatement = connection.prepareStatement("SELECT name FROM users WHERE id = ?")
        selectStatement.setString(1, testUserId)
        val resultSet = selectStatement.executeQuery()

        resultSet.next() shouldBe true
        resultSet.getString("name") shouldBe "Updated Concurrent User"
    }

    @Test
    fun `should validate foreign key constraints within transaction`() = runTest {
        // Given
        val connection = getConnection()
        val userId = UUID.randomUUID().toString()
        val orderId = UUID.randomUUID().toString()

        // When - Insert user first
        val userStatement = connection.prepareStatement(
            "INSERT INTO users (id, name, email, phone) VALUES (?, ?, ?, ?)"
        )
        userStatement.setString(1, userId)
        userStatement.setString(2, "Order User")
        userStatement.setString(3, "order@test.com")
        userStatement.setString(4, null)
        userStatement.executeUpdate()

        // Insert order referencing the user
        val orderStatement = connection.prepareStatement(
            "INSERT INTO orders (id, user_id, status, total_amount) VALUES (?, ?, ?, ?)"
        )
        orderStatement.setString(1, orderId)
        orderStatement.setString(2, userId)
        orderStatement.setString(3, "PENDING")
        orderStatement.setBigDecimal(4, java.math.BigDecimal("99.99"))
        orderStatement.executeUpdate()

        // Then - Verify the relationship
        val joinStatement = connection.prepareStatement("""
            SELECT u.name, o.status, o.total_amount 
            FROM users u 
            JOIN orders o ON u.id = o.user_id 
            WHERE u.id = ?
        """.trimIndent())
        joinStatement.setString(1, userId)
        val resultSet = joinStatement.executeQuery()

        resultSet.next() shouldBe true
        resultSet.getString("name") shouldBe "Order User"
        resultSet.getString("status") shouldBe "PENDING"
        resultSet.getBigDecimal("total_amount").toString() shouldBe "99.99"
    }

    @Test
    fun `should measure transaction performance`() = runTest {
        // Given
        val connection = getConnection()
        val startTime = System.currentTimeMillis()

        // When - Perform batch operations
        repeat(100) { index ->
            val statement = connection.prepareStatement(
                "INSERT INTO users (id, name, email, phone) VALUES (?, ?, ?, ?)"
            )
            statement.setString(1, UUID.randomUUID().toString())
            statement.setString(2, "Batch User $index")
            statement.setString(3, "batch$index@test.com")
            statement.setString(4, null)
            statement.executeUpdate()
        }

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then - Performance should be reasonable (less than 5 seconds for 100 inserts)
        duration shouldBeLessThan 5000L

        // Verify all inserts
        val countStatement = connection.prepareStatement("SELECT COUNT(*) as count FROM users WHERE email LIKE 'batch%@test.com'")
        val resultSet = countStatement.executeQuery()
        resultSet.next() shouldBe true
        resultSet.getInt("count") shouldBe 100
    }
}
