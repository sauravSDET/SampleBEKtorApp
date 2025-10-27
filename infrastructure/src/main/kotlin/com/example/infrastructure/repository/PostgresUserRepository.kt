package com.example.infrastructure.repository

import com.example.domain.model.*
import com.example.domain.repository.UserRepository
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostgresUserRepository @Inject constructor(
    private val connection: Connection
) : UserRepository {

    override suspend fun save(user: User): User {
        val sql = """
            INSERT INTO users (id, email, first_name, last_name, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                email = EXCLUDED.email,
                first_name = EXCLUDED.first_name,
                last_name = EXCLUDED.last_name,
                updated_at = EXCLUDED.updated_at
        """.trimIndent()

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, user.id.value)
            statement.setString(2, user.email.value)
            statement.setString(3, user.firstName)
            statement.setString(4, user.lastName)
            statement.setTimestamp(5, Timestamp.from(user.createdAt.toJavaInstant()))
            statement.setTimestamp(6, Timestamp.from(user.updatedAt.toJavaInstant()))
            statement.executeUpdate()
        }

        return user
    }

    override suspend fun findById(id: UserId): User? {
        val sql = "SELECT * FROM users WHERE id = ?"

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, id.value)
            val resultSet = statement.executeQuery()

            return if (resultSet.next()) {
                mapResultSetToUser(resultSet)
            } else {
                null
            }
        }
    }

    override suspend fun findByEmail(email: Email): User? {
        val sql = "SELECT * FROM users WHERE email = ?"

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, email.value)
            val resultSet = statement.executeQuery()

            return if (resultSet.next()) {
                mapResultSetToUser(resultSet)
            } else {
                null
            }
        }
    }

    override suspend fun findAll(page: Int, size: Int): List<User> {
        val offset = page * size
        val sql = "SELECT * FROM users ORDER BY created_at DESC LIMIT ? OFFSET ?"

        connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, size)
            statement.setInt(2, offset)
            val resultSet = statement.executeQuery()

            val users = mutableListOf<User>()
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet))
            }
            return users
        }
    }

    override suspend fun count(): Long {
        val sql = "SELECT COUNT(*) FROM users"
        connection.prepareStatement(sql).use { statement ->
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                resultSet.getLong(1)
            } else {
                0L
            }
        }
    }

    override suspend fun delete(id: UserId): Boolean {
        val sql = "DELETE FROM users WHERE id = ?"

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, id.value)
            val rowsAffected = statement.executeUpdate()
            return rowsAffected > 0
        }
    }

    fun findUsersCreatedAfter(date: kotlinx.datetime.Instant): List<User> {
        val sql = "SELECT * FROM users WHERE created_at > ? ORDER BY created_at DESC"

        connection.prepareStatement(sql).use { statement ->
            statement.setTimestamp(1, Timestamp.from(date.toJavaInstant()))
            val resultSet = statement.executeQuery()

            val users = mutableListOf<User>()
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet))
            }
            return users
        }
    }

    fun findUsersByEmailDomain(domain: String): List<User> {
        val sql = "SELECT * FROM users WHERE email LIKE ? ORDER BY created_at DESC"

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, "%@$domain")
            val resultSet = statement.executeQuery()

            val users = mutableListOf<User>()
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet))
            }
            return users
        }
    }

    fun countUsers(): Int {
        val sql = "SELECT COUNT(*) FROM users"

        connection.prepareStatement(sql).use { statement ->
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                resultSet.getInt(1)
            } else {
                0
            }
        }
    }

    fun clear() {
        val sql = "DELETE FROM users"
        connection.prepareStatement(sql).use { statement ->
            statement.executeUpdate()
        }
    }

    private fun mapResultSetToUser(resultSet: ResultSet): User {
        return User(
            id = UserId(resultSet.getString("id")),
            email = Email(resultSet.getString("email")),
            firstName = resultSet.getString("first_name"),
            lastName = resultSet.getString("last_name"),
            createdAt = resultSet.getTimestamp("created_at").toInstant().toKotlinInstant(),
            updatedAt = resultSet.getTimestamp("updated_at").toInstant().toKotlinInstant()
        )
    }
}
