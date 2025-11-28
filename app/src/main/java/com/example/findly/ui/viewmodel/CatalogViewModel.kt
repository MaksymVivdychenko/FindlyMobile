package com.example.findly.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.findly.api.RetrofitClient
import com.example.findly.model.Book
import com.example.findly.model.Cover
import com.example.findly.model.Publisher

class CatalogViewModel : ViewModel() {

    // --- СПИСОК КНИГ ---
    var books by mutableStateOf<List<Book>>(emptyList())
        private set

    // --- ДОВІДНИКИ (для Dropdown) ---
    var publishers by mutableStateOf<List<Publisher>>(emptyList())
    var covers by mutableStateOf<List<Cover>>(emptyList())

    // --- ОБРАНІ ФІЛЬТРИ ---
    var searchTitle by mutableStateOf("")
    var searchAuthor by mutableStateOf("") // Нове поле
    var selectedPublisher by mutableStateOf<Publisher?>(null) // Обране видавництво
    var selectedCover by mutableStateOf<Cover?>(null)         // Обрана обкладинка

    // --- СТАНИ UI ---
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Пагінація
    private var currentPage = 1
    private val pageSize = 10
    var isLastPage by mutableStateOf(false)

    init {
        // Завантажуємо все одразу при старті
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Паралельно вантажимо довідники
                publishers = RetrofitClient.api.getPublishers()
                covers = RetrofitClient.api.getCovers()

                // І першу сторінку книг
                loadBooks(reset = true)
            } catch (e: Exception) {
                errorMessage = "Помилка завантаження даних: ${e.localizedMessage}"
            }
        }
    }

    fun loadBooks(reset: Boolean = false) {
        if (isLoading) return
        if (!reset && isLastPage) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            if (reset) {
                currentPage = 1
                isLastPage = false
                books = emptyList()
            }

            try {
                // Виконуємо запит з урахуванням УСІХ нових фільтрів
                // Retrofit автоматично кодує кирилицю в UTF-8
                val newBooks = RetrofitClient.api.getBooks(
                    title = if (searchTitle.isBlank()) null else searchTitle,
                    author = if (searchAuthor.isBlank()) null else searchAuthor,
                    publisherId = selectedPublisher?.id, // Беремо ID з об'єкта
                    coverId = selectedCover?.id,         // Беремо ID з об'єкта
                    pageNumber = currentPage,
                    pageSize = pageSize
                )

                if (newBooks.isEmpty()) {
                    isLastPage = true
                } else {
                    books = books + newBooks
                    currentPage++
                }
            } catch (e: Exception) {
                if (reset) errorMessage = "Помилка пошуку: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    // Методи для оновлення фільтрів з UI

    fun onTitleChange(newTitle: String) {
        searchTitle = newTitle
        loadBooks(reset = true)
    }

    fun onAuthorChange(newAuthor: String) {
        searchAuthor = newAuthor
        loadBooks(reset = true)
    }

    fun onPublisherSelected(publisher: Publisher?) {
        selectedPublisher = publisher
        loadBooks(reset = true)
    }

    fun onCoverSelected(cover: Cover?) {
        selectedCover = cover
        loadBooks(reset = true)
    }
}