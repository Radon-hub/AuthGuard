package com.radon.authguard.core.di

import com.radon.authguard.domain.remote.RepositoryIMP
import com.radon.authguard.ui.viewmodel.AuthViewModel

class AuthViewModelFactory(private val authRepositoryIMP: RepositoryIMP) : Factory<AuthViewModel> {
    override fun create():AuthViewModel{
        return AuthViewModel(authRepositoryIMP)
    }
}