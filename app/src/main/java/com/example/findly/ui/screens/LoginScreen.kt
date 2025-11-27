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
fun LoginScreen(
    onLoginSuccess: () -> Unit, // Що робити, коли вхід успішний (навігація)
    onNavigateToRegister: () -> Unit // Перехід на реєстрацію
) {
    // Отримуємо ViewModel
    val viewModel: AuthViewModel = viewModel()

    // Локальний стан полів вводу
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Слідкуємо за успішним входом
    if (viewModel.loginSuccess) {
        // LaunchedEffect запускає код один раз
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Автентифікація", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // Поле Логін
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логін") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле Пароль
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(), // Приховати символи
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Показуємо помилку, якщо є
        viewModel.errorMessage?.let { msg ->
            Text(text = msg, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Кнопка Входу
        Button(
            onClick = { viewModel.login(login, password) },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Увійти")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка переходу на реєстрацію
        TextButton(onClick = onNavigateToRegister) {
            Text("Реєстрація")
        }
    }
}