package com.example.findly.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {
    // Приватна змінна, яку змінюємо тільки тут
    private val _isLoggedIn = MutableStateFlow(false)

    // Публічна змінна, яку слухають інші (read-only)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun init() {
        // При старті перевіряємо, чи є токен у пам'яті
        _isLoggedIn.value = TokenManager.getToken() != null
    }

    fun login(token: String, userId: String, login: String) {
        TokenManager.saveAuthData(token, userId, login)
        _isLoggedIn.value = true
    }

    fun logout() {
        TokenManager.clear()
        _isLoggedIn.value = false
    }
}