package com.radon.authguard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.viewModelFactory
import com.radon.authguard.domain.AuthGuard
import com.radon.authguard.core.di.AuthViewModelFactory
import com.radon.authguard.ui.viewmodel.AuthViewModel
import com.radon.authguard.ui.data.AuthFormConfig



@Composable
fun AdaptiveAuthForm(
    modifier: Modifier = Modifier,
    config: AuthFormConfig = AuthFormConfig(),
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val viewModel: AuthViewModel = viewModelFactory { AuthViewModelFactory() }.create(AuthViewModel::class.java)
//    val viewModel: AuthViewModel = viewModelFactory { AuthViewModelFactory(
//        AuthGuard.authService,
//        AuthGuard.tokenManager
//    ) }.create(AuthViewModel::class.java)

    Column(modifier = modifier) {
        config.headerContent?.invoke()

        config.fields.forEach { field ->
            key(field.key) {
                field.component(
                    viewModel.formFields[field.key] ?: "",
                    { newValue -> viewModel.updateField(field.key, newValue) }
                )
            }
        }

        config.submitButton(
            { viewModel.login(onSuccess) },
            !viewModel.isLoading
        )

        viewModel.error?.let {
            config.errorContent(it)
        }

        config.footerContent?.invoke()
    }
}
//
//@Composable
//fun AdaptiveAuthForm(
//    authType: AuthType,
//    onSuccess: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//    val viewModel: AuthViewModel = viewModelFactory { AuthViewModelFactory(
//        AuthGuard.authApi,
//        AuthGuard.tokenManager
//    ) }.create(AuthViewModel::class.java)
//
//    Column(modifier = modifier.fillMaxWidth()) {
//        TextField(
//            value = viewModel.emailState,
//            onValueChange = { viewModel.updateEmail(it) },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        TextField(
//            value = viewModel.passwordState,
//            onValueChange = { viewModel.updatePassword(it)},
//            label = { Text("Password") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Button(
//            onClick = { viewModel.login(onSuccess) },
//            enabled = !viewModel.isLoadingState,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(if (viewModel.isLoadingState) "Loading..." else "Submit")
//        }
//
//        viewModel.errorState?.let {
//            Text(it, color = MaterialTheme.colorScheme.error)
//        }
//    }
//
//}
//
//@Composable
//fun AdaptiveAuthForm(
//    authType: AuthType,
//    onSuccess: () -> Unit,
//    modifier: Modifier = Modifier,
//    config: AuthFormConfig = AuthFormConfig(),
//    viewModel: AuthViewModel =  viewModelFactory { AuthViewModelFactory(
//            AuthGuard.authApi,
//            AuthGuard.tokenManager
//        ) }.create(AuthViewModel::class.java)
//) {
//    Column(modifier = modifier.fillMaxWidth()) {
//        // Header slot
//        config.headerContent?.invoke()
//
//        // Email field (customizable)
//        config.emailTextField(
//            viewModel.emailState,
//            { viewModel.updateEmail(it)},
//            Modifier.fillMaxWidth()
//        )
//
//        // Password field (customizable)
//        config.passwordTextField(
//            viewModel.passwordState,
//            { viewModel.updatePassword(it)},
//            Modifier.fillMaxWidth()
//        )
//
//        // Submit button (customizable)
//        config.submitButton(
//            { viewModel.login(onSuccess) },
//            viewModel.isLoadingState,
//            Modifier.fillMaxWidth()
//        )
//
//        // Error message (customizable)
//        viewModel.emailState.let { config.errorContent(it) }
//
//        // Footer slot
//        config.footerContent?.invoke()
//    }
//}
//
