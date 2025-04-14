package com.radon.authguard.core.di

import com.radon.authguard.data.repository.AuthRepository
import org.koin.dsl.module

val RepositoryModule = module {
    single { AuthRepository(get()) }
}