package com.example.app.domain.usecase

import com.example.app.data.Movie
import com.example.app.data.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1, limit: Int = 10): Result<List<Movie>> {
        return repository.searchMovies(query, page, limit)
    }
}
