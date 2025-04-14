package com.radon.authguard.ui.data

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation

data class AuthFormConfig(
    // List of dynamic fields (e.g., email, phone, biometric data)
    val fields: List<FormFieldConfig> = listOf(
        FormFieldConfig(
            key = "username",
            label = "UserName",
            component = { value, onValueChange ->
                TextField(
                    value = value as? String ?: "",
                    onValueChange = { onValueChange(it) },
                    label = { Text("UserName") }
                )
            }
        ),
        FormFieldConfig(
            key = "password",
            label = "Password",
            component = { value, onValueChange ->
                TextField(
                    value = value as? String ?: "",
                    onValueChange = { onValueChange(it) },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text("Password") }
                )
            }
        )
    ),
    val submitButton: @Composable (onClick: () -> Unit, enabled: Boolean) -> Unit =
        { onClick, enabled ->
            Button(
                onClick = onClick,
                enabled = enabled
            ) { Text("Sign In") }
        },
    val errorContent: @Composable (error: String) -> Unit = { error ->
        Text(error, color = MaterialTheme.colorScheme.error)
    },
    val headerContent: @Composable (() -> Unit)? = null,
    val footerContent: @Composable (() -> Unit)? = null
)
