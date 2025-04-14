package com.radon.authguard.ui.viewmodel
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radon.authguard.data.repository.AuthRepository
import com.radon.authguard.domain.AuthGuard
import com.radon.authguard.domain.AuthGuard.config
import kotlinx.coroutines.launch

class AuthViewModel(private val repository:AuthRepository) : ViewModel() {
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

                val response = AuthGuard.authService.refreshToken(
                    "${config.baseUrl}${config.refreshEndpoint}",
                    combinedParams
                )

                Log.e("AuthViewModel", "refresh / " + response.toString())
                val responseMap = response.body()

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

                }

                onSuccess()
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

                val response = AuthGuard.authService.login(
                    "${config.baseUrl}${config.loginEndpoint}",
                    combinedParams
                )

                Log.e("AuthViewModel", response.toString())
                val responseMap = response.body()

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

                }

                onSuccess()
            } catch (e: Exception) {
                error = e.message ?: "Login failed"
                Log.e("AuthViewModel", e.toString() + " / " + e.message)
            } finally {
                isLoading = false
            }
        }
    }
}