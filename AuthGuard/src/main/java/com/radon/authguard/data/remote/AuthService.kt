package com.radon.authguard.data.remote
import com.radon.authguard.core.utils.Authenticated
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface AuthService {
    @POST
    suspend fun login(
        @Url url: String,
        @Body parameters: Map<String, String> // Accept dynamic key-value pairs
    ): Response<Map<String, Any>>

    @POST
    suspend fun refreshToken(
        @Url url: String,
        @Body request: Map<String, String>
    ): Response<Map<String, Any>>
}