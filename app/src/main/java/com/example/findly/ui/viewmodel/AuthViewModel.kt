package com.example.findly.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findly.api.RetrofitClient
import com.example.findly.model.LoginRequest
import com.example.findly.model.RegisterRequest
import com.example.findly.utils.TokenManager
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue

class AuthViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false) // Сигнал для переходу на наступний екран

    fun login(login: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Виклик API
                val response = RetrofitClient.api.login(LoginRequest(login, pass))

                // Збереження токена
                TokenManager.saveAuthData(response.token, response.userId, response.login)

                loginSuccess = true
            } catch (e: Exception) {
                errorMessage = "Помилка входу: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun register(login: String, pass: String) {
        // 1. Скидаємо стан перед початком
        loginSuccess = false

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 2. Виконуємо запит реєстрації
                val response = RetrofitClient.api.register(RegisterRequest(login, pass))

                // 3. Сервер одразу повертає токен, тому зберігаємо його (автоматичний вхід)
                TokenManager.saveAuthData(response.token, response.userId, response.login)

                // 4. Сигнал успіху -> MainActivity перемкне екран
                loginSuccess = true
            } catch (e: Exception) {
                errorMessage = "Помилка реєстрації: ${e.message}"
                loginSuccess = false
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState(){
        loginSuccess = false
        errorMessage = null
        isLoading = false
    }
}