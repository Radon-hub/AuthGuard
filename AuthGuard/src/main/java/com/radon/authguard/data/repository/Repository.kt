package com.radon.authguard.data.repository

interface Repository{
    suspend fun login(params:Map<String, String>,onSuccess:()->Unit,onFailure:(String)->Unit)
    suspend fun refresh(params:Map<String, String>,onSuccess:()->Unit,onFailure:(String)->Unit)
}