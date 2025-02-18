package com.radon.authguard.domain
import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

class TokenManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_auth_tokens",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        _isLoggedIn.value = getAccessToken()?.let { !isTokenExpired(it) } ?: false
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPrefs.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .apply()
        _isLoggedIn.value = true
    }

    fun getAccessToken(): String? = sharedPrefs.getString("access_token", null)
    fun getRefreshToken(): String? = sharedPrefs.getString("refresh_token", null)

    fun clearTokens() {
        sharedPrefs.edit().clear().apply()
        _isLoggedIn.value = false
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            val payload = token.split(".")[1]
            val json = String(
                Base64.decode(payload, Base64.URL_SAFE),
                Charsets.UTF_8
            )
            val exp = Json.parseToJsonElement(json)
                .jsonObject["exp"]?.jsonPrimitive?.long
                ?: return true

            exp < System.currentTimeMillis() / 1000
        } catch (e: Exception) {
            true
        }
    }
}