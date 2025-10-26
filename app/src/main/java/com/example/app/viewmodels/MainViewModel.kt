package com.example.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.Movie
import com.example.app.data.Rating
import com.example.app.domain.usecase.GetMoviesUseCase
import com.example.app.domain.usecase.GetMovieByIdUseCase
import com.example.app.domain.usecase.SearchMoviesUseCase
import com.example.app.data.repository.FilterRepository
import com.example.app.data.repository.FilterSettings
import com.example.app.data.local.FilterCache
import com.example.app.ui.state.MovieUiState
import com.example.app.ui.state.MovieDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getMovieByIdUseCase: GetMovieByIdUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val filterRepository: FilterRepository,
    private val filterCache: FilterCache
) : ViewModel() {

    private val _moviesUiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val moviesUiState: StateFlow<MovieUiState> = _moviesUiState.asStateFlow()
    private val _movieDetailUiState =
        MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val movieDetailUiState: StateFlow<MovieDetailUiState> = _movieDetailUiState.asStateFlow()
    private val _movieDetailsState = MutableStateFlow(MovieDetailsUiState())
    val movieDetailsState: StateFlow<MovieDetailsUiState> = _movieDetailsState.asStateFlow()
    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())
    private val _filteredMovies = MutableStateFlow<List<Movie>>(emptyList())

    init {
        viewModelScope.launch {
            combine(
                _allMovies,
                filterRepository.filterSettings
            ) { movies, filters ->
                applyFilters(movies, filters)
            }.collect { filteredMovies ->
                _filteredMovies.value = filteredMovies
            }
        }
    }

    val movies: StateFlow<List<Movie>> = _filteredMovies.asStateFlow()
    val hasActiveFilters: StateFlow<Boolean> = filterCache.hasActiveFilters

    fun loadMovies() {
        viewModelScope.launch {
            _moviesUiState.value = MovieUiState.Loading
            getMoviesUseCase().fold(
                onSuccess = { movies ->
                    _allMovies.value = movies
                    _moviesUiState.value = MovieUiState.Success(movies)
                },
                onFailure = { error ->
                    _moviesUiState.value = MovieUiState.Error(error.message ?: "Неизвестная ошибка")
                }
            )
        }
    }

    fun loadMovieById(movieId: Int) {
        viewModelScope.launch {
            _movieDetailUiState.value = MovieDetailUiState.Loading
            getMovieByIdUseCase(movieId).fold(
                onSuccess = { movie ->
                    _movieDetailUiState.value = MovieDetailUiState.Success(movie)
                    _movieDetailsState.update {
                        prepareMovieDetailsUiState(movie) { id ->
                            loadMovieById(id)
                        }
                    }
                },
                onFailure = { error ->
                    _movieDetailUiState.value =
                        MovieDetailUiState.Error(error.message ?: "Неизвестная ошибка")
                }
            )
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _moviesUiState.value = MovieUiState.Loading
            searchMoviesUseCase(query).fold(
                onSuccess = { movies ->
                    _allMovies.value = movies
                    _moviesUiState.value = MovieUiState.Success(movies)
                },
                onFailure = { error ->
                    _moviesUiState.value = MovieUiState.Error(error.message ?: "Ошибка поиска")
                }
            )
        }
    }

    fun loadMovieDetails(movieId: Int, onMovieClick: (Int) -> Unit) {
        loadMovieById(movieId)
    }

    private fun prepareMovieDetailsUiState(
        movie: Movie,
        onMovieClick: (Int) -> Unit
    ): MovieDetailsUiState {

        val ratingValue = movie.rating?.kp ?: movie.rating?.imdb
        val ratingText = if (ratingValue != null) String.format("%.1f", ratingValue) else ""

        val genresText = movie.genres.joinToString(", ") { it.name }

        val castMembers = movie.persons.take(10).map { person ->
            CastMemberUi(
                id = person.id,
                name = person.name ?: "Актер",
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
        val allMovies = _allMovies.value

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

    private fun applyFilters(movies: List<Movie>, filterSettings: FilterSettings): List<Movie> {
        return movies.filter { movie ->
            val genreMatch = filterSettings.genre.isEmpty() ||
                    movie.genres.any { genre ->
                        genre.name.contains(filterSettings.genre, ignoreCase = true)
                    }

            val ratingValue = movie.rating?.kp ?: movie.rating?.imdb
            val ratingMatch = filterSettings.minRating <= 0f ||
                    (ratingValue != null && ratingValue >= filterSettings.minRating)

            val nameMatch = filterSettings.query.isEmpty() ||
                    movie.name.contains(filterSettings.query, ignoreCase = true) ||
                    movie.alternativeName?.contains(filterSettings.query, ignoreCase = true) == true

            genreMatch && ratingMatch && nameMatch
        }
    }
}