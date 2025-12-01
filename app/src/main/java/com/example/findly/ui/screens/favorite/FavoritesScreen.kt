package com.example.findly.ui.screens.favorite

import androidx.compose.runtime.*

import com.example.findly.utils.SessionManager

// --- ГОЛОВНИЙ ЕКРАН-РОУТЕР ---
@Composable
fun FavoritesScreen( // Колбек для переходу на вкладку Акаунт
) {
    // Слухаємо глобальну сесію як State
    val isLoggedIn by SessionManager.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        // Якщо є сесія - показуємо реальний список
        AuthorizedFavoritesView()
    } else {
        // Якщо немає - показуємо заглушку
        GuestFavoritesView()
    }
}
