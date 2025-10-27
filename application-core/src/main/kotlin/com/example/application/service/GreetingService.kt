package com.example.application.service

import com.example.domain.model.Greeting
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GreetingService @Inject constructor() {
    fun createGreeting(message: String): Greeting {
        val id = generateId()
        return Greeting(id, message)
    }

    private fun generateId(): String {
        return java.util.UUID.randomUUID().toString()
    }
}
