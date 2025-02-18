package com.radon.authguard.data.remote

import com.radon.authguard.domain.TokenManager
import com.radon.authguard.core.utils.Authenticated
import com.radon.authguard.domain.data.AuthConfig
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import java.io.IOException


class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val authService: AuthService,
    private val config: AuthConfig
) : Interceptor {

    private var retryCount = 0

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val annotation = request.tag(Invocation::class.java)
            ?.method()?.getAnnotation(Authenticated::class.java)

        if (annotation == null) return chain.proceed(request)

        // First attempt
        val response = try {
            proceedWithToken(request, chain)
        } catch (e: IOException) {
            return handleTokenError(e, chain, request)
        }

        // Handle 401
        if (response.code == 401) {
            return handleUnauthorized(chain, request)
        }

        return response
    }

    private fun proceedWithToken(request: Request, chain: Interceptor.Chain): Response {
        val token = tokenManager.getAccessToken()
            ?.takeUnless { tokenManager.isTokenExpired(it) }
            ?: throw IOException("Invalid token")

        return chain.proceed(
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        )
    }

    private fun handleUnauthorized(chain: Interceptor.Chain, originalRequest: Request): Response {
        if (retryCount++ > 0) {
            config.onTokenInvalid()
            throw IOException("Maximum retry attempts reached")
        }

        return try {
            refreshToken()
            chain.proceed(
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer ${tokenManager.getAccessToken()}")
                    .build()
            )
        } catch (e: Exception) {
            config.onTokenInvalid()
            throw IOException("Token refresh failed: ${e.message}")
        }
    }

    private fun refreshToken() {
        val refreshToken = tokenManager.getRefreshToken()
            ?: throw IOException("No refresh token available")

        runBlocking {
            val response = authService.refreshToken(
                "${config.baseUrl}${config.refreshEndpoint}",
                mapOf("refresh_token" to refreshToken)
            )
            tokenManager.saveTokens(
                response["access_token"] ?: throw IOException("Invalid token response"),
                response["refresh_token"] ?: throw IOException("Invalid token response")
            )
        }
    }

    private fun handleTokenError(e: IOException, chain: Interceptor.Chain, request: Request): Response {
        return if (e.message == "Invalid token") {
            handleUnauthorized(chain, request)
        } else {
            throw e
        }
    }
}
