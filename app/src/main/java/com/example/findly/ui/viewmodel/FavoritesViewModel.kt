package com.example.findly.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.findly.api.RetrofitClient
import com.example.findly.model.AddPriceRequest
import com.example.findly.model.LikedOffer

class FavoritesViewModel : ViewModel() {

    var favorites by mutableStateOf<List<LikedOffer>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // --- СТАН ДЛЯ ДІАЛОГУ ЦІНИ ---
    var showPriceDialog by mutableStateOf(false)
    var selectedOfferForAlert by mutableStateOf<LikedOffer?>(null)

    // Завантаження списку
    fun loadFavorites() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                favorites = RetrofitClient.api.getFavorites()
            } catch (e: Exception) {
                errorMessage = "Не вдалося завантажити: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    // Видалення зі списку (Серце/Смітник)
    fun removeFromFavorites(offerId: String) {
        viewModelScope.launch {
            try {
                // Оптимістичне видалення
                favorites = favorites.filter { it.offerId != offerId }
                RetrofitClient.api.removeFromFavorites(offerId)
            } catch (e: Exception) {
                loadFavorites() // Відкат при помилці
                errorMessage = "Помилка видалення"
            }
        }
    }

    // --- ЛОГІКА СПОВІЩЕНЬ (ДЗВІНОЧОК) ---

    fun onBellClick(offer: LikedOffer) {
        if (offer.isNotifySet) {
            // Якщо вже стоїть - видаляємо
            removePriceAlert(offer)
        } else {
            // Якщо ні - відкриваємо діалог
            selectedOfferForAlert = offer
            showPriceDialog = true
        }
    }

    fun setPriceAlert(price: Double) {
        val offer = selectedOfferForAlert ?: return
        viewModelScope.launch {
            try {
                showPriceDialog = false // Закриваємо вікно

                // Оптимістичне оновлення (іконка стає жовтою)
                updateItemInList(offer.copy(isNotifySet = true))

                RetrofitClient.api.addPriceAlert(
                    AddPriceRequest(offer.offerId, price)
                )
            } catch (e: Exception) {
                errorMessage = "Помилка встановлення ціни: ${e.localizedMessage}"
                updateItemInList(offer) // Відкат
            }
        }
    }

    private fun removePriceAlert(offer: LikedOffer) {
        viewModelScope.launch {
            try {
                // Оптимістичне оновлення (іконка стає сірою)
                updateItemInList(offer.copy(isNotifySet = false))

                RetrofitClient.api.removePriceAlert(offer.offerId)
            } catch (e: Exception) {
                errorMessage = "Помилка скасування: ${e.localizedMessage}"
                updateItemInList(offer) // Відкат
            }
        }
    }

    // Допоміжна функція для локального оновлення списку
    private fun updateItemInList(newItem: LikedOffer) {
        favorites = favorites.map {
            if (it.offerId == newItem.offerId) newItem else it
        }
    }
}