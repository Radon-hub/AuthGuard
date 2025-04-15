package com.radon.authguard.domain

import android.app.Application
import android.content.Context
import com.radon.authguard.core.di.ApiModule
import com.radon.authguard.core.di.RepositoryModule
import com.radon.authguard.core.di.RetrofitModule
import com.radon.authguard.core.di.ViewModelModule
import com.radon.authguard.data.remote.AuthInterceptor
import com.radon.authguard.data.remote.AuthService
import com.radon.authguard.domain.data.AuthConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object AuthGuard  {
    lateinit var tokenManager: TokenManager
    lateinit var authService: AuthService
    lateinit var config: AuthConfig
    lateinit var client: OkHttpClient


    fun initialize(
        context: Context,
        configuration: AuthConfig,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    ) {

        val appContext = context.applicationContext as Application

        startKoin {
            androidLogger()
            androidContext(appContext)
            modules(
                ApiModule,
                RepositoryModule,
                RetrofitModule,
                ViewModelModule
            )
        }

        val logging = HttpLoggingInterceptor()

        clientBuilder.addInterceptor(logging)

        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        config = configuration
        tokenManager = TokenManager(appContext)

        val authInterceptor = AuthInterceptor(
            tokenManager,
            createAuthService(clientBuilder),
            config
        )

        client = clientBuilder
            .addInterceptor(authInterceptor)
            .connectTimeout(config.connectTimeoutMillis, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeoutMillis, TimeUnit.MILLISECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authService = retrofit.create(AuthService::class.java)
    }

    private fun createAuthService(clientBuilder: OkHttpClient.Builder): AuthService {
        return Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}