package com.radon.authguard.data.remote
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface AuthService {
    @POST
    suspend fun login(
        @Url url: String,
        @Body parameters: Map<String, Any> // Accept dynamic key-value pairs
    ): Map<String, String>

    @POST
    suspend fun refreshToken(
        @Url url: String,
        @Body request: Map<String, String>
    ): Map<String, String>
}