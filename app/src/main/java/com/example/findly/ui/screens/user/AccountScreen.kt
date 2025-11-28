package com.example.findly.ui.screens.user

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.findly.ui.viewmodel.AccountState
import com.example.findly.ui.viewmodel.AccountViewModel

@Composable
fun AccountScreen() {
    // Використовуємо ViewModel, щоб керувати станом
    val viewModel: AccountViewModel = viewModel()

    // Вся логіка тепер тут - проста і лінійна
    when (viewModel.uiState) {
        AccountState.LOGIN -> {
            LoginScreen(viewModel)
        }
        AccountState.REGISTER -> {
            RegisterScreen(viewModel)
        }
        AccountState.PROFILE -> {
            UserProfileView(viewModel)
        }
    }
}