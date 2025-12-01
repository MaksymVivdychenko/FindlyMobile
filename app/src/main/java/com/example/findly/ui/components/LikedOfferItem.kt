package com.example.findly.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.findly.model.LikedOffer

@Composable
fun LikedOfferItem(
    item: LikedOffer,
    onRemove: () -> Unit,
    onBuyClick: () -> Unit,
    onNotifyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Як у BookItem
        elevation = CardDefaults.cardElevation(4.dp),       // Як у BookItem
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Як у BookItem
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Картинка книги (стиль ідентичний BookItem)
            AsyncImage(
                model = "http://10.0.2.2:5132" + item.bookImageUrl,
                contentDescription = item.bookTitle,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Назва
                Text(
                    text = item.bookTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Автори
                Text(
                    text = item.authors.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Рядок: Магазин (Замість Видавництва/Обкладинки)
                Text(
                    text = item.shopName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f)) // Притискаємо нижній ряд до низу

                // Рядок: Ціна + Кнопки дій
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Ціна
                    Text(
                        text = "${item.currentPrice} грн",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Блок кнопок (Дзвіночок, Видалити, Купити)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Дзвіночок
                        IconButton(
                            onClick = onNotifyClick,
                            modifier = Modifier.size(24.dp) // Компактний розмір
                        ) {
                            Icon(
                                imageVector = if (item.isNotifySet) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                contentDescription = "Сповіщення",
                                tint = if (item.isNotifySet) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Видалити
                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Видалити",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Купити (маленька кнопка)
                        Button(
                            onClick = onBuyClick,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("Купити", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}