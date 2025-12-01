package com.example.findly.ui.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.findly.ui.components.BookItem
import com.example.findly.ui.viewmodel.CatalogViewModel

@Composable
fun BookListView(viewModel: CatalogViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {

        // --- БЛОК ФІЛЬТРІВ (ОНОВЛЕНИЙ ДИЗАЙН) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            // 1. Тінь робимо трохи більшою для об'єму
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            // 2. Колір фону міняємо на чистий (surface), а не сірий
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface // Білий/Темний
            ),
            // 3. Додаємо тонку кольорову рамку для акценту
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 1. Пошук за назвою
                OutlinedTextField(
                    value = viewModel.searchTitle,
                    onValueChange = { viewModel.onTitleChange(it) },
                    label = { Text("Назва книги") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    // Робимо поля трохи округлішими
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Пошук за автором
                OutlinedTextField(
                    value = viewModel.searchAuthor,
                    onValueChange = { viewModel.onAuthorChange(it) },
                    label = { Text("Автор") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Ряд з Dropdown
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        FilterDropdown(
                            label = "Видавництво",
                            options = viewModel.publishers,
                            selectedOption = viewModel.selectedPublisher,
                            onOptionSelected = { viewModel.onPublisherSelected(it) },
                            itemLabel = { it.title }
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

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

                Spacer(modifier = Modifier.height(12.dp))

                // 4. ФІЛЬТР НАЯВНОСТІ (Стилізований)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Легка підсвітка при натисканні
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                        .clickable { viewModel.onAvailabilityChange(!viewModel.isAvailable) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = viewModel.isAvailable,
                        onCheckedChange = { viewModel.onAvailabilityChange(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Text(
                        text = "Тільки в наявності",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface // Чорний/Білий текст
                    )
                }
            }
        }

        // --- СПИСОК КНИГ (Infinite Scroll) ---
        // (Цей блок залишається без змін)
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
                    BookItem(book = book, onClick = { viewModel.openBookDetails(book) })

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
    itemLabel: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption?.let { itemLabel(it) } ?: "",
            onValueChange = {},
            readOnly = true, // Користувач не може писати вручну, тільки вибирати
            label = {
                Text(
                    text = label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            },
            placeholder = { Text("Оберіть...") },
            trailingIcon = {
                // Логіка очищення або стрілочка
                if (selectedOption != null) {
                    IconButton(onClick = {
                        onOptionSelected(null)
                        expanded = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Очистити")
                    }
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            // --- СТИЛІЗАЦІЯ ---
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), // Заокруглення як у інших полів
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                // Робимо фон таким самим, як у картки (чистим), або прозорим
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        // Випадаюче меню
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface) // Фон списку
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = itemLabel(option),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}