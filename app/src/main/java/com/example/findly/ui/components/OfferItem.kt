package com.example.findly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.findly.model.Offer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun OfferItem(
    offer: Offer,
    onBuyClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onBellClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp), // Відступи як у інших карток
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Білий/Темний фон
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(IntrinsicSize.Min) // Важливо: вирівнюємо висоту всіх колонок
        ) {
            // --- 1. ЛОГОТИП МАГАЗИНУ (Зліва) ---
            if (offer.shopLogoUrl != null) {
                AsyncImage(
                    model = "http://10.0.2.2:5132" + offer.shopLogoUrl,
                    contentDescription = offer.shopName,
                    modifier = Modifier
                        .size(100.dp) // Трохи збільшили для кращого вигляду
                        .clip(RoundedCornerShape(8.dp))
                        .align(Alignment.Top), // Логотип притиснутий до верху
                    contentScale = ContentScale.Fit
                )
            } else {
                // Заглушка, якщо лого немає
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = offer.shopName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- 2. ЦЕНТРАЛЬНА ЧАСТИНА (Назва, Наявність, Іконки) ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(), // Розтягуємо на всю висоту рядка
                verticalArrangement = Arrangement.SpaceBetween // Розштовхуємо текст (верх) і іконки (низ)
            ) {
                // Верхня частина: Назва + Наявність
                Column {
                    Text(
                        text = offer.shopName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Індикатор наявності
                    if (offer.isAvailable) {
                        Text(
                            text = "В наявності",
                            color = Color(0xFF4CAF50), // Зелений
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "Закінчився",
                            color = MaterialTheme.colorScheme.error, // Червоний
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Нижня частина: Іконки дій (Серце + Дзвіночок)
                // Розташовані зліва, під текстом
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.offset(x = (-12).dp) // Компенсуємо відступ IconButton, щоб вирівняти з текстом
                ) {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (offer.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "В обране",
                            tint = if (offer.isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onBellClick) {
                        Icon(
                            imageVector = if (offer.isPriceSet) Icons.Default.Notifications else Icons.Default.NotificationsNone,
                            contentDescription = "Сповіщення",
                            tint = if (offer.isPriceSet) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // --- 3. ПРАВА ЧАСТИНА (Ціна, Кнопка) ---
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween // Ціна зверху, кнопка знизу
            ) {
                Text(
                    text = "${offer.price} грн",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onBuyClick,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = offer.isAvailable // Кнопка неактивна, якщо товару немає
                ) {
                    Text("Купити", fontSize = 13.sp)
                }
            }
        }
    }
}