package com.radon.authguard.core.di

import com.radon.authguard.data.remote.AuthInterceptor
import com.radon.authguard.data.remote.AuthService
import com.radon.authguard.domain.AuthGuard.config
import com.radon.authguard.domain.AuthGuard.tokenManager
import com.radon.authguard.domain.remote.RepositoryIMP
import com.radon.authguard.ui.viewmodel.AuthViewModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DiContainer(
    clientBuilder: OkHttpClient.Builder
) {

    private val authInterceptor = AuthInterceptor(
        tokenManager,
        createAuthService(clientBuilder),
        config
    )

    private var client: OkHttpClient = clientBuilder
        .addInterceptor(authInterceptor)
        .connectTimeout(config.connectTimeoutMillis, TimeUnit.MILLISECONDS)
        .readTimeout(config.readTimeoutMillis, TimeUnit.MILLISECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(config.baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private var authService: AuthService = retrofit.create(AuthService::class.java)

    private val repository = RepositoryIMP(authService)

    val authViewModelFactory = AuthViewModelFactory(repository)


    private fun createAuthService(clientBuilder: OkHttpClient.Builder): AuthService {
        return Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

}
