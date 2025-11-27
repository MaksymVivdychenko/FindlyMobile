package com.example.findly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.findly.ui.theme.FindlyTheme
import com.example.findly.utils.TokenManager
import com.example.findly.ui.viewmodel.AuthViewModel
import com.example.findly.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(applicationContext)

        setContent {
            MaterialTheme {
                val authViewModel: AuthViewModel = viewModel()

                // Стан 1: Чи ми вже всередині системи?
                var isLoggedIn by remember { mutableStateOf(TokenManager.getToken() != null) }

                // Стан 2: Якщо ми НЕ всередині, то який екран показувати (Вхід чи Реєстрація)?
                var isRegistering by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    // --- ЕКРАН КАБІНЕТУ (Або головний екран) ---
                    AccountScreen(
                        onLogout = {
                            authViewModel.resetState()
                            isLoggedIn = false
                            isRegistering = false // При виході показуємо логін
                        }
                    )
                } else {
                    // --- ЗОНА АВТЕНТИФІКАЦІЇ ---
                    if (isRegistering) {
                        // Показуємо РЕЄСТРАЦІЮ
                        RegisterScreen(
                            onRegisterSuccess = {
                                // Успішна реєстрація = автоматичний вхід
                                isLoggedIn = true
                            },
                            onNavigateToLogin = {
                                isRegistering = false // Натиснули "Автентифікація" -> йдемо назад
                            }
                        )
                    } else {
                        // Показуємо ВХІД
                        LoginScreen(
                            onLoginSuccess = {
                                isLoggedIn = true
                            },
                            onNavigateToRegister = {
                                isRegistering = true // Натиснули "Реєстрація" -> йдемо на форму реєстрації
                            }
                        )
                    }
                }
            }
        }
    }
}