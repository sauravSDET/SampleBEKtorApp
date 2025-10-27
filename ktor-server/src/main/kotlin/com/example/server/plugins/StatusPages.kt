package com.example.server.plugins

import com.example.application.dto.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.datetime.Clock
import kotlinx.serialization.SerializationException

fun Application.configureStatusPages() {
    install(StatusPages) {
        // Handle validation errors
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    code = "VALIDATION_ERROR",
                    message = cause.message ?: "Invalid input",
                    timestamp = Clock.System.now().toString()
                )
            )
        }

        // Handle serialization errors (malformed JSON)
        exception<SerializationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    code = "INVALID_JSON",
                    message = "Invalid JSON format",
                    timestamp = Clock.System.now().toString()
                )
            )
        }

        // Handle generic exceptions
        exception<Exception> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    code = "INTERNAL_ERROR",
                    message = "An unexpected error occurred",
                    timestamp = Clock.System.now().toString()
                )
            )
        }

        // Handle 404 Not Found
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    code = "NOT_FOUND",
                    message = "The requested resource was not found",
                    timestamp = Clock.System.now().toString()
                )
            )
        }

        // Handle 405 Method Not Allowed
        status(HttpStatusCode.MethodNotAllowed) { call, _ ->
            call.respond(
                HttpStatusCode.MethodNotAllowed,
                ErrorResponse(
                    code = "METHOD_NOT_ALLOWED",
                    message = "HTTP method not allowed for this endpoint",
                    timestamp = Clock.System.now().toString()
                )
            )
        }
    }
}
