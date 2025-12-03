package com.example.findly.ui.viewmodel

import android.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.findly.api.RetrofitClient
import com.example.findly.model.Book
import com.example.findly.model.Cover
import com.example.findly.model.Offer
import com.example.findly.model.Publisher
import com.example.findly.utils.SessionManager

enum class SearchState {
    LIST,    // Список книг
    DETAILS  // Деталі книги (Пропозиції)
}

class CatalogViewModel : ViewModel() {

    // --- НАВІГАЦІЯ ---
    var searchState by mutableStateOf(SearchState.LIST)
        private set

    var selectedBook by mutableStateOf<Book?>(null) // Книга, яку зараз переглядають
        private set

    // --- ДАНІ КАТАЛОГУ (Ті, що були раніше) ---
    var books by mutableStateOf<List<Book>>(emptyList())
    var publishers by mutableStateOf<List<Publisher>>(emptyList())
    var covers by mutableStateOf<List<Cover>>(emptyList())

    // --- ДАНІ ДЕТАЛЕЙ (Нові) ---
    var offers by mutableStateOf<List<Offer>>(emptyList()) // Список цін магазинів
    var areOffersLoading by mutableStateOf(false)

    // --- ФІЛЬТРИ ТА ПОШУК ---
    var searchTitle by mutableStateOf("")
    var searchAuthor by mutableStateOf("")
    var selectedPublisher by mutableStateOf<Publisher?>(null)
    var selectedCover by mutableStateOf<Cover?>(null)

    var isAvailable by mutableStateOf<Boolean>(false)
    var isPriceAscending by mutableStateOf<Boolean>(false)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Пагінація
    private var currentPage = 1
    private val pageSize = 10
    var isLastPage by mutableStateOf(false)


    init {
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
                    pageSize = pageSize,
                    isAvailable = isAvailable
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

    fun openBookDetails(book: Book) {
        selectedBook = book
        searchState = SearchState.DETAILS
        // Одразу завантажуємо пропозиції для цієї книги
        loadOffers(book.id)
    }

    fun closeBookDetails() {
        searchState = SearchState.LIST
        selectedBook = null
        offers = emptyList() // Очищаємо старі дані
    }

    fun toggleSortOrder()
    {
        isPriceAscending = !isPriceAscending
        applySort()
    }

    private fun applySort() {
        offers = if (isPriceAscending) {
            offers.sortedBy { it.price }
        } else {
            offers.sortedByDescending { it.price }
        }
    }

    private fun loadOffers(bookId: String) {
        viewModelScope.launch {
            areOffersLoading = true
            try {
                // Запит до API за списком пропозицій
                offers = RetrofitClient.api.getOffersByBookId(bookId)
                applySort()
            } catch (e: Exception) {
                // Тут можна обробити помилку окремо, або просто показати пустий список
                println("Помилка завантаження пропозицій: ${e.message}")
            } finally {
                areOffersLoading = false
            }
        }
    }

    // Методи для оновлення фільтрів з UI

    fun onTitleChange(newTitle: String) {
        searchTitle = newTitle
        loadBooks(reset = true)
    }

    fun onAvailabilityChange(newStatus: Boolean) {
        isAvailable = newStatus
        loadBooks(reset = true) // Одразу перезавантажуємо список
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

    var showPriceDialog by mutableStateOf(false)
    var showAuthDialog by mutableStateOf(false)
    var selectedOfferForAlert by mutableStateOf<Offer?>(null)

    // --- ЛОГІКА ОБРАНИХ (СЕРЦЕ) ---
    fun toggleFavorite(offer: Offer) {
        if (!SessionManager.isLoggedIn.value) {
            showAuthDialog = true // Показуємо попередження
            return
        }
        viewModelScope.launch {
            try {
                // Оптимістичне оновлення UI (міняємо іконку відразу)
                updateOfferInList(offer.copy(isLiked = !offer.isLiked))

                if (offer.isLiked) {
                    RetrofitClient.api.removeFromFavorites(offer.id)
                } else {
                    RetrofitClient.api.addToFavorites(offer.id)
                }
            } catch (e: Exception) {
                // Якщо помилка - повертаємо як було
                updateOfferInList(offer)
                errorMessage = "Помилка: ${e.localizedMessage}"
            }
        }
    }

    // --- ЛОГІКА СПОВІЩЕНЬ (ДЗВІНОЧОК) ---

    fun onBellClick(offer: Offer) {
        if (!SessionManager.isLoggedIn.value) {
            showAuthDialog = true
            return
        }

        if (offer.isPriceSet) {
            // Якщо ціна вже стоїть - видаляємо сповіщення
            removePriceAlert(offer)
        } else {
            if(!offer.isLiked) {
                toggleFavorite(offer)
            }
            selectedOfferForAlert = offer
            showPriceDialog = true
        }
    }

    fun dismissAuthDialog() {
        showAuthDialog = false
    }
    fun setPriceAlert(price: Double) {
        val offer = selectedOfferForAlert ?: return
        viewModelScope.launch {
            try {
                // Закриваємо діалог
                showPriceDialog = false

                // Візуально оновлюємо (серце теж стає активним, бо це додає в обрані)
                updateOfferInList(offer.copy(isPriceSet = true, isLiked = true))

                // Запит на сервер
                RetrofitClient.api.addPriceAlert(
                    com.example.findly.model.AddPriceRequest(offer.id, price)
                )
            } catch (e: Exception) {
                errorMessage = "Не вдалося встановити ціну"
            }
        }
    }

    private fun removePriceAlert(offer: Offer) {
        viewModelScope.launch {
            try {
                updateOfferInList(offer.copy(isPriceSet = false))
                RetrofitClient.api.removePriceAlert(offer.id)
            } catch (e: Exception) {
                errorMessage = "Помилка видалення: ${e.localizedMessage}"
            }
        }
    }

    // Допоміжна функція для оновлення одного елемента в списку без перезавантаження
    private fun updateOfferInList(newOffer: Offer) {
        offers = offers.map { if (it.id == newOffer.id) newOffer else it }
    }
}