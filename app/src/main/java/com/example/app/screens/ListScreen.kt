package com.example.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import com.example.app.R
import com.example.app.viewmodels.MainViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController, viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val moviesState = viewModel.movies.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Список") }) }
    ) { padding ->
        val movies = moviesState.value
        LazyColumn(contentPadding = padding, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(movies) { movie ->
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
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Fit,
                        placeholder = painterResource(id = R.drawable.ic_movie_placeholder),
                        error = painterResource(id = R.drawable.ic_movie_placeholder)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(movie.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
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
                        AssistChip(onClick = {}, label = { Text(String.format("★ %.1f", rating)) }, colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondary, labelColor = MaterialTheme.colorScheme.onSecondary))
                    }
                }
            }
        }
    }
}
