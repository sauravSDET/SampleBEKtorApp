package com.example.application.service

import com.example.domain.model.*
import com.example.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend fun createUser(email: Email, firstName: String, lastName: String): User {
        // Check if user already exists
        val existingUser = userRepository.findByEmail(email)
        if (existingUser != null) {
            throw IllegalArgumentException("User with email ${email.value} already exists")
        }

        // Create and save new user
        val user = User.create(email, firstName, lastName)
        return userRepository.save(user)
    }

    suspend fun getUserById(id: UserId): User? {
        return userRepository.findById(id)
    }

    suspend fun getUserByEmail(email: Email): User? {
        return userRepository.findByEmail(email)
    }

    suspend fun updateUser(id: UserId, firstName: String?, lastName: String?): User? {
        val existingUser = userRepository.findById(id) ?: return null

        val updatedUser = existingUser.updateProfile(
            newFirstName = firstName ?: existingUser.firstName,
            newLastName = lastName ?: existingUser.lastName
        )

        return userRepository.save(updatedUser)
    }

    suspend fun getAllUsers(page: Int = 0, size: Int = 10): List<User> {
        return userRepository.findAll(page, size)
    }

    suspend fun deleteUser(id: UserId): Boolean {
        return userRepository.delete(id)
    }

    suspend fun getUserCount(): Long {
        return userRepository.count()
    }
}
