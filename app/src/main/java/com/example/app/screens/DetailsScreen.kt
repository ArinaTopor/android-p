package com.example.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.viewmodels.CastMemberUi
import com.example.app.viewmodels.MovieDetailsUiState
import com.example.app.viewmodels.SimilarMovieUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    itemId: Int,
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.movieDetailsState.collectAsStateWithLifecycle()

    LaunchedEffect(itemId) {
        viewModel.loadMovieDetails(itemId) { movieId ->
            navController.navigate("detail/$movieId")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        MovieDetailsContent(
            uiState = uiState,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun MovieDetailsContent(
    uiState: MovieDetailsUiState,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
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
                model = uiState.posterUrl,
                contentDescription = uiState.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        }

        Text(
            text = uiState.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(posterRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        val metaText = listOfNotNull(
            uiState.alternativeTitle.takeIf { it.isNotBlank() },
            uiState.year.takeIf { it.isNotBlank() }
        ).joinToString(" • ")

        if (metaText.isNotBlank()) {
            Text(
                text = metaText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.constrainAs(metaRef) {
                    top.linkTo(titleRef.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.constrainAs(chipsRef) {
                top.linkTo(metaRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
            }
        ) {
            if (uiState.ageRating.isNotBlank()) {
                InfoChip(text = uiState.ageRating)
            }
            if (uiState.mpaaRating.isNotBlank()) {
                InfoChip(text = uiState.mpaaRating)
            }
            if (uiState.duration.isNotBlank()) {
                InfoChip(text = uiState.duration)
            }
        }

        if (uiState.rating.isNotBlank()) {
            AssistChip(
                onClick = {},
                label = { Text("Рейтинг: ${uiState.rating}") },
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
            text = uiState.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.constrainAs(descRef) {
                top.linkTo(ratingRef.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = androidx.constraintlayout.compose.Dimension.fillToConstraints
            }
        )

        if (uiState.hasCast) {
            CastSection(
                cast = uiState.cast,
                modifier = Modifier.constrainAs(castRef) {
                    top.linkTo(descRef.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
            )
        }

        if (uiState.hasSimilar) {
            SimilarMoviesSection(
                similarMovies = uiState.similarMovies,
                modifier = Modifier.constrainAs(similarRef) {
                    top.linkTo(
                        if (uiState.hasCast) castRef.bottom else descRef.bottom,
                        margin = 16.dp
                    )
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
            )
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    AssistChip(
        onClick = {},
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
private fun CastSection(cast: List<CastMemberUi>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            "Актеры",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cast) { castMember ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = castMember.photoUrl,
                        contentDescription = castMember.name,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(id = R.drawable.ic_movie_placeholder),
                        error = painterResource(id = R.drawable.ic_movie_placeholder)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(castMember.name, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun SimilarMoviesSection(
    similarMovies: List<SimilarMovieUi>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            "Похожие фильмы",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        similarMovies.forEach { similarMovie ->
            SimilarMovieItem(
                similarMovie = similarMovie,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SimilarMovieItem(similarMovie: SimilarMovieUi, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
            .clickable { similarMovie.onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = similarMovie.posterUrl,
            contentDescription = similarMovie.name,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_movie_placeholder),
            error = painterResource(id = R.drawable.ic_movie_placeholder)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                similarMovie.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            val meta = listOfNotNull(
                similarMovie.alternativeName.takeIf { it.isNotBlank() },
                similarMovie.year.takeIf { it.isNotBlank() }
            ).joinToString(" • ")
            if (meta.isNotBlank()) {
                Text(
                    meta,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (similarMovie.rating.isNotBlank()) {
            AssistChip(
                onClick = {},
                label = { Text(similarMovie.rating) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    labelColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    }
}