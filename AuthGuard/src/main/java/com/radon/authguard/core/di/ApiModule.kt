package com.radon.authguard.core.di

import com.radon.authguard.data.remote.AuthService
import org.koin.dsl.module
import retrofit2.Retrofit

val ApiModule = module {
    single(createdAtStart = false) { get<Retrofit>().create(AuthService::class.java) }
}