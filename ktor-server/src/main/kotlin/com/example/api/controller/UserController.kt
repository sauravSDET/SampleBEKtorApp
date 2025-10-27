package com.example.api.controller

import com.example.api.service.UserService
import com.example.user.api.models.*
import com.example.commons.api.models.*
import com.example.application.dto.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock

// Global user service instance for simplicity in tests
val userService = UserService()

// Add function to clear data for tests
fun clearUserServiceData() {
    userService.clearAll()
}

fun Routing.userRoutes() {
    route("/api/v1/users") {

        // POST /api/v1/users - Create user
        post {
            try {
                val request = call.receive<CreateUserRequest>()

                // Check for oversized payload
                if (request.name.length > 1000 || request.email.length > 500) {
                    call.respond(HttpStatusCode.PayloadTooLarge)
                    return@post
                }

                userService.createUser(request).fold(
                    onSuccess = { userResponse ->
                        val apiResponse = ApiResponse(
                            data = userResponse,
                            success = true,
                            timestamp = Clock.System.now()
                        )
                        call.respond(HttpStatusCode.Created, apiResponse)
                    },
                    onFailure = { exception ->
                        when (exception) {
                            is UserService.ValidationException -> {
                                call.respond(
                                    HttpStatusCode.BadRequest,
                                    ErrorResponse(
                                        message = exception.message ?: "Validation failed",
                                        code = "VALIDATION_ERROR",
                                        errors = exception.errors
                                    )
                                )
                            }
                            is UserService.ConflictException -> {
                                call.respond(
                                    HttpStatusCode.Conflict,
                                    ErrorResponse(
                                        message = exception.message ?: "Conflict occurred",
                                        code = "CONFLICT"
                                    )
                                )
                            }
                            else -> {
                                call.respond(
                                    HttpStatusCode.InternalServerError,
                                    ErrorResponse(
                                        message = "Internal server error",
                                        code = "INTERNAL_ERROR"
                                    )
                                )
                            }
                        }
                    }
                )
            } catch (_: Exception) {
                // Handle malformed JSON or missing required fields
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        message = "Invalid request format",
                        code = "BAD_REQUEST",
                        errors = mapOf("name" to "Name is required", "email" to "Email is required")
                    )
                )
            }
        }

        // GET /api/v1/users - List users with pagination
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

            val result = userService.listUsers(page, size)
            val users = result["users"] as? List<*> ?: emptyList<UserResponse>()
            val userResponses = users.filterIsInstance<UserResponse>()
            val totalCount = result["totalCount"] as? Int ?: 0
            val resultPage = result["page"] as? Int ?: page
            val resultSize = result["size"] as? Int ?: size

            val totalPages = if (totalCount > 0) (totalCount + resultSize - 1) / resultSize else 0

            val paginatedResponse = PaginatedResponse(
                items = userResponses,
                meta = PaginationMeta(
                    page = resultPage,
                    size = resultSize,
                    total = totalCount.toLong(),
                    totalPages = totalPages
                ),
                success = true,
                timestamp = Clock.System.now()
            )
            call.respond(HttpStatusCode.OK, paginatedResponse)
        }

        // GET /api/v1/users/{id} - Get user by ID
        get("/{id}") {
            val id = call.parameters["id"] ?: ""

            userService.getUserById(id).fold(
                onSuccess = { userResponse ->
                    val apiResponse = ApiResponse(
                        data = userResponse,
                        success = true,
                        timestamp = Clock.System.now()
                    )
                    call.respond(HttpStatusCode.OK, apiResponse)
                },
                onFailure = { exception ->
                    when (exception) {
                        is UserService.NotFoundException -> {
                            call.respond(
                                HttpStatusCode.NotFound,
                                ErrorResponse(
                                    message = exception.message ?: "User not found",
                                    code = "NOT_FOUND"
                                )
                            )
                        }
                        is UserService.BadRequestException -> {
                            call.respond(
                                HttpStatusCode.BadRequest,
                                ErrorResponse(
                                    message = exception.message ?: "Bad request",
                                    code = "BAD_REQUEST"
                                )
                            )
                        }
                        else -> {
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                ErrorResponse(
                                    message = "Internal server error",
                                    code = "INTERNAL_ERROR"
                                )
                            )
                        }
                    }
                }
            )
        }

        // PUT /api/v1/users/{id} - Update user
        put("/{id}") {
            val id = call.parameters["id"] ?: ""

            try {
                val request = call.receive<UpdateUserRequest>()

                userService.updateUser(id, request).fold(
                    onSuccess = { userResponse ->
                        val apiResponse = ApiResponse(
                            data = userResponse,
                            success = true,
                            timestamp = Clock.System.now()
                        )
                        call.respond(HttpStatusCode.OK, apiResponse)
                    },
                    onFailure = { exception ->
                        when (exception) {
                            is UserService.ValidationException -> {
                                call.respond(
                                    HttpStatusCode.BadRequest,
                                    ErrorResponse(
                                        message = exception.message ?: "Validation failed",
                                        code = "VALIDATION_ERROR",
                                        errors = exception.errors
                                    )
                                )
                            }
                            is UserService.NotFoundException -> {
                                call.respond(
                                    HttpStatusCode.NotFound,
                                    ErrorResponse(
                                        message = exception.message ?: "User not found",
                                        code = "NOT_FOUND"
                                    )
                                )
                            }
                            is UserService.ConflictException -> {
                                call.respond(
                                    HttpStatusCode.Conflict,
                                    ErrorResponse(
                                        message = exception.message ?: "Conflict occurred",
                                        code = "CONFLICT"
                                    )
                                )
                            }
                            is UserService.BadRequestException -> {
                                call.respond(
                                    HttpStatusCode.BadRequest,
                                    ErrorResponse(
                                        message = exception.message ?: "Bad request",
                                        code = "BAD_REQUEST"
                                    )
                                )
                            }
                            else -> {
                                call.respond(
                                    HttpStatusCode.InternalServerError,
                                    ErrorResponse(
                                        message = "Internal server error",
                                        code = "INTERNAL_ERROR"
                                    )
                                )
                            }
                        }
                    }
                )
            } catch (_: Exception) {
                // Handle malformed JSON or missing required fields
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        message = "Invalid request format",
                        code = "BAD_REQUEST"
                    )
                )
            }
        }
    }
}
