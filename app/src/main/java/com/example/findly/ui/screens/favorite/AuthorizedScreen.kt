package com.example.findly.ui.screens.favorite

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.findly.ui.components.LikedOfferItem
import com.example.findly.ui.screens.PriceAlertDialog
import com.example.findly.ui.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorizedFavoritesView() {
    val viewModel: FavoritesViewModel = viewModel()
    val context = LocalContext.current

    // Оновлюємо дані при вході
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Збережені пропозиції") })
        }
    ) { padding ->
        // Використовуємо Box, щоб Діалог міг перекрити список
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.favorites.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Список порожній",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("Додайте товари з пошуку ❤️", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                // --- СПИСОК ---
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    // verticalArrangement = Arrangement.spacedBy(8.dp) // Можна додати відступи між картками
                ) {
                    items(viewModel.favorites) { offer ->
                        LikedOfferItem(
                            item = offer,
                            onRemove = { viewModel.removeFromFavorites(offer.offerId) },
                            onBuyClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(offer.link))
                                context.startActivity(intent)
                            },
                            onNotifyClick = { viewModel.onBellClick(offer) } // <-- Підключення дзвіночка
                        )
                    }
                }
            }

            // --- ДІАЛОГОВЕ ВІКНО (З'являється поверх) ---
            if (viewModel.showPriceDialog) {
                PriceAlertDialog(
                    onDismiss = { viewModel.showPriceDialog = false },
                    onConfirm = { price -> viewModel.setPriceAlert(price) }
                )
            }

            // Помилки
            viewModel.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}