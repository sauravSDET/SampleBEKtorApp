package com.example.di

import com.example.domain.repository.UserRepository
import com.example.domain.repository.OrderRepository
import com.example.application.service.GreetingService
import javax.inject.Singleton

// Manual dependency injection replacing Dagger
@Singleton
class AppComponent {
    private val userRepository: UserRepository by lazy {
        AppModule.provideUserRepository()
    }
    private val orderRepository: OrderRepository by lazy {
        AppModule.provideOrderRepository()
    }
    private val greetingService: GreetingService by lazy {
        AppModule.provideGreetingService()
    }

    fun userRepository(): UserRepository = userRepository
    fun orderRepository(): OrderRepository = orderRepository
    fun greetingService(): GreetingService = greetingService
}
