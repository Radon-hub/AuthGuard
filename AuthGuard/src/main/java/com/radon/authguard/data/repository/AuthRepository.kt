package com.radon.authguard.data.repository

import com.radon.authguard.data.remote.AuthService
import retrofit2.Response

class AuthRepository(private val service:AuthService) {
//    suspend fun login(url: String,user:Map<String, String>): Response<Map<String, String>> {
//        return service.login(url, user)
//    }
//    suspend fun getUser(url: String): Response<Map<String, String>> {
//        return service.getUser(url)
//    }
}