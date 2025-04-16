package com.radon.authguard.domain

import android.app.Application
import android.content.Context
import com.radon.authguard.core.di.DiContainer
import com.radon.authguard.domain.data.AuthConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


object AuthGuard  {

    lateinit var config: AuthConfig
    lateinit var tokenManager: TokenManager
    lateinit var container: DiContainer


    fun initialize(
        context: Context,
        configuration: AuthConfig,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    ) {

        val appContext = context.applicationContext as Application

        val logging = HttpLoggingInterceptor()

        clientBuilder.addInterceptor(logging)

        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        config = configuration
        tokenManager = TokenManager(appContext)
        container = DiContainer(clientBuilder)

    }

}