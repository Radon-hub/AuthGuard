package com.radon.authguard.ui.viewmodel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radon.authguard.domain.AuthGuard
import com.radon.authguard.domain.AuthGuard.config
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    val formFields = mutableStateMapOf<String, Any>() // Stores dynamic parameters
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun updateField(key: String, value: Any) {
        formFields[key] = value
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val combinedParams = formFields.toMap() + config.additionalLoginParams()
                val response = AuthGuard.authService.login(
                    "${config.baseUrl}${config.loginEndpoint}",
                    combinedParams
                )
                AuthGuard.tokenManager.saveTokens(
                    response["access_token"]!!,
                    response["refresh_token"]!!
                )
                onSuccess()
            } catch (e: Exception) {
                error = e.message ?: "Login failed"
            } finally {
                isLoading = false
            }
        }
    }
}