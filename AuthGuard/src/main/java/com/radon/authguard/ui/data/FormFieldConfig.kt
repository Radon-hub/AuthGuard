package com.radon.authguard.ui.data

import androidx.compose.runtime.Composable

data class FormFieldConfig(
    val key: String, // Parameter key (e.g., "phone", "username")
    val label: String,
    val component: @Composable (value: Any, onValueChange: (Any) -> Unit) -> Unit
)