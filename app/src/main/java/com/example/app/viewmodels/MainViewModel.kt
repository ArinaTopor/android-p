package com.example.app.viewmodels

import androidx.lifecycle.ViewModel
import com.example.app.data.Movie
import com.example.app.data.Rating
import com.example.app.data.repository.SampleMoviesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MovieDetailsUiState(
    val movie: Movie? = null,
    val title: String = "",
    val alternativeTitle: String = "",
    val year: String = "",
    val description: String = "",
    val rating: String = "",
    val posterUrl: String? = null,
    val ageRating: String = "",
    val mpaaRating: String = "",
    val duration: String = "",
    val genres: String = "",
    val cast: List<CastMemberUi> = emptyList(),
    val similarMovies: List<SimilarMovieUi> = emptyList(),
    val hasCast: Boolean = false,
    val hasSimilar: Boolean = false
)

data class CastMemberUi(
    val id: Int,
    val name: String,
    val photoUrl: String?
)

data class SimilarMovieUi(
    val id: Int,
    val name: String,
    val alternativeName: String,
    val year: String,
    val rating: String,
    val posterUrl: String?,
    val onClick: () -> Unit
)

class MainViewModel : ViewModel() {
    private val _movies = MutableStateFlow(SampleMoviesRepository.movies)
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _movieDetailsState = MutableStateFlow(MovieDetailsUiState())
    val movieDetailsState: StateFlow<MovieDetailsUiState> = _movieDetailsState.asStateFlow()

    fun loadMovieDetails(movieId: Int, onMovieClick: (Int) -> Unit) {
        val movie = _movies.value.find { it.id == movieId }

        _movieDetailsState.update {
            prepareMovieDetailsUiState(movie, onMovieClick)
        }
    }

    private fun prepareMovieDetailsUiState(
        movie: Movie?,
        onMovieClick: (Int) -> Unit
    ): MovieDetailsUiState {
        if (movie == null) return MovieDetailsUiState()

        val ratingValue = movie.rating?.kp ?: movie.rating?.imdb
        val ratingText = if (ratingValue != null) String.format("%.1f", ratingValue) else ""

        val genresText = movie.genres.joinToString(", ") { it.name }

        val castMembers = movie.persons.take(10).map { person ->
            CastMemberUi(
                id = person.id,
                name = person.name,
                photoUrl = person.photo
            )
        }

        val similarMovies = prepareSimilarMovies(movie, onMovieClick)

        return MovieDetailsUiState(
            movie = movie,
            title = movie.name,
            alternativeTitle = movie.alternativeName ?: "",
            year = movie.year?.toString() ?: "",
            description = movie.description ?: "Описание отсутствует",
            rating = ratingText,
            posterUrl = movie.poster?.url ?: movie.poster?.previewUrl,
            ageRating = movie.ageRating?.let { "$it+" } ?: "",
            mpaaRating = movie.ratingMpaa?.uppercase() ?: "",
            duration = movie.movieLength?.let { "$it мин" } ?: "",
            genres = genresText,
            cast = castMembers,
            similarMovies = similarMovies,
            hasCast = castMembers.isNotEmpty(),
            hasSimilar = similarMovies.isNotEmpty()
        )
    }

    private fun prepareSimilarMovies(
        movie: Movie,
        onMovieClick: (Int) -> Unit
    ): List<SimilarMovieUi> {
        val allMovies = _movies.value

        val fromModel = movie.similarMovies.mapNotNull { similarMovie ->
            allMovies.find { it.id == similarMovie.id }?.let { foundMovie ->
                SimilarMovieUi(
                    id = foundMovie.id,
                    name = foundMovie.name,
                    alternativeName = foundMovie.alternativeName ?: "",
                    year = foundMovie.year?.toString() ?: "",
                    rating = prepareRatingText(foundMovie.rating),
                    posterUrl = foundMovie.poster?.previewUrl ?: foundMovie.poster?.url,
                    onClick = { onMovieClick(foundMovie.id) }
                )
            } ?: run {
                SimilarMovieUi(
                    id = similarMovie.id,
                    name = similarMovie.name,
                    alternativeName = similarMovie.alternativeName ?: "",
                    year = similarMovie.year?.toString() ?: "",
                    rating = prepareRatingText(similarMovie.rating),
                    posterUrl = similarMovie.poster?.previewUrl ?: similarMovie.poster?.url,
                    onClick = { onMovieClick(similarMovie.id) }
                )
            }
        }

        return if (fromModel.isNotEmpty()) {
            fromModel.take(3)
        } else {
            val targetGenres = movie.genres.map { it.name }.toSet()
            val candidates = allMovies
                .filter { it.id != movie.id }
                .filter { candidate ->
                    candidate.genres.any { it.name in targetGenres }
                }
                .take(3)
                .map { candidate ->
                    SimilarMovieUi(
                        id = candidate.id,
                        name = candidate.name,
                        alternativeName = candidate.alternativeName ?: "",
                        year = candidate.year?.toString() ?: "",
                        rating = prepareRatingText(candidate.rating),
                        posterUrl = candidate.poster?.previewUrl ?: candidate.poster?.url,
                        onClick = { onMovieClick(candidate.id) }
                    )
                }
            candidates
        }
    }

    private fun prepareRatingText(rating: Rating?): String {
        val value = rating?.kp ?: rating?.imdb
        return if (value != null) String.format("★ %.1f", value) else ""
    }
}