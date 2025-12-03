package com.example.findly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
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
            .padding(vertical = 4.dp), // Зменшили вертикальний відступ між картками
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp) // Зменшили внутрішній відступ (було 12.dp)
                .height(IntrinsicSize.Min)
        ) {
            // --- 1. ФОТО КНИГИ (Зліва) ---
            AsyncImage(
                model = "http://10.0.2.2:5132" + item.bookImageUrl,
                contentDescription = item.bookTitle,
                modifier = Modifier
                    .width(80.dp)
                    .height(100.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            // --- 2. ЦЕНТРАЛЬНА ЧАСТИНА (Книга + Іконки) ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Інфо про книгу
                Column {
                    Text(
                        text = item.bookTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 16.sp // Трохи компактніше
                    )
                    Text(
                        text = item.shopName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                // Іконки (Знизу зліва)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.offset(x = (-10).dp) // Компенсація
                ) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Видалити",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(onClick = onNotifyClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (item.isNotifySet) Icons.Default.Notifications else Icons.Default.NotificationsNone,
                            contentDescription = "Сповіщення",
                            tint = if (item.isNotifySet) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // --- 3. ПРАВА ЧАСТИНА (Магазин, Статус, Ціна, Кнопка) ---
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                // Блок інформації про магазин (Зверху)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${item.currentPrice} грн",
                        style = MaterialTheme.typography.titleLarge, // Повернув TitleLarge для балансу
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    if (item.isAvailable) {
                        Text(
                            text = "В наявності",
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = "Закінчилося",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 12.sp
                        )
                    }
                }

                // Блок ціни та кнопки (Знизу)
                Column(horizontalAlignment = Alignment.End) {
                    Button(
                        onClick = onBuyClick,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier
                            .height(30.dp) // Дуже компактна кнопка
                            .defaultMinSize(minWidth = 1.dp), // Дозволяє кнопці бути вузькою
                        shape = RoundedCornerShape(6.dp),
                    ) {
                        Text("Купити", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}