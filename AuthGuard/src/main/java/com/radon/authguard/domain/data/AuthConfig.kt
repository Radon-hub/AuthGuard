package com.radon.authguard.domain.data

data class AuthConfig(
    val baseUrl: String,
    val loginEndpoint: String = "auth/login",
    val refreshEndpoint: String = "auth/refresh",
    val connectTimeoutMillis: Long = 30_000,
    val readTimeoutMillis: Long = 30_000,
    val onTokenInvalid: () -> Unit = {}
)