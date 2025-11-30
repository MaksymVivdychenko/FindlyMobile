package com.example.findly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

@Composable
fun OfferItem(
    offer: Offer,
    onBuyClick: () -> Unit,
    onFavoriteClick: () -> Unit, // Новий колбек
    onBellClick: () -> Unit      // Новий колбек
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Логотип магазину (або заглушка)
            if (offer.shopLogoUrl != null) {
                AsyncImage(
                    model = offer.shopLogoUrl,
                    contentDescription = offer.shopName,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp))
                )
            } else {
                // Якщо лого немає - просто іконка або перша літера
                Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    Text(offer.shopName.take(1), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Назва магазину
            Column(modifier = Modifier.weight(1f)) {
                Text(text = offer.shopName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                if (!offer.isAvailable) {
                    Text(text = "Закінчився", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
                else  {
                    Text(text = "В наявності", color = Color.Green, style = MaterialTheme.typography.bodySmall)
                }
                Row(horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.offset(x = (-12).dp)){
                    // 1. СЕРЦЕ
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (offer.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "В обране",
                            tint = if (offer.isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 2. ДЗВІНОЧОК
                    IconButton(onClick = onBellClick) {
                        Icon(
                            imageVector = if (offer.isPriceSet) Icons.Default.Notifications else Icons.Default.NotificationsNone,
                            contentDescription = "Сповіщення",
                            tint = if (offer.isPriceSet) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant // Жовтий або Сірий
                        )
                    }
                }
            }

            // Ціна і кнопка
            Column(modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "${offer.price} грн",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(26.dp))
                Button(
                    onClick = onBuyClick,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Купити")
                }
            }
        }
    }
}