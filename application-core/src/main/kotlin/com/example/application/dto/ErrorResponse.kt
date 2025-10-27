package com.example.application.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val code: String? = null,
    val errors: Map<String, String>? = null,
    val timestamp: String = kotlinx.datetime.Clock.System.now().toString()
)
