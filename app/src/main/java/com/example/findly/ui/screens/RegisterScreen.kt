package com.example.findly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.findly.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit, // Успішна реєстрація -> вхід в додаток
    onNavigateToLogin: () -> Unit  // Кнопка "Вже маю акаунт"
) {
    // Використовуємо ту саму ViewModel (shared instance з Activity)
    val viewModel: AuthViewModel = viewModel()

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Скидаємо старі стани при вході на екран
    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    // Слідкуємо за успіхом
    if (viewModel.loginSuccess) {
        LaunchedEffect(Unit) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок згідно ТЗ [cite: 66]
        Text(text = "Реєстрація", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логін") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.errorMessage != null) {
            Text(text = viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Кнопка реєстрації
        Button(
            onClick = { viewModel.register(login, password) },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Зареєструватися")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка повернення на вхід
        TextButton(onClick = onNavigateToLogin) {
            Text("Автентифікація")
        }
    }
}
