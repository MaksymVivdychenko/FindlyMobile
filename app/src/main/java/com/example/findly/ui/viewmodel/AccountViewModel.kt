package com.example.findly.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.findly.api.RetrofitClient
import com.example.findly.model.LoginRequest
import com.example.findly.model.RegisterRequest
import com.example.findly.model.ChangePasswordRequest
import com.example.findly.utils.TokenManager

// Стани екрану
enum class AccountState {
    LOGIN,
    REGISTER,
    PROFILE
}

class AccountViewModel : ViewModel() {

    // --- СТАН UI ---
    // При старті: якщо є токен -> показуємо Профіль, інакше -> Логін
    var uiState by mutableStateOf(
        if (TokenManager.getToken() != null) AccountState.PROFILE else AccountState.LOGIN
    )
        private set

    // --- ДАНІ ---
    var userLogin by mutableStateOf(TokenManager.getLogin() ?: "")
        private set

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null) // Для помилок входу/реєстрації
    var message by mutableStateOf<String?>(null)      // Для повідомлень профілю (зміна паролю)

    // --- НАВІГАЦІЯ ---

    fun navigateToRegister() {
        errorMessage = null
        uiState = AccountState.REGISTER
    }

    fun navigateToLogin() {
        errorMessage = null
        uiState = AccountState.LOGIN
    }

    // --- ЛОГІКА АВТОРИЗАЦІЇ (Перенесено з AuthViewModel) ---

    fun login(login: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.api.login(LoginRequest(login, pass))

                // 1. Зберігаємо дані
                TokenManager.saveAuthData(response.token, response.userId, response.login)
                userLogin = response.login

                // 2. ЯВНО перемикаємо стан на Профіль
                uiState = AccountState.PROFILE
            } catch (e: Exception) {
                errorMessage = "Помилка входу: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun register(login: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.api.register(RegisterRequest(login, pass))

                TokenManager.saveAuthData(response.token, response.userId, response.login)
                userLogin = response.login

                // Після успішної реєстрації теж йдемо в профіль
                uiState = AccountState.PROFILE
            } catch (e: Exception) {
                errorMessage = "Помилка реєстрації: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // --- ЛОГІКА ПРОФІЛЮ ---

    fun logout() {
        TokenManager.clear()
        userLogin = ""
        // Миттєво перемикаємо на екран логіну
        uiState = AccountState.LOGIN
    }

    fun refreshUserData() {
        userLogin = TokenManager.getLogin() ?: ""
    }

    fun changePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            isLoading = true
            message = null
            try {
                val request = ChangePasswordRequest(newPassword = newPass, oldPassword = oldPass)
                val response = RetrofitClient.api.changePassword(request)
                message = response.message
            } catch (e: Exception) {
                message = "Помилка: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}