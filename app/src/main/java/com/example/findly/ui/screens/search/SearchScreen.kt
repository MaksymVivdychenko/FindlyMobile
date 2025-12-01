package com.example.findly.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.findly.ui.screens.search.BookListView
import com.example.findly.ui.viewmodel.CatalogViewModel
import com.example.findly.ui.viewmodel.SearchState

@Composable
fun SearchScreen() {
    // ViewModel живе на рівні цього екрану. Вона тримає стан List/Details
    val viewModel: CatalogViewModel = viewModel()

    when (viewModel.searchState) {
        SearchState.LIST -> {
            // Показуємо список книг (ми винесли це в окрему функцію для чистоти)
            BookListView(viewModel)
        }
        SearchState.DETAILS -> {
            // Показуємо деталі
            BookDetailsScreen(viewModel)
        }
    }
    if (viewModel.showAuthDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAuthDialog() },
            title = { Text("Потрібна авторизація") },
            text = { Text("Ця дія доступна тільки для зареєстрованих користувачів.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.dismissAuthDialog()
                }) {
                    Text("Зрозуміло")
                }
            },
        )
    }
}