package com.example.findly.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.findly.ui.components.BookItem
import com.example.findly.ui.viewmodel.CatalogViewModel

@Composable
fun SearchScreen() {
    val viewModel: CatalogViewModel = viewModel()

    Column(modifier = Modifier.fillMaxSize()) {

        // --- БЛОК ФІЛЬТРІВ (HEADER) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // 1. Пошук за назвою
                OutlinedTextField(
                    value = viewModel.searchTitle,
                    onValueChange = { viewModel.onTitleChange(it) },
                    label = { Text("Назва книги") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Пошук за автором (Кирилиця працює автоматично)
                OutlinedTextField(
                    value = viewModel.searchAuthor,
                    onValueChange = { viewModel.onAuthorChange(it) },
                    label = { Text("Автор") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Ряд з Dropdown (Видавництво та Обкладинка)
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Видавництво (ліворуч)
                    Box(modifier = Modifier.weight(1f)) {
                        FilterDropdown(
                            label = "Видавництво",
                            options = viewModel.publishers,
                            selectedOption = viewModel.selectedPublisher,
                            onOptionSelected = { viewModel.onPublisherSelected(it) },
                            itemLabel = { it.title } // Поле, яке показувати в списку
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Обкладинка (праворуч)
                    Box(modifier = Modifier.weight(1f)) {
                        FilterDropdown(
                            label = "Обкладинка",
                            options = viewModel.covers,
                            selectedOption = viewModel.selectedCover,
                            onOptionSelected = { viewModel.onCoverSelected(it) },
                            itemLabel = { it.name }
                        )
                    }
                }
            }
        }

        // --- СПИСОК КНИГ (Infinite Scroll) ---
        if (viewModel.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(viewModel.books) { index, book ->
                    BookItem(book = book, onClick = { /* TODO: Open details */ })

                    // Пагінація
                    if (index >= viewModel.books.lastIndex - 1 && !viewModel.isLoading && !viewModel.isLastPage) {
                        LaunchedEffect(Unit) {
                            viewModel.loadBooks(reset = false)
                        }
                    }
                }

                if (viewModel.isLoading && viewModel.books.isNotEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

// --- УНІВЕРСАЛЬНИЙ КОМПОНЕНТ DROPDOWN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilterDropdown(
    label: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T?) -> Unit,
    itemLabel: (T) -> String // Функція, щоб дістати текст з об'єкта
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption?.let { itemLabel(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            trailingIcon = {
                // Якщо щось вибрано - показуємо хрестик для очищення
                if (selectedOption != null) {
                    IconButton(onClick = {
                        onOptionSelected(null) // Очистити фільтр
                        expanded = false // Закрити меню, щоб не блимало
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(itemLabel(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}