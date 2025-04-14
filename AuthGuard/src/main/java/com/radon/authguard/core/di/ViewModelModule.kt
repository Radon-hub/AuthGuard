package com.radon.authguard.core.di

import com.radon.authguard.ui.viewmodel.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel { AuthViewModel(get()) }
}