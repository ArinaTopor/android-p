package com.example.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import com.example.app.R
import com.example.app.viewmodels.MainViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import com.example.app.ui.state.MovieUiState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val moviesUiState = viewModel.moviesUiState.collectAsStateWithLifecycle()
    val filteredMovies by viewModel.movies.collectAsStateWithLifecycle()
    val hasActiveFilters by viewModel.hasActiveFilters.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.loadMovies()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Фильмы")
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (hasActiveFilters) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error
                                ) {
                                    Text("1")
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(Icons.Default.Settings, contentDescription = "Настройки")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Поиск фильмов") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            viewModel.searchMovies(searchQuery)
                            keyboardController?.hide()
                        } else {
                            viewModel.loadMovies()
                        }
                    }
                ),
                singleLine = true
            )

            when (val state = moviesUiState.value) {
                is MovieUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is MovieUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Ошибка загрузки",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = {
                                    if (searchQuery.isNotBlank()) {
                                        viewModel.searchMovies(searchQuery)
                                    } else {
                                        viewModel.loadMovies()
                                    }
                                }
                            ) {
                                Text("Повторить")
                            }
                        }
                    }
                }

                is MovieUiState.Success -> {
                    if (filteredMovies.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) "Фильмы не найдены" else "Нет фильмов",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredMovies) { movie ->
                                val shape = RoundedCornerShape(12.dp)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(shape)
                                        .clickable { navController.navigate("detail/${movie.id}") }
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = movie.poster?.previewUrl ?: movie.poster?.url,
                                        contentDescription = movie.name,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentScale = ContentScale.Fit,
                                        placeholder = painterResource(id = R.drawable.ic_movie_placeholder),
                                        error = painterResource(id = R.drawable.ic_movie_placeholder)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            movie.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            listOfNotNull(
                                                movie.alternativeName,
                                                movie.year?.toString(),
                                                movie.genres.firstOrNull()?.name
                                            ).joinToString(" • "),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    val rating = movie.rating?.kp ?: movie.rating?.imdb
                                    if (rating != null) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text(String.format("★ %.1f", rating)) },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = MaterialTheme.colorScheme.secondary,
                                                labelColor = MaterialTheme.colorScheme.onSecondary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
