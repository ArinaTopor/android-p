package com.example.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.app.data.local.FilterCache
import com.example.app.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val filterSettings by viewModel.filterSettings.collectAsStateWithLifecycle(
        initialValue = com.example.app.data.repository.FilterSettings()
    )
    val hasActiveFilters by viewModel.hasActiveFilters.collectAsStateWithLifecycle()

    var genre by remember(filterSettings.genre) { mutableStateOf(filterSettings.genre) }
    var minRatingString by remember(filterSettings.minRating) { 
        mutableStateOf(if (filterSettings.minRating > 0f) filterSettings.minRating.toString() else "") 
    }
    var query by remember(filterSettings.query) { mutableStateOf(filterSettings.query) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Настройки фильтров")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            genre = ""
                            minRatingString = ""
                            query = ""
                            viewModel.clearFilters()
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Сбросить фильтры")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val minRating = minRatingString.toFloatOrNull() ?: 0f
                    viewModel.applyFilters(genre, minRating, query)
                    navController.popBackStack()
                }
            ) {
                Text("✓")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Жанр фильма") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Например: драма, комедия") }
            )

            OutlinedTextField(
                value = minRatingString,
                onValueChange = { minRatingString = it },
                label = { Text("Минимальная оценка") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0.0 - 10.0") }
            )

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Название фильма") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск по названию") }
            )
        }
    }
}
