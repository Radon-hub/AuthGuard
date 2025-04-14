package com.radon.authguard.data.remote

import android.util.Log
import com.radon.authguard.domain.TokenManager
import com.radon.authguard.core.utils.Authenticated
import com.radon.authguard.domain.AuthGuard
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
            Log.e("AuthViewModel", "process with tokens in interceptor")
            proceedWithToken(request, chain)
        } catch (e: IOException) {
            return handleTokenError(e, chain, request)
        }
        Log.e("AuthViewModel", "response : ${response.code}")

        // Handle 401
        if (response.code == 401) {
            return handleUnauthorized(chain, request)
        }

        Log.e("AuthViewModel",  "come to end $response")

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

        Log.e("AuthViewModel", "come in refresh token")

        runBlocking {

            val refreshField = mapOf((AuthGuard.tokenManager.getRefreshKey() ?: "") to (AuthGuard.tokenManager.getRefreshToken() ?: ""))

            val combinedParams = refreshField + AuthGuard.config.additionalRefreshParams()

            val response = authService.refreshToken(
                "${config.baseUrl}${config.refreshEndpoint}",
                    combinedParams
            )

            var ResponseaccessToken = ""
            var ResponserefreshToken = ""

            response.body()?.forEach { map ->
                if(map.key.contains("access")){
                    ResponseaccessToken = map.value.toString()
                    Log.e("AuthViewModel", "this is access $map")
                }
                if(map.key.contains("refresh")){
                    ResponserefreshToken = map.value.toString()
                    Log.e("AuthViewModel", "this is refresh $map")
                }
            }

            Log.e("AuthViewModel", "getted token from refresh : access:$ResponseaccessToken refresh:$ResponserefreshToken")

            tokenManager.saveTokens(
                ResponseaccessToken,
                ResponserefreshToken
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
