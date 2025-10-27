package com.example.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.healthRoutes() {
    routing {
        get("/health") {
            call.respondText("OK", ContentType.Text.Plain, HttpStatusCode.OK)
        }

        get("/health/check") {
            call.respondText(
                """{"status":"UP","timestamp":${System.currentTimeMillis()}}""",
                ContentType.Application.Json,
                HttpStatusCode.OK
            )
        }
    }
}
