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
import com.example.findly.utils.SessionManager
import com.example.findly.utils.TokenManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

// Стани екрану
enum class AccountState {
    LOGIN,
    REGISTER,
    PROFILE
}

class AccountViewModel : ViewModel() {

    // --- СТАН UI ---
    // При старті: якщо є токен -> показуємо Профіль, інакше -> Логін
    var uiState by mutableStateOf(AccountState.LOGIN)
        private set

    // --- ДАНІ ---
    var userLogin by mutableStateOf<String>(TokenManager.getLogin() ?: "Користувач")
        private set

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null) // Для помилок входу/реєстрації
    var message by mutableStateOf<String?>(null)      // Для повідомлень профілю (зміна паролю)

    // --- НАВІГАЦІЯ ---

    init {
        viewModelScope.launch {
            refreshUserData()
            SessionManager.isLoggedIn.collect { isLogged ->
                if (isLogged) {
                    // Якщо увійшли -> показуємо Профіль і оновлюємо логін
                    uiState = AccountState.PROFILE
                } else {
                    // Якщо вийшли (або протух токен) -> показуємо Логін і чистимо дані
                    // Але тільки якщо ми вже не на екрані реєстрації (щоб не збивати юзера)
                    if (uiState != AccountState.REGISTER) {
                        uiState = AccountState.LOGIN
                    }
                }
            }
        }
    }

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
                val response = RetrofitClient.api.login(LoginRequest(login, pass, FirebaseMessaging.getInstance().token.await()))

                // 1. Зберігаємо дані
                SessionManager.login(response.token, response.userId, response.login)

            } catch (e: Exception) {
                errorMessage = "Помилка входу: ${e.message}"
            } finally {
                isLoading = false
                userLogin = TokenManager.getLogin() ?: ""
            }
        }
    }

    fun register(login: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.api.register(RegisterRequest(login, pass, FirebaseMessaging.getInstance().token.await()))

                SessionManager.login(response.token, response.userId, response.login)
            } catch (e: Exception) {
                errorMessage = "Помилка реєстрації: ${e.message}"
            } finally {
                isLoading = false
                userLogin = TokenManager.getLogin() ?: ""
            }
        }
    }

    // --- ЛОГІКА ПРОФІЛЮ ---

    fun logout() {
        userLogin = ""
        SessionManager.logout()
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