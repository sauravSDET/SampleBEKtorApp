package com.example.di

import com.example.application.service.GreetingService
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.UserRepository
import com.example.infrastructure.kafka.KafkaEventProducer
import com.example.infrastructure.repository.PostgresOrderRepository
import com.example.infrastructure.repository.PostgresUserRepository
import java.sql.Connection
import java.sql.DriverManager
import javax.inject.Singleton

// Simplified manual dependency injection - temporarily disable complex services
@Singleton
object AppModule {

    fun provideDatabaseConnection(): Connection {
        // In production, these should come from configuration/environment variables
        val jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
        val username = System.getenv("DATABASE_USERNAME") ?: "sa"
        val password = System.getenv("DATABASE_PASSWORD") ?: ""
        return DriverManager.getConnection(jdbcUrl, username, password)
    }

    fun provideUserRepository(): UserRepository {
        return PostgresUserRepository(provideDatabaseConnection())
    }

    fun provideOrderRepository(): OrderRepository {
        return PostgresOrderRepository()
    }

    fun provideKafkaEventProducer(): KafkaEventProducer {
        return KafkaEventProducer()
    }

    fun provideGreetingService(): GreetingService {
        return GreetingService()
    }

    // Remove problematic application service implementations
    // These will be handled directly in the application layer for now
}
