package com.radon.authguard.domain.remote

import android.util.Log
import com.radon.authguard.data.remote.AuthService
import com.radon.authguard.data.repository.Repository
import com.radon.authguard.domain.AuthGuard
import com.radon.authguard.domain.AuthGuard.config

class RepositoryIMP(private val authService: AuthService):Repository {

    override suspend fun login(
        params: Map<String, String>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {

        val response = authService.login(
            "${config.baseUrl}${config.loginEndpoint}",
            params
        )

        Log.e("AuthViewModel", response.toString())

        val responseMap = response.body()

        if(response.code() !in (200..299)){
            onFailure("Bad Request : ${response.code()} / ${response.body()}")
            return
        }

        responseMap?.let { map ->

            var accessToken = ""
            var refreshToken = ""

            for(it in map) {
                if(it.key.contains("access")){
                    accessToken = it.value.toString()
                    Log.e("AuthViewModel", "this is access $it")
                    AuthGuard.tokenManager.saveKeys(accessKey = it.key)
                }
                if(it.key.contains("refresh")){
                    refreshToken = it.value.toString()
                    Log.e("AuthViewModel", "this is refresh $it")
                    AuthGuard.tokenManager.saveKeys(refreshKey = it.key)
                }
            }

            Log.e("AuthViewModel", "this is the mapped response : $map")

            AuthGuard.tokenManager.saveTokens(
                accessToken,
                refreshToken
            )
            onSuccess()
        }

    }

    override suspend fun refresh(
        params: Map<String, String>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {

        val response = authService.refreshToken(
            "${config.baseUrl}${config.refreshEndpoint}",
            params
        )

        Log.e("AuthViewModel", "refresh / " + response.toString())
        val responseMap = response.body()

        if(response.code() !in (200..299)){
            onFailure("Bad Request : ${response.code()} / ${response.body()}")
            return
        }

        responseMap?.let { map ->

            var accessToken = ""
            var refreshToken = ""

            for(it in map) {
                if(it.key.contains("access")){
                    accessToken = it.value.toString()
                    Log.e("AuthViewModel", "refresh / this is access $it")
                }
                if(it.key.contains("refresh")){
                    refreshToken = it.value.toString()
                    Log.e("AuthViewModel", "refresh / this is refresh $it")
                }
            }

            Log.e("AuthViewModel", "refresh / this is the mapped response : $map")

            AuthGuard.tokenManager.saveTokens(
                accessToken,
                refreshToken
            )

            onSuccess()
        }


    }
}