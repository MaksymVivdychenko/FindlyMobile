package com.example.findly.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.findly.model.Offer
import com.example.findly.ui.components.AvailabilityBadge
import com.example.findly.ui.components.OfferItem
import com.example.findly.ui.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(viewModel: CatalogViewModel) {
    val book = viewModel.selectedBook ?: return // Якщо книги немає - виходимо
    val context = LocalContext.current

    // ВАЖЛИВО: Перехоплюємо системну кнопку "Назад", щоб вона не закривала додаток,
    // а повертала до списку книг.
    BackHandler {
        viewModel.closeBookDetails()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пропозиції", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.closeBookDetails() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize())
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            // 1. ВЕЛИКА КАРТКА КНИГИ (HEADER)
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    AsyncImage(
                        model = "http://10.0.2.2:5132" + book.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(text = book.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Автор: ${book.authors.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                        book.publisher?.let {
                            Text(text = "Видавництво: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                        book.cover?.let {
                            Text(text = "Обкладинка: $it", style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        AvailabilityBadge(isAvailable = book.isAvailable)
                    }
                }
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Рядок заголовка і сортування
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp), // Відступи зверху/знизу
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Текст зліва
                    Text(
                        text = "Пропозиції магазинів",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Кнопка справа
                    TextButton(
                        onClick = { viewModel.toggleSortOrder() },
                        contentPadding = PaddingValues(horizontal = 8.dp) // Компактна кнопка
                    ) {
                        // Текст змінюється залежно від стану
                        Text(
                            text = if (viewModel.isPriceAscending) "Дешевші зверху" else "Дорожчі зверху",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // Іконка стрілочки
                        Icon(
                            imageVector = if (viewModel.isPriceAscending) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = "Сортування",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            // 2. СПИСОК ПРОПОЗИЦІЙ (OFFERS)
            if (viewModel.areOffersLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (viewModel.offers.isEmpty()) {
                item {
                    Text("На жаль, пропозицій поки немає.", modifier = Modifier.padding(top = 16.dp))
                }
            } else {
                items(viewModel.offers) { offer ->
                    OfferItem(offer = offer,
                        onBuyClick = { val intent = Intent(Intent.ACTION_VIEW, Uri.parse(offer.link))
                            context.startActivity(intent)},
                        onFavoriteClick = { viewModel.toggleFavorite(offer) }, // <-- ПІДКЛЮЧИЛИ
                        onBellClick = { viewModel.onBellClick(offer) }
                    )
                }
            }
        }
        if (viewModel.showPriceDialog) {
            PriceAlertDialog(
                onDismiss = { viewModel.showPriceDialog = false },
                onConfirm = { price -> viewModel.setPriceAlert(price) }
            )
        }
    }
}

@Composable
fun PriceAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var priceInput by remember { mutableStateOf("") }
    val isError = priceInput.toDoubleOrNull() == null && priceInput.isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Сповіщення про ціну") },
        text = {
            Column {
                Text("Введіть бажану ціну, щоб отримати сповіщення:")
                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it },
                    label = { Text("Ціна (грн)") },
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    priceInput.toDoubleOrNull()?.let { onConfirm(it) }
                },
                enabled = !isError && priceInput.isNotEmpty()
            ) {
                Text("Ок")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}