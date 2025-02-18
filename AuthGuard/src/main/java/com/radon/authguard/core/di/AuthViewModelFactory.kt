package com.radon.authguard.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.radon.authguard.data.remote.AuthService
import com.radon.authguard.domain.TokenManager
import com.radon.authguard.ui.viewmodel.AuthViewModel

class AuthViewModelFactory(
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}