package com.example.findly.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.findly.ui.navigation.BottomNavItem

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                // Отримуємо поточний маршрут, щоб підсвітити активну кнопку
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Список вкладок
                val items = listOf(
                    BottomNavItem.Search,
                    BottomNavItem.Favorites,
                    BottomNavItem.Account
                )

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                // Це важливо: при натисканні "Назад" повертаємось на першу вкладку, а не виходимо з додатка
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Щоб не відкривати ту саму вкладку багаторазово
                                launchSingleTop = true
                                // Зберігаємо стан (наприклад, введений текст в логіні не зникне при перемиканні вкладок)
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Тут відбувається магія перемикання екранів
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Search.route, // Стартовий екран - Пошук
            modifier = Modifier.padding(innerPadding)
        ) {
            // Вкладка 1: Пошук
            composable(BottomNavItem.Search.route) {
                SearchScreen()
            }

            // Вкладка 2: Обрані
            composable(BottomNavItem.Favorites.route) {
                FavoritesScreen()
            }

            // Вкладка 3: Акаунт (Тут живе вся ваша логіка авторизації)
            composable(BottomNavItem.Account.route) {
                AccountScreen()
            }
        }
    }
}

fun onLogout()
{

}