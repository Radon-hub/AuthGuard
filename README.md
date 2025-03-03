# AuthGuard 🔒  
**v1.2.0 | Secure Authentication Made Simple for Android**  
https://jitpack.io/#Radon-hub/AuthGuard

A robust authentication library handling token management, adaptive login flows, and secure API integration for Android applications.

---

## Features ✨
- 🛠️ Flexible authentication forms (email/password, phone/PIN, MFA)
- 🔐 Secure token storage and automatic injection
- ♻️ Silent token refresh & expiration handling
- 📱 Device metadata auto-attachment
- � Comprehensive error handling
- 🛡️ Biometric authentication support
- 🌀 Jetpack Compose compatible

---

## Installation 📦
Add to your **settings.gradle**:
```groovy
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}
```
Add to your **app/build.gradle**:
```groovy
dependencies {
	        implementation("com.github.Radon-hub:AuthGuard:1.2.0")
	}
```
---


## Quick Start 🚀
1. Initialization
```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthGuard.initialize(
            configuration = AuthConfig(
                baseUrl = "https://api.example.com/",
                onTokenInvalid = { /* Handle session expiration */ },
                additionalLoginParams = {
                    mapOf(
                        "device_id" to Settings.Secure.getString(
                            contentResolver,
                            Settings.Secure.ANDROID_ID
                        )
                    )
                }
            )
        )
    }
}

```

2. Basic Login Flow
```kotlin
@Composable
fun LoginScreen() {
    AdaptiveAuthForm(
        onSuccess = { navController.navigate("home") },
        onError = { error -> showToast(error.message) }
    )
}
```
3. API Integration
```kotlin
interface UserApi {
    @GET("user/profile")
    @Authenticated // Auto-injects token
    suspend fun getProfile(): UserProfile
}
```
---

## Advanced Usage 🔧
Custom Login Forms

```kotlin
AdaptiveAuthForm(
    config = AuthFormConfig(
        fields = listOf(
            FormFieldConfig(
                key = "phone",
                label = "Phone Number",
                component = { value, onChange -> 
                    PhoneNumberField(value, onChange) 
                }
            ),
            FormFieldConfig(
                key = "pin",
                label = "6-digit PIN",
                component = { value, onChange ->
                    PinInput(value, onChange) 
                }
            )
        )
    )
)
```
Manual Token Management
```kotlin
// Get current token
val accessToken = AuthGuard.tokenManager.getAccessToken()

// Force token refresh
viewModelScope.launch {
    try {
        AuthGuard.authService.refreshToken()
    } catch (e: AuthException) {
        handleTokenRefreshFailure()
    }
}
```
Authentication State Observer
```kotlin
@Composable
fun ProtectedScreen() {
    val isLoggedIn by AuthGuard.tokenManager.isLoggedIn.collectAsState()
    
    if (!isLoggedIn) RedirectToLogin()
    else UserDashboard()
}
```
---
## Security Features 🛡️
Automatic Device Metadata
```kotlin

AuthConfig(
    additionalLoginParams = { context ->
        mapOf(
            "device" : mapOf(
                "model" to Build.MODEL,
                "os_version" to Build.VERSION.RELEASE
            ),
            "app_version" : BuildConfig.VERSION_NAME
        )
    }
)
```
## Biometric Authentication
```kotlin
FormFieldConfig(
    key = "biometric",
    label = "Fingerprint",
    component = { _, onChange ->
        BiometricPrompt(
            onSuccess = { onChange("verified") },
            onError = { onChange("failed") }
        )
    }
)
```
---
## Error Handling ⚠️
```kotlin
class AuthRepository {
    suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: IOException) {
            when {
                e.isNetworkError() -> Result.failure(NetworkException())
                e.isTokenExpired() -> {
                    forceTokenRefresh()
                    safeCall(block) // Retry after refresh
                }
                else -> Result.failure(UnknownAuthException())
            }
        }
    }
}
```
---
## Best Practices ✅
🔄 Set token expiration to 15-30 minutes

📡 Always use HTTPS for authentication endpoints

🗑️ Clear tokens immediately on logout

📲 Combine with Android's credential manager for passwordless flows

📊 Monitor authentication failures through analytics

---
## Documentation 📚

AdaptiveAuthForm -> Smart login form composable

TokenManager -> Encrypted token storage & rotation

AuthInterceptor ->	Automatic header injection

AuthService	Token -> refresh & validation service


---
## Contribution 🤝
Found a bug or have a feature request? Open an issue or submit a PR!
