package com.radon.authguard.ui.viewmodel
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radon.authguard.domain.AuthGuard
import com.radon.authguard.domain.AuthGuard.config
import com.radon.authguard.domain.remote.RepositoryIMP
import kotlinx.coroutines.launch

class AuthViewModel(val repository: RepositoryIMP) : ViewModel() {
    val formFields = mutableStateMapOf<String, String>() // Stores dynamic parameters
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun updateField(key: String, value: String) {
        formFields[key] = value
    }


    fun refresh(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {

                val refreshField = mapOf((AuthGuard.tokenManager.getRefreshKey() ?: "") to (AuthGuard.tokenManager.getRefreshToken() ?: ""))

                val combinedParams = refreshField + config.additionalRefreshParams()

                Log.e("AuthViewModel",  "refresh / the combination $combinedParams")

                repository.refresh(
                    params = combinedParams,
                    onSuccess = {
                        onSuccess.invoke()
                    },
                    onFailure = {
                        throw Exception(it)
                    }
                )

            } catch (e: Exception) {
                error = e.message ?: "Login failed"
                Log.e("AuthViewModel", e.toString() + " / " + e.message)
            } finally {
                isLoading = false
            }
        }
    }



    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val combinedParams = formFields.toMap() + config.additionalLoginParams()

                Log.e("AuthViewModel", "${config.baseUrl}${config.loginEndpoint}")
                Log.e("AuthViewModel",  "the combination $combinedParams")

                repository.login(
                    params = combinedParams,
                    onSuccess = {
                        onSuccess.invoke()
                    },
                    onFailure = {
                        throw Exception(it)
                    }
                )

            } catch (e: Exception) {
                error = e.message ?: "Login failed"
                Log.e("AuthViewModel", e.toString() + " / " + e.message)
            } finally {
                isLoading = false
            }
        }
    }
}