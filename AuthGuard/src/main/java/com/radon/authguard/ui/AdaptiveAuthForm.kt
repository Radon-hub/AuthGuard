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
import com.radon.authguard.ui.viewmodel.AuthViewModel
import com.radon.authguard.ui.data.AuthFormConfig


@Composable
fun AdaptiveAuthForm(
    modifier: Modifier = Modifier,
    config: AuthFormConfig = AuthFormConfig(),
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    viewModel: AuthViewModel = AuthGuard.container.authViewModelFactory.create()
) {

    Column(modifier = modifier) {
        config.headerContent?.invoke()

        config.fields.forEach { field ->
            key(field.key) {
                field.component(
                    viewModel.formFields[field.key] ?: "",
                    { newValue -> viewModel.updateField(field.key, newValue.toString()) }
                )
            }
        }

        config.submitButton(
            { viewModel.login(onSuccess) },
            !viewModel.isLoading
        )

        viewModel.error?.let {
            config.errorContent(it)
            onError(it)
        }

        config.footerContent?.invoke()
    }
}
