package com.example.findly.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.findly.api.RetrofitClient
import com.example.findly.model.ChangePasswordRequest
import com.example.findly.utils.TokenManager

class AccountViewModel : ViewModel() {

    var userLogin by mutableStateOf("Завантаження...")
        private set

    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null) // Повідомлення про успіх чи помилку

    fun refreshUserData() {
        userLogin = TokenManager.getLogin() ?: "Невідомий користувач"
    }
    fun changePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            isLoading = true
            message = null
            try {
                val request = ChangePasswordRequest(newPassword = newPass, oldPassword = oldPass)
                val response = RetrofitClient.api.changePassword(request)
                message = "Пароль успішно змінено!"
            } catch (e: Exception) {
                message = "Помилка: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        TokenManager.clear()
        userLogin = ""
        message = null
        // Додаткова логіка очищення БД, якщо треба
    }
}