package com.example.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app.screens.Details
import com.example.app.screens.Settings
import com.example.app.ui.navigation.Screen
import com.example.app.screens.ListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.List.route) {
        composable(Screen.List.route) { ListScreen(navController) }
        composable(Screen.Detail.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
            Details(itemId = id, navController = navController)
        }
        composable(Screen.Settings.route) { Settings() }
    }
}