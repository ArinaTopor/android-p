package com.example.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app.viewmodels.MainViewModel
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import com.example.app.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Details(itemId: Int, navController: NavController, viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val movie = viewModel.getMovieById(itemId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(movie?.name ?: "Нет данных") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        ConstraintLayout(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            val (posterRef, titleRef, metaRef, chipsRef, ratingRef, descRef, castRef, similarRef) = createRefs()

            Box(
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .constrainAs(posterRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                AsyncImage(
                    model = movie?.poster?.url ?: movie?.poster?.previewUrl,
                    contentDescription = movie?.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.9f)),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
            }

            Text(
                text = movie?.name ?: "",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(posterRef.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            Text(
                text = listOfNotNull(movie?.alternativeName, movie?.year?.toString()).joinToString(" • "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.constrainAs(metaRef) {
                    top.linkTo(titleRef.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.constrainAs(chipsRef) {
                    top.linkTo(metaRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                }
            ) {
                if (movie?.ageRating != null) AssistChip(onClick = {}, label = { Text("${movie.ageRating}+") }, colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, labelColor = MaterialTheme.colorScheme.onSurfaceVariant))
                if (!movie?.ratingMpaa.isNullOrBlank()) AssistChip(onClick = {}, label = { Text(movie?.ratingMpaa!!.uppercase()) }, colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, labelColor = MaterialTheme.colorScheme.onSurfaceVariant))
                if (movie?.movieLength != null) AssistChip(onClick = {}, label = { Text("${movie.movieLength} мин") }, colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, labelColor = MaterialTheme.colorScheme.onSurfaceVariant))
            }

            val ratingValue = movie?.rating?.kp ?: movie?.rating?.imdb
            if (ratingValue != null) {
                AssistChip(
                    onClick = {},
                    label = { Text(String.format("Рейтинг: %.1f", ratingValue)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        labelColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.constrainAs(ratingRef) {
                        top.linkTo(chipsRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                    }
                )
            }

            Text(
                text = movie?.description ?: "Описание отсутствует",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.constrainAs(descRef) {
                    top.linkTo(ratingRef.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
            )

            val hasCast = !movie?.persons.isNullOrEmpty()
            if (hasCast) {
                Column(
                    modifier = Modifier.constrainAs(castRef) {
                        top.linkTo(descRef.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                    }
                ) {
                    Text("Актеры", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(movie!!.persons) { p ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AsyncImage(
                                    model = p.photo,
                                    contentDescription = p.name,
                                    modifier = Modifier.size(64.dp).clip(CircleShape)
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(p.name, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            val all = viewModel.movies.value
            val similar = movie?.let { m ->
                val fromModel = m.similarMovies
                if (fromModel.isNotEmpty()) {
                    val byId = all.associateBy { it.id }
                    fromModel.mapNotNull { sm -> byId[sm.id] }.ifEmpty {
                        fromModel.map { sm ->
                            com.example.app.data.Movie(
                                id = sm.id,
                                name = sm.name,
                                alternativeName = sm.alternativeName,
                                year = sm.year,
                                description = null,
                                rating = sm.rating,
                                poster = sm.poster,
                                genres = emptyList(),
                                persons = emptyList()
                            )
                        }
                    }.take(3)
                } else {
                    val g = m.genres.map { it.name }.toSet()
                    val candidates = all.filter { it.id != m.id }
                    val byGenre = if (g.isNotEmpty()) candidates.filter { it.genres.any { it.name in g } } else emptyList()
                    (byGenre.ifEmpty { candidates }).take(3)
                }
            } ?: emptyList()

            if (similar.isNotEmpty()) {
                Column(
                    modifier = Modifier.constrainAs(similarRef) {
                        top.linkTo(if (hasCast) castRef.bottom else descRef.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                    }
                ) {
                    Text("Похожие фильмы", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    similar.forEach { sm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                                .clickable { navController.navigate("detail/${sm.id}") },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = sm.poster?.previewUrl ?: sm.poster?.url,
                                contentDescription = sm.name,
                                modifier = Modifier.size(56.dp).clip(MaterialTheme.shapes.small),
                                contentScale = ContentScale.Fit,
                                placeholder = painterResource(id = R.drawable.ic_movie_placeholder),
                                error = painterResource(id = R.drawable.ic_movie_placeholder)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(sm.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                                val meta = listOfNotNull(sm.alternativeName, sm.year?.toString()).joinToString(" • ")
                                if (meta.isNotBlank()) Text(meta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            val r = sm.rating?.kp ?: sm.rating?.imdb
                            if (r != null) AssistChip(onClick = {}, label = { Text(String.format("★ %.1f", r)) }, colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondary, labelColor = MaterialTheme.colorScheme.onSecondary))
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
