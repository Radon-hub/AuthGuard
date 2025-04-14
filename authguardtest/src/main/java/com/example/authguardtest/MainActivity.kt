package com.example.authguardtest

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.authguardtest.ui.theme.AuthenticationTheme
import com.radon.authguard.domain.AuthGuard
import com.radon.authguard.domain.data.AuthConfig
import com.radon.authguard.ui.AdaptiveAuthForm
import com.radon.authguard.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        AuthGuard.initialize(
            context = baseContext,
            configuration = AuthConfig(
                baseUrl = "https://dummyjson.com/",
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


        setContent {
            AuthenticationTheme {
                KoinAndroidContext{

                    val viewModel:AuthViewModel = koinViewModel()

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

                            AdaptiveAuthForm(
                                onError = { error ->
                                    Log.e("TAGAM",error)
                                },
                                onSuccess = {
                                    Log.e("TAGAM","the user successfully logged in")
                                },
                                viewModel = viewModel
                            )


                            Button(
                                onClick = {
                                    Log.e("AuthViewModel", "This is saved access token : ${AuthGuard.tokenManager.getAccessToken()}")
                                    Log.e("AuthViewModel", "This is saved refresh token : ${AuthGuard.tokenManager.getRefreshToken()}")
                                }
                            ) {
                                Text("Show Saves")
                            }

                            Button(
                                onClick = {
//                                    viewModel.getUser()
                                }
                            ) {
                                Text("Check User")
                            }

                            Button(
                                onClick = {
                                    viewModel.refresh {

                                    }
                                }
                            ) {
                                Text("Click for force refresh")
                            }

                        }


                    }
                }
            }
        }
    }
}


