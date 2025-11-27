package com.example.findly.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Search : BottomNavItem("search", Icons.Default.Search, "Пошук")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Обрані")
    object Account : BottomNavItem("account", Icons.Default.AccountCircle, "Акаунт")
}