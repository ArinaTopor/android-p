package com.example.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val baseRoute: String = route
) {
    object List : Screen(
        route = "list",
        title = "Фильмы",
        icon = Icons.Filled.Movie,
        baseRoute = "list"
    )
    object Detail : Screen(
        route = "detail/{itemId}",
        title = "Детали",
        baseRoute = "detail"
    )
    object Settings : Screen(
        route = "settings",
        title = "Настройки",
        icon = Icons.Filled.Settings,
        baseRoute = "settings"
    )
    object Favorites : Screen(
        route = "favorites",
        title = "Избранное",
        icon = Icons.Filled.Favorite,
        baseRoute = "favorites"
    )
    object Profile : Screen(
        route = "profile",
        title = "Профиль",
        icon = Icons.Filled.Person,
        baseRoute = "profile"
    )
    object EditProfile : Screen(
        route = "profile/edit",
        title = "Редактировать профиль",
        baseRoute = "profile/edit"
    )
}
