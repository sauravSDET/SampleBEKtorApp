package com.example.server

import com.example.api.controller.healthRoutes
import com.example.api.controller.userRoutes
import com.example.api.controller.orderRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.metrics.micrometer.*
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import java.util.UUID
import kotlinx.serialization.json.Json

import kotlinx.datetime.Clock
fun Application.module() {
    // Install CallLogging with correlation ID generation
    install(CallLogging) {
        level = Level.INFO
        mdc("correlationId") { call ->
            call.request.header("X-Request-ID") ?: UUID.randomUUID().toString()
        }
    }

    // Install Micrometer metrics
    val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        registry = prometheusRegistry
    }

    // Configure JSON serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }

    // Configure routing
    routing {
        userRoutes()
        orderRoutes()

        // Metrics endpoint
        get("/metrics") {
            call.respond(prometheusRegistry.scrape())
        }

        // Simple health endpoints
        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "UP"))
        }

        get("/health/readiness") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "UP"))
        }

        get("/health/liveness") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "UP"))
        }
    }

    // Configure health routes outside of main routing
    healthRoutes()
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
